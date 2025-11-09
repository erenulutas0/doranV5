package com.microservices.order.Event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Order Created Event
 * 
 * Sipariş oluşturulduğunda RabbitMQ'ya gönderilecek event
 * Notification Service bu event'i dinleyerek bildirim gönderecek
 * 
 * Event-Driven Architecture Pattern:
 * - Order Service: Event Producer (mesaj gönderen)
 * - Notification Service: Event Consumer (mesajı dinleyen)
 */
public class OrderCreatedEvent {
    
    private UUID orderId;
    private UUID userId;
    private String userEmail;
    private String userName;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String city;
    private String zipCode;
    private String phoneNumber;
    private LocalDateTime orderDate;
    private List<OrderItemInfo> orderItems;

    // Default constructor (Jackson için gerekli)
    public OrderCreatedEvent() {
    }

    // Constructor
    public OrderCreatedEvent(UUID orderId, UUID userId, String userEmail, String userName,
                           BigDecimal totalAmount, String shippingAddress, String city,
                           String zipCode, String phoneNumber, LocalDateTime orderDate,
                           List<OrderItemInfo> orderItems) {
        this.orderId = orderId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.city = city;
        this.zipCode = zipCode;
        this.phoneNumber = phoneNumber;
        this.orderDate = orderDate;
        this.orderItems = orderItems;
    }

    // Getters and Setters
    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public List<OrderItemInfo> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemInfo> orderItems) {
        this.orderItems = orderItems;
    }

    /**
     * OrderItem bilgileri için inner class
     */
    public static class OrderItemInfo {
        private UUID productId;
        private String productName;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal subtotal;

        public OrderItemInfo() {
        }

        public OrderItemInfo(UUID productId, String productName, Integer quantity, 
                           BigDecimal price, BigDecimal subtotal) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
            this.subtotal = subtotal;
        }

        // Getters and Setters
        public UUID getProductId() {
            return productId;
        }

        public void setProductId(UUID productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public BigDecimal getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(BigDecimal subtotal) {
            this.subtotal = subtotal;
        }
    }
}

