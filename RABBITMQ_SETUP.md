# RabbitMQ Kurulum ve Kullanım Kılavuzu

## RabbitMQ Kurulumu

RabbitMQ başarıyla kuruldu ve çalışıyor! 🎉

### Erişim Bilgileri

- **Management UI**: http://localhost:15672
- **Username**: `guest`
- **Password**: `guest`
- **AMQP Port**: `5672`
- **Management Port**: `15672`

### Container Durumu

```bash
# Container durumunu kontrol et
docker ps --filter "name=rabbitmq"

# Container loglarını görüntüle
docker logs rabbitmq

# Container'ı durdur
docker stop rabbitmq

# Container'ı başlat
docker start rabbitmq

# Container'ı sil
docker rm rabbitmq
```

## Queue'lar

Servisler başlatıldığında otomatik olarak oluşturulacak queue'lar:

1. **order.created** - Sipariş oluşturulduğunda mesaj gönderilir
2. **order.status.changed** - Sipariş durumu değiştiğinde mesaj gönderilir

## Test Senaryoları

### 1. Servisleri Başlatma

```bash
# Terminal 1: Service Registry
cd service-registry
mvn spring-boot:run

# Terminal 2: User Service
cd user-service
mvn spring-boot:run

# Terminal 3: Product Service
cd product-service
mvn spring-boot:run

# Terminal 4: Inventory Service
cd inventory-service
mvn spring-boot:run

# Terminal 5: Order Service
cd order-service
mvn spring-boot:run

# Terminal 6: Notification Service
cd notification-service
mvn spring-boot:run
```

### 2. Sipariş Oluşturma Testi

```bash
# POST /api/orders
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user-id",
    "shippingAddress": "Test Address, Test Street 123",
    "city": "Istanbul",
    "zipCode": "34000",
    "phoneNumber": "5551234567",
    "orderItems": [
      {
        "productId": "product-id",
        "quantity": 2
      }
    ]
  }'
```

### 3. RabbitMQ Management UI'da Kontrol

1. http://localhost:15672 adresine gidin
2. `guest` / `guest` ile giriş yapın
3. **Queues** sekmesine gidin
4. `order.created` ve `order.status.changed` queue'larını görün
5. Mesajların geldiğini kontrol edin

### 4. Notification Service Loglarını Kontrol

Notification Service loglarında şunları göreceksiniz:
- "Order created notification sent for order: ..."
- "Order status changed notification sent for order: ..."

## Troubleshooting

### RabbitMQ Bağlantı Hatası

Eğer servisler RabbitMQ'ya bağlanamazsa:

1. RabbitMQ container'ının çalıştığını kontrol edin:
   ```bash
   docker ps --filter "name=rabbitmq"
   ```

2. Port'ların açık olduğunu kontrol edin:
   ```bash
   netstat -an | findstr "5672"
   netstat -an | findstr "15672"
   ```

3. Application.yaml'da RabbitMQ ayarlarını kontrol edin:
   ```yaml
   spring:
     rabbitmq:
       host: localhost
       port: 5672
       username: guest
       password: guest
   ```

### Queue'lar Görünmüyor

Queue'lar ilk mesaj geldiğinde otomatik oluşturulur. Eğer görünmüyorsa:

1. Bir sipariş oluşturun
2. Management UI'da **Queues** sekmesini yenileyin
3. Queue'lar görünecektir

## Sonraki Adımlar

1. ✅ RabbitMQ kuruldu
2. ⏭️ Servisleri başlatıp test et
3. ⏭️ Management UI'da queue'ları kontrol et
4. ⏭️ End-to-end test yap
5. ⏭️ Error handling iyileştir (Dead Letter Queue, vb.)

