# API Gateway - Entegrasyon Dokümantasyonu

## 🎯 Özellikler

API Gateway, tüm mikroservislerin tek bir entry point üzerinden erişilebilir olmasını sağlar.

### ✅ Tamamlanan Özellikler

1. **Routing (Yönlendirme)**
   - Tüm servisler `/api/{service-name}/**` path'i üzerinden erişilebilir
   - Eureka Service Discovery ile otomatik servis bulma
   - Load balancing (Eureka ile)

2. **CORS (Cross-Origin Resource Sharing)**
   - Global CORS yapılandırması
   - Tüm HTTP metodları desteklenir
   - Production'da spesifik domain'ler belirtilmeli

3. **Circuit Breaker (Resilience4j)**
   - Her servis için ayrı circuit breaker
   - Servis hata verdiğinde fallback endpoint'e yönlendirme
   - Otomatik retry mekanizması

4. **Retry Mechanism**
   - Başarısız istekler için otomatik retry
   - Exponential backoff stratejisi
   - Yapılandırılabilir retry sayısı

5. **Logging**
   - Tüm istekler loglanır
   - Request/Response bilgileri kaydedilir
   - Client IP tracking

6. **Rate Limiting** (Opsiyonel - Redis gerektirir)
   - IP adresine göre rate limiting
   - Servis bazında farklı limitler
   - Redis ile distributed rate limiting

7. **Actuator Endpoints**
   - Health checks: `/actuator/health`
   - Gateway routes: `/actuator/gateway/routes`
   - Metrics: `/actuator/metrics`

## 📋 Servis Route'ları

| Servis | Gateway Path | Backend Service |
|--------|--------------|----------------|
| User Service | `/api/users/**` | `lb://user-service` |
| Product Service | `/api/products/**` | `lb://product-service` |
| Order Service | `/api/orders/**` | `lb://order-service` |
| Inventory Service | `/api/inventory/**` | `lb://inventory-service` |
| Notification Service | `/api/notifications/**` | `lb://notification-service` |

## 🚀 Kullanım

### 1. API Gateway'i Başlatma

```bash
cd api-gateway
mvn spring-boot:run
```

Gateway `http://localhost:8080` üzerinde çalışır.

### 2. Servislere Erişim

**Önceki Yöntem (Direkt):**
```bash
GET http://localhost:8081/users
GET http://localhost:8082/products
```

**Yeni Yöntem (Gateway üzerinden):**
```bash
GET http://localhost:8080/api/users
GET http://localhost:8080/api/products
GET http://localhost:8080/api/orders
```

### 3. Fallback Endpoints

Circuit breaker açıldığında fallback endpoint'ler devreye girer:

- `/fallback` - Genel fallback
- `/fallback/user` - User Service fallback
- `/fallback/product` - Product Service fallback
- `/fallback/order` - Order Service fallback
- `/fallback/inventory` - Inventory Service fallback
- `/fallback/notification` - Notification Service fallback

## ⚙️ Yapılandırma

### Rate Limiting (Redis ile)

Rate limiting'i aktif etmek için:

1. **Redis'i kurun:**
```bash
docker run -d -p 6379:6379 --name redis redis:alpine
```

2. **application.yaml'da Redis yapılandırmasını aktif edin:**
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

3. **Rate limiting filter'larını aktif edin:**
```yaml
spring:
  cloud:
    gateway:
      default-filters:
        - name: RequestRateLimiter
          args:
            redis-rate-limiter.replenishRate: 10
            redis-rate-limiter.burstCapacity: 20
            redis-rate-limiter.requestedTokens: 1
            key-resolver: "#{@ipKeyResolver}"
```

### Circuit Breaker Yapılandırması

`application.yaml` dosyasında Resilience4j yapılandırması:

```yaml
resilience4j:
  circuitbreaker:
    configs:
      default:
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        failureRateThreshold: 50
        waitDurationInOpenState: 10s
```

## 📊 Monitoring

### Actuator Endpoints

- **Health Check:** `http://localhost:8080/actuator/health`
- **Gateway Routes:** `http://localhost:8080/actuator/gateway/routes`
- **Metrics:** `http://localhost:8080/actuator/metrics`

### Logging

Tüm istekler `LoggingGlobalFilter` tarafından loglanır:

```
=== API Gateway Request ===
Method: GET
Path: /api/users
Client IP: 127.0.0.1
Headers: {...}

=== API Gateway Response ===
Status: 200 OK
Duration: 45 ms
============================
```

## 🔧 Geliştirme Notları

### Yeni Route Ekleme

`application.yaml` dosyasına yeni route eklemek için:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: new-service
          uri: lb://new-service
          predicates:
            - Path=/api/new-service/**
          filters:
            - StripPrefix=2
            - name: CircuitBreaker
              args:
                name: newServiceCircuitBreaker
                fallbackUri: forward:/fallback/new-service
```

### Custom Filter Ekleme

Yeni bir global filter eklemek için:

```java
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Custom logic
        return chain.filter(exchange);
    }
    
    @Override
    public int getOrder() {
        return -1; // Filter sırası
    }
}
```

## 🐛 Sorun Giderme

### Gateway Başlamıyor

1. **Eureka Server çalışıyor mu?**
   - Eureka Server'ın `http://localhost:8761` üzerinde çalıştığından emin olun

2. **Port çakışması var mı?**
   - Port 8080'in kullanılabilir olduğundan emin olun

### Rate Limiting Çalışmıyor

1. **Redis çalışıyor mu?**
   ```bash
   docker ps | grep redis
   ```

2. **Redis yapılandırması doğru mu?**
   - `application.yaml`'da Redis host ve port kontrol edin

3. **Rate limiting filter'ları aktif mi?**
   - Yorum satırı olmadığından emin olun

### Circuit Breaker Çalışmıyor

1. **Resilience4j dependency eklendi mi?**
   - `pom.xml`'de `spring-cloud-starter-circuitbreaker-reactor-resilience4j` kontrol edin

2. **Fallback endpoint'ler tanımlı mı?**
   - `FallbackController` sınıfının mevcut olduğundan emin olun

## 📝 Sonraki Adımlar

1. **Security (JWT Authentication)**
   - API Gateway seviyesinde authentication filter
   - Token validation

2. **API Documentation (Swagger)**
   - Tüm endpoint'ler için dokümantasyon
   - Gateway üzerinden erişilebilir Swagger UI

3. **Request/Response Transformation**
   - Request/Response body'lerini dönüştürme
   - Header manipulation

4. **Distributed Tracing**
   - Zipkin/Jaeger entegrasyonu
   - Request tracing

## 📚 Kaynaklar

- [Spring Cloud Gateway Documentation](https://spring.io/projects/spring-cloud-gateway)
- [Resilience4j Documentation](https://resilience4j.readme.io/)
- [Eureka Service Discovery](https://spring.io/projects/spring-cloud-netflix)

