# Swagger UI Kullanım Kılavuzu

## 📋 Swagger UI Nasıl Açılır?

### 1. Servisleri Derleyin

```bash
# Proje ana dizininde
mvn clean compile
```

### 2. Servisleri Başlatın

Her servisi ayrı bir terminalde başlatın:

```bash
# Terminal 1 - User Service
cd user-service
mvn spring-boot:run

# Terminal 2 - Product Service
cd product-service
mvn spring-boot:run

# Terminal 3 - Order Service
cd order-service
mvn spring-boot:run

# Terminal 4 - Inventory Service
cd inventory-service
mvn spring-boot:run

# Terminal 5 - Notification Service
cd notification-service
mvn spring-boot:run
```

**Not:** Servislerin başlaması birkaç saniye sürebilir. Loglarda `Started [ServiceName]Application` mesajını görünce servis hazırdır.

### 3. Tarayıcıda Swagger UI'yi Açın

Servisler başladıktan sonra, tarayıcınızda aşağıdaki URL'leri açın:

#### User Service
```
http://localhost:8081/swagger-ui.html
```

#### Product Service
```
http://localhost:8082/swagger-ui.html
```

#### Order Service
```
http://localhost:8083/swagger-ui.html
```

#### Inventory Service
```
http://localhost:8084/swagger-ui.html
```

#### Notification Service
```
http://localhost:8085/swagger-ui.html
```

## 🎯 Swagger UI'de Ne Yapabilirsiniz?

### 1. **API Endpoint'lerini Görüntüleme**
- Sol panelde tüm API endpoint'leri listelenir
- Her endpoint için HTTP method (GET, POST, PUT, DELETE) gösterilir
- Endpoint'ler tag'lere göre gruplandırılır

### 2. **API Detaylarını İnceleme**
- Endpoint'e tıklayarak detayları görebilirsiniz
- Request parametreleri, body şeması, response şeması görüntülenir
- Örnek request/response gösterilir

### 3. **API'leri Test Etme**
- "Try it out" butonuna tıklayın
- Request parametrelerini doldurun
- "Execute" butonuna tıklayın
- Response'u görüntüleyin

### 4. **OpenAPI JSON İndirme**
- Swagger UI'de "Download" butonuna tıklayarak OpenAPI JSON dosyasını indirebilirsiniz
- Veya direkt endpoint'ten: `http://localhost:8081/api-docs`

## 🔍 Örnek Kullanım

### User Service'te Kullanıcı Oluşturma

1. Swagger UI'yi açın: `http://localhost:8081/swagger-ui.html`
2. "User Controller" tag'ini bulun
3. "POST /users" endpoint'ine tıklayın
4. "Try it out" butonuna tıklayın
5. Request body'yi doldurun:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "Test123!@",
  "phoneNumber": "+905551234567"
}
```
6. "Execute" butonuna tıklayın
7. Response'u görüntüleyin

## 📝 Notlar

- **Servis Başlatma:** Servisler başlatıldıktan sonra birkaç saniye bekleyin
- **Port Kontrolü:** Servislerin doğru portlarda çalıştığından emin olun
- **Eureka:** Service Registry (Eureka) çalışıyor olmalı
- **PostgreSQL:** Veritabanı bağlantısı çalışıyor olmalı

## 🐛 Sorun Giderme

### Swagger UI Açılmıyor

1. **Servis çalışıyor mu kontrol edin:**
   ```bash
   # Health check
   curl http://localhost:8081/actuator/health
   ```

2. **Port çakışması var mı kontrol edin:**
   - Başka bir uygulama aynı portu kullanıyor olabilir
   - `application.yaml` dosyasındaki port numarasını kontrol edin

3. **Logları kontrol edin:**
   - Servis loglarında hata mesajları var mı bakın
   - PostgreSQL bağlantı hatası olabilir

### API Endpoint'leri Görünmüyor

- Controller sınıflarının `@RestController` annotation'ına sahip olduğundan emin olun
- `@RequestMapping` veya `@GetMapping`, `@PostMapping` gibi annotation'lar kullanıldığından emin olun

## 🎉 İpuçları

- **Hızlı Test:** Swagger UI'den direkt API'leri test edebilirsiniz, Postman'e gerek yok
- **Dokümantasyon:** Swagger UI otomatik olarak API dokümantasyonu oluşturur
- **Paylaşım:** OpenAPI JSON dosyasını paylaşarak frontend geliştiricilerle entegrasyon yapabilirsiniz

