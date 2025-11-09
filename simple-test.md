# RabbitMQ Test Kılavuzu

## Servislerin Durumu

Servislerin çalıştığını kontrol edin:
- Service Registry: http://localhost:8761
- API Gateway: http://localhost:8080
- User Service: http://localhost:8081
- Product Service: http://localhost:8082
- Order Service: http://localhost:8083
- Inventory Service: http://localhost:8084
- Notification Service: http://localhost:8085

## Manuel Test Adımları

### 1. RabbitMQ Management UI'da Queue'ları Kontrol Edin

1. http://localhost:15672 adresine gidin
2. `guest` / `guest` ile giriş yapın
3. **Queues** sekmesine gidin
4. `order.created` ve `order.status.changed` queue'larını görmelisiniz

### 2. Postman veya curl ile Test

#### A. User Oluştur (User Service)
```bash
POST http://localhost:8081/users
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "Test123!@#",
  "firstName": "Test",
  "lastName": "User",
  "phone": "5551234567",
  "address": "Test Address 123",
  "city": "Istanbul",
  "state": "IST",
  "zip": "34000"
}
```

#### B. Product Oluştur (Product Service)
```bash
POST http://localhost:8082/products
Content-Type: application/json

{
  "name": "Test Product",
  "description": "Test Description",
  "price": 100.00,
  "category": "Test"
}
```

#### C. Inventory Oluştur (Inventory Service)
```bash
POST http://localhost:8084/inventory
Content-Type: application/json

{
  "productId": "<product-id-from-step-b>",
  "quantity": 100,
  "minStockLevel": 10
}
```

#### D. Order Oluştur (Order Service - RabbitMQ Test)
```bash
POST http://localhost:8083/orders
Content-Type: application/json

{
  "userId": "<user-id-from-step-a>",
  "shippingAddress": "Test Address, Test Street 123",
  "city": "Istanbul",
  "zipCode": "34000",
  "phoneNumber": "5551234567",
  "orderItems": [
    {
      "productId": "<product-id-from-step-b>",
      "quantity": 2
    }
  ]
}
```

### 3. RabbitMQ'da Mesajları Kontrol Edin

1. Management UI'da **Queues** sekmesine gidin
2. `order.created` queue'suna tıklayın
3. **Get messages** sekmesine gidin
4. Mesajları görebilirsiniz

### 4. Notification Service'de Bildirimleri Kontrol Edin

```bash
GET http://localhost:8085/notifications
```

Veya Management UI'da:
- `order.created` queue'sunda mesaj sayısının azaldığını göreceksiniz
- Notification Service mesajı işledi ve bildirim oluşturdu

## Beklenen Sonuçlar

1. ✅ Order oluşturulduğunda:
   - Order Service → RabbitMQ'ya `order.created` mesajı gönderir
   - Notification Service → Mesajı alır ve bildirim oluşturur
   - Management UI'da `order.created` queue'sunda mesaj görünür (kısa süre sonra işlenir)

2. ✅ Order durumu değiştiğinde:
   - Order Service → RabbitMQ'ya `order.status.changed` mesajı gönderir
   - Notification Service → Mesajı alır ve bildirim oluşturur

## Troubleshooting

### Queue'lar Görünmüyor
- Servislerin başlatıldığından emin olun
- Order Service ve Notification Service'in çalıştığını kontrol edin
- Queue'lar ilk mesaj geldiğinde otomatik oluşturulur

### Mesajlar İşlenmiyor
- Notification Service'in çalıştığını kontrol edin
- Notification Service loglarını kontrol edin
- RabbitMQ Management UI'da queue'ların durumunu kontrol edin

### Bağlantı Hatası
- RabbitMQ container'ının çalıştığını kontrol edin: `docker ps --filter "name=rabbitmq"`
- Port'ların açık olduğunu kontrol edin: `netstat -an | findstr "5672"`

