# RabbitMQ Test Kılavuzu

## ✅ RabbitMQ Kurulumu Tamamlandı!

- **RabbitMQ Version**: 3.13.7
- **Management UI**: http://localhost:15672
- **Username**: `guest`
- **Password**: `guest`
- **Queue'lar**: `order.created` ve `order.status.changed` hazır ve çalışıyor

## 🧪 Test Senaryosu

### Adım 1: Test Verileri Oluştur

#### 1.1 User Oluştur
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

**Response'dan `id` değerini alın → `USER_ID`**

#### 1.2 Product Oluştur
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

**Response'dan `id` değerini alın → `PRODUCT_ID`**

#### 1.3 Inventory Oluştur
```bash
POST http://localhost:8084/inventory
Content-Type: application/json

{
  "productId": "<PRODUCT_ID>",
  "quantity": 100,
  "minStockLevel": 10
}
```

### Adım 2: Order Oluştur (RabbitMQ Test)

```bash
POST http://localhost:8083/orders
Content-Type: application/json

{
  "userId": "<USER_ID>",
  "shippingAddress": "Test Address, Test Street 123",
  "city": "Istanbul",
  "zipCode": "34000",
  "phoneNumber": "5551234567",
  "orderItems": [
    {
      "productId": "<PRODUCT_ID>",
      "quantity": 2
    }
  ]
}
```

**Response'dan `id` değerini alın → `ORDER_ID`**

### Adım 3: RabbitMQ'da Mesajları Kontrol Et

1. **Management UI'ya gidin**: http://localhost:15672
2. **Queues** sekmesine gidin
3. **`order.created`** queue'suna tıklayın
4. **Get messages** sekmesine gidin
5. Mesajları görebilirsiniz!

**Beklenen:**
- `order.created` queue'sunda 1 mesaj görünecek
- Kısa süre sonra Notification Service mesajı işleyecek ve mesaj sayısı 0'a düşecek

### Adım 4: Notification Kontrolü

```bash
GET http://localhost:8085/notifications
```

Veya belirli order için:
```bash
GET http://localhost:8085/notifications/related?entityType=ORDER&entityId=<ORDER_ID>
```

**Beklenen:**
- Order oluşturulduğunda bir bildirim oluşturulmuş olmalı
- Subject: "Siparişiniz Oluşturuldu - #..."
- Status: SENT

### Adım 5: Order Durumu Değiştir (İkinci Test)

```bash
PATCH http://localhost:8083/orders/<ORDER_ID>/status?status=CONFIRMED
```

**Beklenen:**
- `order.status.changed` queue'sunda 1 mesaj görünecek
- Notification Service yeni bir bildirim oluşturacak
- Subject: "Sipariş Durumu Güncellendi - #..."

## 📊 RabbitMQ Management UI'da Ne Göreceksiniz?

### Queues Sekmesi
- **order.created**: Sipariş oluşturulduğunda mesajlar buraya gelir
- **order.status.changed**: Sipariş durumu değiştiğinde mesajlar buraya gelir

### Mesaj Detayları
1. Queue'ya tıklayın
2. **Get messages** sekmesine gidin
3. **Get Message(s)** butonuna tıklayın
4. Mesaj içeriğini görebilirsiniz:
   ```json
   {
     "orderId": "...",
     "userId": "...",
     "userEmail": "...",
     "totalAmount": 200.00,
     ...
   }
   ```

## 🎯 Başarı Kriterleri

✅ Order oluşturulduğunda:
- Order Service → RabbitMQ'ya mesaj gönderir
- `order.created` queue'sunda mesaj görünür
- Notification Service mesajı alır ve işler
- Bildirim oluşturulur

✅ Order durumu değiştiğinde:
- Order Service → RabbitMQ'ya mesaj gönderir
- `order.status.changed` queue'sunda mesaj görünür
- Notification Service mesajı alır ve işler
- Yeni bildirim oluşturulur

## 🔍 Debug İpuçları

### Mesajlar İşlenmiyor
1. Notification Service loglarını kontrol edin
2. RabbitMQ Management UI'da queue durumunu kontrol edin
3. Notification Service'in çalıştığından emin olun

### Queue'lar Görünmüyor
- Queue'lar ilk mesaj geldiğinde otomatik oluşturulur
- Order Service başlatıldığında queue'lar oluşturulur
- Management UI'da **Queues** sekmesini yenileyin

### Bağlantı Hatası
```bash
# RabbitMQ container durumu
docker ps --filter "name=rabbitmq"

# RabbitMQ logları
docker logs rabbitmq

# Port kontrolü
netstat -an | findstr "5672"
```

## 📝 Notlar

- RabbitMQ mesajları **asenkron** olarak işler
- Order Service mesaj gönderdikten sonra **beklemez**, hemen devam eder
- Notification Service arka planda mesajları işler
- Mesajlar **durable** (kalıcı) - RabbitMQ restart olsa bile kaybolmaz

