package com.microservices.order.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Timer.Sample;

import com.microservices.order.Client.InventoryServiceClient;
import com.microservices.order.Client.ProductServiceClient;
import com.microservices.order.Client.UserServiceClient;
import com.microservices.order.Config.RabbitMQConfig;
import com.microservices.order.Controller.OrderWebSocketController;
import com.microservices.order.DTO.OrderStatusUpdate;
import com.microservices.order.Event.OrderCreatedEvent;
import com.microservices.order.Event.OrderStatusChangedEvent;
import com.microservices.order.Exception.ResourceNotFoundException;

import feign.FeignException;
import com.microservices.order.Model.Order;
import com.microservices.order.Model.OrderItem;
import com.microservices.order.Model.OrderStatus;
import com.microservices.order.Repository.OrderRepository;

/**
 * Order Service
 * Sipariş yönetimi için business logic
 * 
 * Önemli Notlar:
 * - Sipariş oluşturulurken OrderItem'lar otomatik eklenir
 * - Toplam tutar otomatik hesaplanır (@PreUpdate)
 * - Sipariş durumu enum ile yönetilir
 * - Inventory Service ile stok kontrolü yapılır (Feign Client ile)
 * - Product Service ile ürün bilgileri çekilir (Feign Client ile)
 * - User Service ile kullanıcı doğrulama yapılır (Feign Client ile)
 */
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;
    private final InventoryServiceClient inventoryServiceClient;
    private final UserServiceClient userServiceClient;
    private final RabbitTemplate rabbitTemplate;
    private final OrderWebSocketController webSocketController;
    private final MeterRegistry meterRegistry;

    public OrderService(
            OrderRepository orderRepository,
            ProductServiceClient productServiceClient,
            InventoryServiceClient inventoryServiceClient,
            UserServiceClient userServiceClient,
            RabbitTemplate rabbitTemplate,
            OrderWebSocketController webSocketController,
            MeterRegistry meterRegistry) {
        this.orderRepository = orderRepository;
        this.productServiceClient = productServiceClient;
        this.inventoryServiceClient = inventoryServiceClient;
        this.userServiceClient = userServiceClient;
        this.rabbitTemplate = rabbitTemplate;
        this.webSocketController = webSocketController;
        this.meterRegistry = meterRegistry;
    }

    /**
     * Tüm siparişleri getir
     * Admin paneli için kullanılır
     */
    @Cacheable(value = "orders", key = "'all'")
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * ID'ye göre sipariş getir
     */
    @Cacheable(value = "orders", key = "#orderId.toString()")
    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
    }

    /**
     * User ID'ye göre siparişleri getir
     * Kullanıcının tüm siparişlerini listeler
     */
    @Cacheable(value = "orders", key = "'user:' + #userId.toString()")
    public List<Order> getOrdersByUserId(UUID userId) {
        return orderRepository.findByUserId(userId);
    }

    /**
     * Sipariş durumuna göre filtrele
     */
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * Kullanıcının belirli durumdaki siparişlerini getir
     */
    public List<Order> getOrdersByUserIdAndStatus(UUID userId, OrderStatus status) {
        return orderRepository.findByUserIdAndStatus(userId, status);
    }

    /**
     * Yeni sipariş oluştur
     * 
     * ÖNEMLİ:
     * - OrderItem'lar otomatik olarak Order'a eklenir (addOrderItem metodu ile)
     * - Toplam tutar otomatik hesaplanır (@PreUpdate)
     * - Sipariş durumu PENDING olarak başlar
     * 
     * İşlem Adımları:
     * 1. User Service ile kullanıcı doğrulama
     * 2. Product Service ile ürün bilgilerini çek (snapshot için)
     * 3. Inventory Service ile stok kontrolü
     * 4. OrderItem'ları oluştur (ürün bilgileri ile)
     * 5. Siparişi kaydet
     */
    @Transactional
    @CacheEvict(value = "orders", key = "'user:' + #order.userId.toString()")  // Kullanıcının sipariş listesi cache'ini temizle
    public Order createOrder(Order order) {
        Sample sample = Timer.start(meterRegistry);
        // 1. Kullanıcı doğrulama
        UserServiceClient.UserResponse user;
        try {
            user = userServiceClient.getUserById(order.getUserId());
        } catch (FeignException.NotFound e) {
            meterRegistry.counter("orders.created.fail", "exception", "UserNotFound").increment();
            throw new ResourceNotFoundException("User", "id", order.getUserId());
        } catch (RuntimeException e) {
            meterRegistry.counter("orders.created.fail", "exception", e.getClass().getSimpleName()).increment();
            sample.stop(Timer.builder("orders.created.duration").register(meterRegistry));
            throw e;
        }
        
        if (user == null) {
            throw new ResourceNotFoundException("User", "id", order.getUserId());
        }
        
        // Eğer adres bilgileri gönderilmediyse, kullanıcının default adresini kullan
        if (order.getShippingAddress() == null || order.getShippingAddress().isEmpty()) {
            order.setShippingAddress(user.getAddress());
            order.setCity(user.getCity());
            order.setZipCode(user.getZip());
            order.setPhoneNumber(user.getPhone());
        }
        
        // 2. Stok kontrolü ve ürün bilgilerini çek
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        
        // Stok kontrolü için Map oluştur
        Map<UUID, Integer> stockCheckRequest = new HashMap<>();
        Map<UUID, ProductServiceClient.ProductResponse> productMap = new HashMap<>();
        Map<UUID, InventoryServiceClient.InventoryResponse> inventoryMap = new HashMap<>();
        
        for (OrderItem item : order.getOrderItems()) {
            UUID productId = item.getProductId();
            Integer quantity = item.getQuantity();
            
            // Ürün bilgilerini çek
            ProductServiceClient.ProductResponse product;
            try {
                product = productServiceClient.getProductById(productId);
            } catch (FeignException.NotFound e) {
                meterRegistry.counter("orders.created.fail", "exception", "ProductNotFound").increment();
                sample.stop(Timer.builder("orders.created.duration").register(meterRegistry));
                throw new ResourceNotFoundException("Product", "id", productId);
            }
            if (product == null) {
                throw new ResourceNotFoundException("Product", "id", productId);
            }
            productMap.put(productId, product);
            
            // Stok bilgisini çek
            InventoryServiceClient.InventoryResponse inventory;
            try {
                inventory = inventoryServiceClient.getInventoryByProductId(productId);
            } catch (FeignException.NotFound e) {
                meterRegistry.counter("orders.created.fail", "exception", "InventoryNotFound").increment();
                sample.stop(Timer.builder("orders.created.duration").register(meterRegistry));
                throw new ResourceNotFoundException("Inventory", "productId", productId);
            }
            if (inventory == null) {
                throw new ResourceNotFoundException("Inventory", "productId", productId);
            }
            inventoryMap.put(productId, inventory);
            
            // Stok kontrolü için ekle
            stockCheckRequest.put(productId, quantity);
        }
        
        // Toplu stok kontrolü
        Map<UUID, Boolean> stockAvailability = inventoryServiceClient.checkStockAvailability(stockCheckRequest);
        
        // Stok kontrolü sonuçlarını kontrol et
        for (Map.Entry<UUID, Boolean> entry : stockAvailability.entrySet()) {
            UUID productId = entry.getKey();
            Boolean available = entry.getValue();
            
            if (!available) {
                InventoryServiceClient.InventoryResponse inventory = inventoryMap.get(productId);
                Integer requestedQuantity = stockCheckRequest.get(productId);
                Integer availableQuantity = inventory.getAvailableQuantity();
                
                throw new IllegalArgumentException(
                    String.format("Insufficient stock for product %s. Requested: %d, Available: %d",
                        productId, requestedQuantity, availableQuantity));
            }
        }
        
        // 3. OrderItem'ları oluştur (ürün bilgileri ile snapshot)
        List<OrderItem> itemsToAdd = new ArrayList<>(order.getOrderItems());
        order.getOrderItems().clear();
        
        for (OrderItem item : itemsToAdd) {
            UUID productId = item.getProductId();
            ProductServiceClient.ProductResponse product = productMap.get(productId);
            
            // Snapshot: Ürün bilgilerini OrderItem'a kaydet
            item.setProductName(product.getName());
            if (item.getPrice() == null) {
                item.setPrice(product.getPrice());
            }
            
            // OrderItem'ı Order'a ekle
            order.addOrderItem(item);
        }
        
        // 4. Toplam tutarı hesapla
        order.setTotalAmount(order.calculateTotalAmount());
        
        // 5. Siparişi kaydet
        Order savedOrder = orderRepository.save(order);
        meterRegistry.counter("orders.created.count").increment();
        
        // 6. RabbitMQ'ya OrderCreatedEvent gönder (asenkron)
        // OrderItems'ı initialize etmek için order'ı yeniden fetch et
        try {
            Order orderForEvent = orderRepository.findById(savedOrder.getId())
                .orElse(savedOrder);
            sendOrderCreatedEvent(orderForEvent, user);
        } catch (Exception e) {
            // RabbitMQ hatası sipariş oluşturmayı engellemez
            // Log'a yazılmalı (production'da logger kullanılmalı)
            System.err.println("Error sending OrderCreatedEvent: " + e.getMessage());
            e.printStackTrace();
        }
        sample.stop(Timer.builder("orders.created.duration").register(meterRegistry));
        return savedOrder;
    }
    
    /**
     * OrderCreatedEvent gönder
     * RabbitMQ'ya asenkron mesaj gönderir
     */
    private void sendOrderCreatedEvent(Order order, UserServiceClient.UserResponse user) {
        // OrderItem bilgilerini event için hazırla
        // Lazy loading hatası önlemek için orderItems'ı yeni bir listeye kopyala
        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems == null) {
            orderItems = new ArrayList<>();
        }
        
        List<OrderCreatedEvent.OrderItemInfo> orderItemInfos = new ArrayList<>();
        for (OrderItem item : orderItems) {
            orderItemInfos.add(new OrderCreatedEvent.OrderItemInfo(
                item.getProductId(),
                item.getProductName(),
                item.getQuantity(),
                item.getPrice(),
                item.getSubtotal()
            ));
        }
        
        // Event oluştur
        OrderCreatedEvent event = new OrderCreatedEvent(
            order.getId(),
            order.getUserId(),
            user.getEmail(),
            user.getFirstName() + " " + user.getLastName(),
            order.getTotalAmount(),
            order.getShippingAddress(),
            order.getCity(),
            order.getZipCode(),
            order.getPhoneNumber(),
            order.getOrderDate(),
            orderItemInfos
        );
        
        // RabbitMQ'ya gönder (Exchange ve Routing Key kullanarak)
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.ORDER_EXCHANGE,
            RabbitMQConfig.ROUTING_KEY_CREATED,
            event
        );
    }

    /**
     * Sipariş durumunu güncelle
     * 
     * Örnek durum geçişleri:
     * - PENDING → CONFIRMED (stok kontrolü yapıldı, stoklar rezerve edilir)
     * - CONFIRMED → PROCESSING (hazırlanıyor)
     * - PROCESSING → SHIPPED (kargoya verildi)
     * - SHIPPED → DELIVERED (teslim edildi)
     * - Herhangi bir durum → CANCELLED (iptal edildi)
     */
    @Transactional
    @CacheEvict(value = "orders", key = "#orderId.toString()")  // Bu siparişin cache'ini temizle
    public Order updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        Order order = getOrderById(orderId);
        
        // Durum geçişi kontrolü
        validateStatusTransition(order.getStatus(), newStatus);
        
        // PENDING/PAYMENT_PENDING → CONFIRMED geçişinde stokları rezerve et
        if ((order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.PAYMENT_PENDING)
                && newStatus == OrderStatus.CONFIRMED) {
            for (OrderItem item : order.getOrderItems()) {
                try {
                    // Inventory bilgisini çek
                    InventoryServiceClient.InventoryResponse inventory = 
                        inventoryServiceClient.getInventoryByProductId(item.getProductId());
                    
                    if (inventory != null) {
                        // Stokları rezerve et
                        inventoryServiceClient.reserveStock(
                            inventory.getId(), 
                            item.getQuantity());
                    }
                } catch (Exception e) {
                    // Log hatası ama sipariş onay işlemini durdurma
                    // Production'da logger kullanılmalı
                    System.err.println("Error reserving stock for product " + 
                        item.getProductId() + ": " + e.getMessage());
                }
            }
        }
        
        OrderStatus oldStatus = order.getStatus();
        order.updateStatus(newStatus);
        Order savedOrder = orderRepository.save(order);
        meterRegistry.counter("orders.status.change.count", "to", newStatus.name()).increment();

        // Success/failure funnels
        if (newStatus == OrderStatus.DELIVERED) {
            meterRegistry.counter("orders.delivered.count").increment();
        } else if (newStatus == OrderStatus.CANCELLED) {
            meterRegistry.counter("orders.cancelled.count").increment();
        } else if (newStatus == OrderStatus.PAYMENT_FAILED) {
            meterRegistry.counter("orders.payment.fail.count").increment();
        } else if (newStatus == OrderStatus.PAYMENT_PENDING) {
            meterRegistry.counter("orders.payment.pending.count").increment();
        } else if (newStatus == OrderStatus.REFUND_REQUESTED) {
            meterRegistry.counter("orders.refund.request.count").increment();
        } else if (newStatus == OrderStatus.REFUNDED) {
            meterRegistry.counter("orders.refunded.count").increment();
        }
        
        // RabbitMQ'ya OrderStatusChangedEvent gönder (asenkron)
        try {
            sendOrderStatusChangedEvent(savedOrder, oldStatus);
        } catch (Exception e) {
            // RabbitMQ hatası durum güncellemeyi engellemez
            System.err.println("Error sending OrderStatusChangedEvent: " + e.getMessage());
        }
        
        // WebSocket'e real-time update gönder
        try {
            OrderStatusUpdate statusUpdate = new OrderStatusUpdate(
                savedOrder.getId(),
                oldStatus,
                newStatus,
                savedOrder.getUserId()
            );
            
            // Belirli sipariş için update gönder
            webSocketController.sendOrderStatusUpdate(savedOrder.getId(), statusUpdate);
            
            // Kullanıcının tüm siparişleri için update gönder
            webSocketController.sendUserOrderUpdate(savedOrder.getUserId(), statusUpdate);
        } catch (Exception e) {
            // WebSocket hatası durum güncellemeyi engellemez
            System.err.println("Error sending WebSocket update: " + e.getMessage());
        }
        
        return savedOrder;
    }

    /**
     * Ödeme başlatıldı → PAYMENT_PENDING
     */
    @Transactional
    @CacheEvict(value = "orders", key = "#orderId.toString()")
    public Order markPaymentPending(UUID orderId) {
        Order order = getOrderById(orderId);
        OrderStatus previous = order.getStatus();
        validateStatusTransition(order.getStatus(), OrderStatus.PAYMENT_PENDING);
        order.updateStatus(OrderStatus.PAYMENT_PENDING);
        Order saved = orderRepository.save(order);
        meterRegistry.counter("orders.status.change.count", "to", OrderStatus.PAYMENT_PENDING.name()).increment();
        sendOrderStatusChangedEventSafe(saved, previous);
        return saved;
    }

    /**
     * Ödeme başarılı → CONFIRMED + stok rezervasyonu
     */
    @Transactional
    @CacheEvict(value = "orders", key = "#orderId.toString()")
    public Order markPaymentSuccess(UUID orderId) {
        Order order = getOrderById(orderId);
        OrderStatus previous = order.getStatus();
        validateStatusTransition(order.getStatus(), OrderStatus.CONFIRMED);

        // Rezervasyon
        for (OrderItem item : order.getOrderItems()) {
            try {
                InventoryServiceClient.InventoryResponse inventory =
                    inventoryServiceClient.getInventoryByProductId(item.getProductId());
                if (inventory != null) {
                    inventoryServiceClient.reserveStock(inventory.getId(), item.getQuantity());
                }
            } catch (Exception e) {
                System.err.println("Error reserving stock (payment success) for product " +
                        item.getProductId() + ": " + e.getMessage());
            }
        }

        order.updateStatus(OrderStatus.CONFIRMED);
        Order saved = orderRepository.save(order);
        meterRegistry.counter("orders.status.change.count", "to", OrderStatus.CONFIRMED.name()).increment();
        meterRegistry.counter("orders.payment.success.count").increment();
        sendOrderStatusChangedEventSafe(saved, previous);
        return saved;
    }

    /**
     * Ödeme başarısız → PAYMENT_FAILED + stok serbest
     */
    @Transactional
    @CacheEvict(value = "orders", key = "#orderId.toString()")
    public Order markPaymentFailed(UUID orderId) {
        Order order = getOrderById(orderId);
        OrderStatus previous = order.getStatus();
        validateStatusTransition(order.getStatus(), OrderStatus.PAYMENT_FAILED);

        releaseStock(order);

        order.updateStatus(OrderStatus.PAYMENT_FAILED);
        Order saved = orderRepository.save(order);
        meterRegistry.counter("orders.status.change.count", "to", OrderStatus.PAYMENT_FAILED.name()).increment();
        meterRegistry.counter("orders.payment.fail.count").increment();
        sendOrderStatusChangedEventSafe(saved, previous);
        return saved;
    }

    /**
     * İade talebi → REFUND_REQUESTED
     */
    @Transactional
    @CacheEvict(value = "orders", key = "#orderId.toString()")
    public Order requestRefund(UUID orderId) {
        Order order = getOrderById(orderId);
        OrderStatus previous = order.getStatus();
        validateStatusTransition(order.getStatus(), OrderStatus.REFUND_REQUESTED);
        order.updateStatus(OrderStatus.REFUND_REQUESTED);
        Order saved = orderRepository.save(order);
        meterRegistry.counter("orders.status.change.count", "to", OrderStatus.REFUND_REQUESTED.name()).increment();
        sendOrderStatusChangedEventSafe(saved, previous);
        return saved;
    }

    /**
     * İade onayı → REFUNDED + stok serbest
     */
    @Transactional
    @CacheEvict(value = "orders", key = "#orderId.toString()")
    public Order approveRefund(UUID orderId) {
        Order order = getOrderById(orderId);
        OrderStatus previous = order.getStatus();
        validateStatusTransition(order.getStatus(), OrderStatus.REFUNDED);

        releaseStock(order);

        order.updateStatus(OrderStatus.REFUNDED);
        Order saved = orderRepository.save(order);
        meterRegistry.counter("orders.status.change.count", "to", OrderStatus.REFUNDED.name()).increment();
        meterRegistry.counter("orders.refunded.count").increment();
        sendOrderStatusChangedEventSafe(saved, previous);
        return saved;
    }

    private void sendOrderStatusChangedEventSafe(Order savedOrder, OrderStatus oldStatus) {
        try {
            sendOrderStatusChangedEvent(savedOrder, oldStatus);
        } catch (Exception e) {
            System.err.println("Error sending OrderStatusChangedEvent: " + e.getMessage());
        }
    }

    private void releaseStock(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            try {
                InventoryServiceClient.InventoryResponse inventory =
                    inventoryServiceClient.getInventoryByProductId(item.getProductId());
                if (inventory != null) {
                    inventoryServiceClient.releaseReservedStock(inventory.getId(), item.getQuantity());
                }
            } catch (Exception e) {
                System.err.println("Error releasing stock for product " +
                        item.getProductId() + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * OrderStatusChangedEvent gönder
     * RabbitMQ'ya asenkron mesaj gönderir
     */
    private void sendOrderStatusChangedEvent(Order order, OrderStatus oldStatus) {
        // Kullanıcı bilgilerini çek
        UserServiceClient.UserResponse user;
        try {
            user = userServiceClient.getUserById(order.getUserId());
        } catch (Exception e) {
            // Kullanıcı bilgisi alınamazsa event gönderilmez
            System.err.println("Error getting user for OrderStatusChangedEvent: " + e.getMessage());
            return;
        }
        
        if (user == null) {
            return;
        }
        
        // Event oluştur
        OrderStatusChangedEvent event = new OrderStatusChangedEvent(
            order.getId(),
            order.getUserId(),
            user.getEmail(),
            user.getFirstName() + " " + user.getLastName(),
            oldStatus != null ? oldStatus.name() : "UNKNOWN",
            order.getStatus().name(),
            LocalDateTime.now()
        );
        
        // RabbitMQ'ya gönder (Exchange ve Routing Key kullanarak)
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.ORDER_EXCHANGE,
            RabbitMQConfig.ROUTING_KEY_STATUS_CHANGED,
            event
        );
    }

    /**
     * Sipariş güncelle
     * Partial update yapıyor (null olmayan field'ları günceller)
     */
    @CacheEvict(value = "orders", key = "#orderId.toString()")  // Bu siparişin cache'ini temizle
    public Order updateOrder(UUID orderId, Order orderDetails) {
        Order order = getOrderById(orderId);
        
        // Sadece PENDING durumundaki siparişler güncellenebilir
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException(
                "Order can only be updated when status is PENDING. Current status: " + order.getStatus());
        }
        
        // Güncelleme
        if (orderDetails.getShippingAddress() != null) {
            order.setShippingAddress(orderDetails.getShippingAddress());
        }
        if (orderDetails.getCity() != null) {
            order.setCity(orderDetails.getCity());
        }
        if (orderDetails.getZipCode() != null) {
            order.setZipCode(orderDetails.getZipCode());
        }
        if (orderDetails.getPhoneNumber() != null) {
            order.setPhoneNumber(orderDetails.getPhoneNumber());
        }
        if (orderDetails.getNotes() != null) {
            order.setNotes(orderDetails.getNotes());
        }
        
        // OrderItem'ları güncelle (eğer gönderildiyse)
        if (orderDetails.getOrderItems() != null && !orderDetails.getOrderItems().isEmpty()) {
            // Mevcut OrderItem'ları temizle
            order.getOrderItems().clear();
            // Yeni OrderItem'ları ekle
            for (OrderItem item : orderDetails.getOrderItems()) {
                order.addOrderItem(item);
            }
            // Toplam tutarı yeniden hesapla
            order.setTotalAmount(order.calculateTotalAmount());
        }
        
        return orderRepository.save(order);
    }

    /**
     * Sipariş iptal et
     * 
     * ÖNEMLİ:
     * - Sadece PENDING veya CONFIRMED durumundaki siparişler iptal edilebilir
     * - İptal edildiğinde stok geri verilir (Inventory Service ile)
     */
    @Transactional
    public Order cancelOrder(UUID orderId) {
        Order order = getOrderById(orderId);
        
        // Sadece belirli durumlardaki siparişler iptal edilebilir
        if (order.getStatus() == OrderStatus.SHIPPED || 
            order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalArgumentException(
                "Cannot cancel order with status: " + order.getStatus() + 
                ". Only PENDING or CONFIRMED orders can be cancelled.");
        }
        
        // Eğer zaten iptal edilmişse
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException(
                "Order is already cancelled.");
        }
        
        // CONFIRMED veya PAYMENT_PENDING/FAILED durumlarında stok serbest bırak
        if (order.getStatus() == OrderStatus.CONFIRMED
                || order.getStatus() == OrderStatus.PAYMENT_PENDING
                || order.getStatus() == OrderStatus.PAYMENT_FAILED) {
            releaseStock(order);
        }
        
        order.updateStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    /**
     * Sipariş sil
     */
    @CacheEvict(value = "orders", key = "#orderId.toString()")  // Bu siparişin cache'ini temizle
    public void deleteOrder(UUID orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order", "id", orderId);
        }
        orderRepository.deleteById(orderId);
    }

    /**
     * Durum geçişi validasyonu
     * Geçerli durum geçişlerini kontrol eder
     */
    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // CANCELLED durumundan başka duruma geçilemez
        if (currentStatus == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot change status from CANCELLED");
        }
        
        // DELIVERED durumundan başka duruma geçilemez
        if (currentStatus == OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("Cannot change status from DELIVERED");
        }

        // REFUNDED terminal
        if (currentStatus == OrderStatus.REFUNDED) {
            throw new IllegalArgumentException("Cannot change status from REFUNDED");
        }

        // PAYMENT_FAILED → sadece CANCELLED'a izin ver
        if (currentStatus == OrderStatus.PAYMENT_FAILED && newStatus != OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Only cancellation allowed after PAYMENT_FAILED");
        }

        // PAYMENT_PENDING yalnızca PENDING'den
        if (newStatus == OrderStatus.PAYMENT_PENDING) {
            if (currentStatus != OrderStatus.PENDING) {
                throw new IllegalArgumentException("PAYMENT_PENDING only allowed from PENDING");
            }
        }

        // CONFIRMED yalnızca PENDING veya PAYMENT_PENDING'den
        if (newStatus == OrderStatus.CONFIRMED) {
            if (currentStatus != OrderStatus.PENDING && currentStatus != OrderStatus.PAYMENT_PENDING) {
                throw new IllegalArgumentException("CONFIRMED only allowed from PENDING or PAYMENT_PENDING");
            }
        }

        // PAYMENT_FAILED yalnızca PENDING veya PAYMENT_PENDING'den
        if (newStatus == OrderStatus.PAYMENT_FAILED) {
            if (currentStatus != OrderStatus.PENDING && currentStatus != OrderStatus.PAYMENT_PENDING) {
                throw new IllegalArgumentException("PAYMENT_FAILED only allowed from PENDING or PAYMENT_PENDING");
            }
        }

        // REFUND_REQUESTED yalnızca kargolama sonrası/öncesi belirli durumlar
        if (newStatus == OrderStatus.REFUND_REQUESTED) {
            if (currentStatus != OrderStatus.SHIPPED && currentStatus != OrderStatus.DELIVERED &&
                currentStatus != OrderStatus.PROCESSING && currentStatus != OrderStatus.CONFIRMED) {
                throw new IllegalArgumentException("REFUND_REQUESTED allowed after processing/shipped/delivered/confirmed");
            }
        }

        // REFUNDED yalnızca REFUND_REQUESTED veya ilgili ileri durumlar
        if (newStatus == OrderStatus.REFUNDED) {
            if (currentStatus != OrderStatus.REFUND_REQUESTED && currentStatus != OrderStatus.SHIPPED
                    && currentStatus != OrderStatus.DELIVERED && currentStatus != OrderStatus.PROCESSING
                    && currentStatus != OrderStatus.CONFIRMED) {
                throw new IllegalArgumentException("REFUNDED allowed after refund request or shipped/delivered");
            }
        }
    }
}

