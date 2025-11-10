# Java Microservices Project

Spring Boot ile geliştirilmiş production-ready microservices mimarisi örneği.

## 🎯 Proje Yapısı

Bu proje, bir e-ticaret sistemi için tam özellikli microservices mimarisini içermektedir:

### Servisler

1. **service-registry** (Port: 8761)
   - Eureka Server - Servis bulma ve kayıt

2. **config-server** (Port: 8888)
   - Spring Cloud Config Server - Merkezi yapılandırma yönetimi

3. **api-gateway** (Port: 8080)
   - Spring Cloud Gateway - Tüm isteklerin giriş noktası
   - Routing, Load Balancing, Rate Limiting, Circuit Breaker, CORS

4. **user-service** (Port: 8081)
   - Kullanıcı yönetimi (kayıt, giriş, profil)

5. **product-service** (Port: 8082)
   - Ürün yönetimi (CRUD işlemleri, kategori)

6. **order-service** (Port: 8083)
   - Sipariş yönetimi (oluşturma, durum takibi)
   - Feign Client ile diğer servislere entegrasyon
   - RabbitMQ ile event-driven communication

7. **inventory-service** (Port: 8084)
   - Stok yönetimi (miktar kontrolü, stok güncelleme)

8. **notification-service** (Port: 8085)
   - Bildirim yönetimi (e-posta, SMS, push)
   - RabbitMQ consumer

## 🛠️ Teknolojiler

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Cloud 2023.0.0**
- **Spring Cloud Gateway** - API Gateway
- **Netflix Eureka** - Service Discovery
- **Spring Cloud Config Server** - Centralized Configuration
- **Spring Data JPA** - Database Access
- **PostgreSQL** - Production Database
- **H2 Database** - Test Database
- **RabbitMQ** - Message Queue
- **Redis** - Rate Limiting
- **Resilience4j** - Circuit Breaker, Retry, Time Limiter
- **Feign Client** - Inter-service Communication
- **SpringDoc OpenAPI (Swagger)** - API Documentation
- **Spring Boot Actuator** - Monitoring & Health Checks
- **Micrometer Tracing + Zipkin** - Distributed Tracing
- **Lombok** - Boilerplate Code Reduction

## 📋 Özellikler

### ✅ Tamamlanan Özellikler

- [x] Service Discovery (Eureka)
- [x] API Gateway (Routing, Load Balancing, CORS)
- [x] Centralized Configuration (Config Server)
- [x] Inter-service Communication (Feign Client)
- [x] Circuit Breaker (Resilience4j)
- [x] Retry Mechanism
- [x] Rate Limiting (Redis)
- [x] Message Queue (RabbitMQ)
- [x] Event-driven Architecture
- [x] PostgreSQL Migration
- [x] API Documentation (Swagger/OpenAPI)
- [x] Health Checks (Actuator)
- [x] Distributed Tracing (Micrometer Tracing + Zipkin)
- [x] Global Exception Handling
- [x] Comprehensive Testing

### 🚧 Eksik Özellikler

- [x] Distributed Tracing (Micrometer Tracing + Zipkin) ✅
- [ ] Docker & Docker Compose
- [ ] Kubernetes Deployment
- [ ] Security (JWT Authentication)
- [ ] Logging & Monitoring (ELK Stack)
- [ ] CI/CD Pipeline
- [ ] Performance Testing

## 🚀 Kurulum ve Çalıştırma

### Gereksinimler

- Java 17 veya üzeri
- Maven 3.6+
- PostgreSQL 12+
- RabbitMQ
- Redis (Rate Limiting için)

### Adımlar

1. **PostgreSQL Veritabanlarını Oluşturun:**
   ```bash
   psql -U postgres -f create-databases.sql
   ```

2. **RabbitMQ'yu Başlatın:**
   ```bash
   # Docker ile
   docker run -d -p 5672:5672 -p 15672:15672 --name rabbitmq rabbitmq:3-management
   ```

3. **Redis'i Başlatın:**
   ```bash
   # Docker ile
   docker run -d -p 6379:6379 --name redis redis:alpine
   ```

4. **Config Repository'yi Hazırlayın:**
   - Config Server için local file system kullanılıyor
   - `C:\Users\pc\config-repo` dizininde yapılandırma dosyaları bulunmalı
   - Production'da Git repository kullanılabilir

5. **Servisleri Sırayla Başlatın:**
   ```bash
   # 1. Service Registry
   cd service-registry
   mvn spring-boot:run
   
   # 2. Config Server
   cd config-server
   mvn spring-boot:run
   
   # 3. API Gateway
   cd api-gateway
   mvn spring-boot:run
   
   # 4. Diğer servisler (sıra önemli değil)
   cd user-service && mvn spring-boot:run
   cd product-service && mvn spring-boot:run
   cd order-service && mvn spring-boot:run
   cd inventory-service && mvn spring-boot:run
   cd notification-service && mvn spring-boot:run
   ```

## 📡 API Endpoints

Tüm API'ler `api-gateway` üzerinden erişilebilir (port 8080):

- **User Service:** `http://localhost:8080/api/users/**`
- **Product Service:** `http://localhost:8080/api/products/**`
- **Order Service:** `http://localhost:8080/api/orders/**`
- **Inventory Service:** `http://localhost:8080/api/inventory/**`
- **Notification Service:** `http://localhost:8080/api/notifications/**`

### Swagger UI

Her servisin kendi Swagger UI'si var:
- User Service: `http://localhost:8081/swagger-ui.html`
- Product Service: `http://localhost:8082/swagger-ui.html`
- Order Service: `http://localhost:8083/swagger-ui.html`
- Inventory Service: `http://localhost:8084/swagger-ui.html`
- Notification Service: `http://localhost:8085/swagger-ui.html`

## 🔍 Monitoring

### Eureka Dashboard
Servis kayıtlarını görmek için:
`http://localhost:8761`

### Actuator Endpoints
Her servis için health check:
- `http://localhost:8081/actuator/health`
- `http://localhost:8082/actuator/health`
- `http://localhost:8083/actuator/health`
- `http://localhost:8084/actuator/health`
- `http://localhost:8085/actuator/health`

### Config Server
- Health: `http://localhost:8888/actuator/health`
- Config: `http://localhost:8888/{service-name}/default`

### RabbitMQ Management
`http://localhost:15672` (guest/guest)

### Zipkin UI (Distributed Tracing)
`http://localhost:9411`

## 🧪 Test Scripts

Proje kök dizininde test script'leri bulunmaktadır:
- `test-config-server.ps1` - Config Server ve servislerin durumunu kontrol eder
- `test-api-gateway.ps1` - API Gateway testleri
- `test-full-flow.ps1` - Tam akış testi (User → Product → Order → Notification)
- `test-postgresql-connection.ps1` - PostgreSQL bağlantı testleri

## 📚 Dokümantasyon

- `CENTRALIZED_CONFIG_EXPLANATION.md` - Config Server açıklaması
- `SWAGGER_UI_GUIDE.md` - Swagger UI kullanım kılavuzu
- `POSTGRESQL_MIGRATION.md` - PostgreSQL migration notları
- `RABBITMQ_SETUP.md` - RabbitMQ kurulum kılavuzu

## 🏗️ Mimari

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       ▼
┌─────────────────┐
│   API Gateway   │ (Port: 8080)
│  - Routing      │
│  - Rate Limit   │
│  - Circuit Br.   │
└────────┬────────┘
         │
    ┌────┴────┐
    │         │
    ▼         ▼
┌─────────┐ ┌─────────┐
│ Eureka  │ │ Config  │
│ Registry│ │ Server  │
│ (8761)  │ │ (8888)  │
└─────────┘ └─────────┘
    │
    ▼
┌─────────────────────────────────────┐
│         Microservices               │
│  ┌──────────┐  ┌──────────┐        │
│  │  User    │  │ Product  │        │
│  │ (8081)   │  │ (8082)   │        │
│  └──────────┘  └──────────┘        │
│  ┌──────────┐  ┌──────────┐        │
│  │  Order   │  │Inventory │        │
│  │ (8083)   │  │ (8084)   │        │
│  └────┬─────┘  └──────────┘        │
│       │                             │
│       ▼                             │
│  ┌──────────┐                       │
│  │Notification│                     │
│  │ (8085)   │                       │
│  └──────────┘                       │
└─────────────────────────────────────┘
         │
         ▼
┌─────────────────┐
│   RabbitMQ      │
│  (Message Queue)│
└─────────────────┘
         │
         ▼
┌─────────────────┐
│   PostgreSQL    │
│   (Database)    │
└─────────────────┘
```

## 📝 Lisans

Bu proje eğitim amaçlı geliştirilmiştir.

## 👤 Yazar

Eren Ulutaş

## 🙏 Teşekkürler

Bu proje, modern microservices mimarisi ve Spring Cloud ekosistemini öğrenmek için geliştirilmiştir.
