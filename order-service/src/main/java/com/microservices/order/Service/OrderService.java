package com.microservices.order.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservices.order.Client.InventoryServiceClient;
import com.microservices.order.Client.ProductServiceClient;
import com.microservices.order.Client.UserServiceClient;
import com.microservices.order.Config.RabbitMQConfig;
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

    public OrderService(
            OrderRepository orderRepository,
            ProductServiceClient productServiceClient,
            InventoryServiceClient inventoryServiceClient,
            UserServiceClient userServiceClient,
            RabbitTemplate rabbitTemplate) {
        this.orderRepository = orderRepository;
        this.productServiceClient = productServiceClient;
        this.inventoryServiceClient = inventoryServiceClient;
        this.userServiceClient = userServiceClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Tüm siparişleri getir
     * Admin paneli için kullanılır
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * ID'ye göre sipariş getir
     */
    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
    }

    /**
     * User ID'ye göre siparişleri getir
     * Kullanıcının tüm siparişlerini listeler
     */
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
    public Order createOrder(Order order) {
        // 1. Kullanıcı doğrulama
        UserServiceClient.UserResponse user;
        try {
            user = userServiceClient.getUserById(order.getUserId());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("User", "id", order.getUserId());
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
    public Order updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        Order order = getOrderById(orderId);
        
        // Durum geçişi kontrolü
        validateStatusTransition(order.getStatus(), newStatus);
        
        // PENDING → CONFIRMED geçişinde stokları rezerve et
        if (order.getStatus() == OrderStatus.PENDING && newStatus == OrderStatus.CONFIRMED) {
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
        
        // RabbitMQ'ya OrderStatusChangedEvent gönder (asenkron)
        try {
            sendOrderStatusChangedEvent(savedOrder, oldStatus);
        } catch (Exception e) {
            // RabbitMQ hatası durum güncellemeyi engellemez
            System.err.println("Error sending OrderStatusChangedEvent: " + e.getMessage());
        }
        
        return savedOrder;
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
        
        // Eğer CONFIRMED durumundaysa, rezerve edilmiş stokları geri ver
        if (order.getStatus() == OrderStatus.CONFIRMED) {
            for (OrderItem item : order.getOrderItems()) {
                try {
                    // Inventory bilgisini çek
                    InventoryServiceClient.InventoryResponse inventory = 
                        inventoryServiceClient.getInventoryByProductId(item.getProductId());
                    
                    if (inventory != null) {
                        // Rezerve edilmiş stoku geri ver
                        inventoryServiceClient.releaseReservedStock(
                            inventory.getId(), 
                            item.getQuantity());
                    }
                } catch (Exception e) {
                    // Log hatası ama sipariş iptal işlemini durdurma
                    // Production'da logger kullanılmalı
                    System.err.println("Error releasing stock for product " + 
                        item.getProductId() + ": " + e.getMessage());
                }
            }
        }
        
        order.updateStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    /**
     * Sipariş sil
     */
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
        
        // Geçerli durum geçişleri (basit kontrol, gelecekte daha detaylı yapılabilir)
        // Şimdilik sadece CANCELLED ve DELIVERED kontrolü yapılıyor
    }
}

