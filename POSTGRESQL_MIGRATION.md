# PostgreSQL Migration Rehberi

Bu rehber, microservices projesini H2 in-memory database'den PostgreSQL'e geçirmek için adımları içerir.

## 📋 Adımlar

### 1. PostgreSQL Veritabanlarını Oluşturma

**pgAdmin ile:**

1. pgAdmin'i açın
2. PostgreSQL 17 server'ına bağlanın
3. **Tools > Query Tool** menüsünden Query Tool'u açın
4. `create-databases.sql` dosyasını açın ve içeriğini Query Tool'a yapıştırın
5. **F5** tuşuna basarak script'i çalıştırın

**Alternatif (psql ile):**

```bash
psql -U postgres -f create-databases.sql
```

**Oluşturulacak Veritabanları:**
- `user_db` - User Service için
- `product_db` - Product Service için
- `order_db` - Order Service için
- `inventory_db` - Inventory Service için
- `notification_db` - Notification Service için

### 2. PostgreSQL Bağlantı Bilgilerini Güncelleme

Her servisin `application.yaml` dosyasında PostgreSQL şifresini güncelleyin:

```yaml
spring:
  datasource:
    username: postgres  # PostgreSQL kullanıcı adı
    password: postgres  # ⚠️ KENDİ ŞİFRENİZİ GİRİN
```

**Güncellenecek Dosyalar:**
- `user-service/src/main/resources/application.yaml`
- `product-service/src/main/resources/application.yaml`
- `order-service/src/main/resources/application.yaml`
- `inventory-service/src/main/resources/application.yaml`
- `notification-service/src/main/resources/application.yaml`

### 3. Maven Dependencies Güncelleme

Tüm servislerin `pom.xml` dosyalarına PostgreSQL dependency eklendi. H2 dependency test scope'una taşındı.

**Yapılan Değişiklikler:**
- ✅ PostgreSQL dependency eklendi (runtime scope)
- ✅ H2 dependency test scope'una taşındı (sadece testlerde kullanılacak)

### 4. Servisleri Derleme ve Başlatma

```bash
# Her servisi derle
cd user-service
mvn clean compile

cd ../product-service
mvn clean compile

cd ../order-service
mvn clean compile

cd ../inventory-service
mvn clean compile

cd ../notification-service
mvn clean compile
```

**Servisleri Başlatma:**

Her servis başlatıldığında:
1. PostgreSQL'e bağlanacak
2. Tabloları otomatik oluşturacak/güncelleyecek (`ddl-auto: update`)
3. Eureka'ya kayıt olacak

### 5. Bağlantıyı Test Etme

**pgAdmin ile:**
1. Her veritabanını açın
2. **Schemas > public > Tables** altında tabloların oluştuğunu kontrol edin

**Loglar ile:**
Servis loglarında şu mesajları görmelisiniz:
```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
```

## 🔧 Yapılandırma Detayları

### Connection Pool Ayarları (HikariCP)

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10      # Maksimum connection sayısı
      minimum-idle: 5             # Minimum boşta bekleyen connection
      connection-timeout: 30000   # Connection timeout (30 saniye)
      idle-timeout: 600000        # Idle connection timeout (10 dakika)
      max-lifetime: 1800000      # Connection max lifetime (30 dakika)
```

### JPA/Hibernate Ayarları

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update  # Tabloları otomatik güncelle (create-drop yerine)
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        jdbc:
          batch_size: 20  # Batch insert/update için
        order_inserts: true
        order_updates: true
```

## ⚠️ Önemli Notlar

1. **Şifre Güvenliği:** Production'da şifreleri environment variable veya Spring Cloud Config Server ile yönetin.

2. **ddl-auto: update:** Production'da `ddl-auto: validate` veya `none` kullanın. `update` sadece development için uygundur.

3. **H2 Database:** Test dosyalarında hala H2 kullanılıyor (test scope). Bu normaldir.

4. **Backup:** Production'a geçmeden önce veritabanı backup stratejisi oluşturun.

## 🐛 Sorun Giderme

### Bağlantı Hatası

**Hata:** `Connection refused` veya `FATAL: password authentication failed`

**Çözüm:**
1. PostgreSQL'in çalıştığını kontrol edin
2. `application.yaml` dosyasındaki şifreyi kontrol edin
3. PostgreSQL kullanıcı şifresini doğrulayın

### Tablo Oluşturma Hatası

**Hata:** `relation "table_name" already exists`

**Çözüm:**
- `ddl-auto: update` kullanıldığı için tablolar zaten var. Bu normaldir.
- Eğer tabloları sıfırdan oluşturmak istiyorsanız, önce veritabanını silin ve yeniden oluşturun.

### Port Çakışması

**Hata:** `Address already in use`

**Çözüm:**
- PostgreSQL'in 5432 portunda çalıştığını kontrol edin
- Farklı bir port kullanıyorsanız, `application.yaml` dosyasındaki URL'i güncelleyin

## 📚 Sonraki Adımlar

1. ✅ PostgreSQL migration tamamlandı
2. 🔄 API Documentation (Swagger/OpenAPI) ekle
3. 🔄 Centralized Configuration (Spring Cloud Config Server) kur
4. 🔄 Distributed Tracing (Sleuth + Zipkin) ekle
5. 🔄 Docker Compose ile tüm stack'i containerize et
6. 🔄 Monitoring & Logging (Prometheus + Grafana, ELK Stack)
7. 🔄 Security (JWT Authentication & Authorization) - En son

## 📝 Veritabanı Şemaları

Her servis kendi veritabanını kullanır:

- **user_db:** users, addresses tabloları
- **product_db:** products, categories tabloları
- **order_db:** orders, order_items tabloları
- **inventory_db:** inventory, stock_movements tabloları
- **notification_db:** notifications tablosu

## 🔗 İlgili Dosyalar

- `create-databases.sql` - Veritabanı oluşturma script'i
- Her servisin `pom.xml` - PostgreSQL dependency
- Her servisin `application.yaml` - PostgreSQL yapılandırması

