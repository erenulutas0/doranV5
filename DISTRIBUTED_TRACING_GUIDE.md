# 🔍 Distributed Tracing (Sleuth + Zipkin) Kılavuzu

## 📋 Genel Bakış

Bu projede **Micrometer Tracing** (Spring Boot 3'te Sleuth'un yeni adı) ve **Zipkin** kullanarak distributed tracing implementasyonu yapılmıştır.

### Ne İşe Yarar?

- **Request Flow Tracking**: Bir isteğin tüm microservice'ler arasında nasıl ilerlediğini görebilirsiniz
- **Performance Monitoring**: Her serviste ne kadar süre harcandığını ölçebilirsiniz
- **Error Tracing**: Hataların hangi serviste ve hangi adımda oluştuğunu tespit edebilirsiniz
- **Service Dependencies**: Servisler arası bağımlılıkları görselleştirebilirsiniz

---

## 🛠️ Teknoloji Stack

- **Micrometer Tracing**: Spring Boot 3'te Sleuth'un yerine geçen tracing framework
- **Brave**: Tracing implementation (OpenZipkin'in tracer'ı)
- **Zipkin**: Distributed tracing system (trace'leri görselleştirme)

---

## 🚀 Kurulum

### 1. Dependencies

Tüm servislere aşağıdaki dependencies eklenmiştir:

```xml
<!-- Micrometer Tracing -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>

<!-- Zipkin Reporter -->
<dependency>
    <groupId>io.zipkin.reporter2</groupId>
    <artifactId>zipkin-reporter-brave</artifactId>
</dependency>
```

### 2. Configuration

Config repository'de (`C:\Users\pc\config-repo\application.yaml`) ortak configuration:

```yaml
management:
  tracing:
    sampling:
      probability: 1.0  # %100 sampling
  zipkin:
    tracing:
      endpoint: http://localhost:9411/api/v2/spans
```

### 3. Zipkin Server Kurulumu

#### Docker ile (Önerilen):

```bash
docker-compose -f docker-compose-zipkin.yml up -d
```

Veya manuel:

```bash
docker run -d -p 9411:9411 --name zipkin openzipkin/zipkin:latest
```

#### Java ile:

```bash
curl -sSL https://zipkin.io/quickstart.sh | bash -s
java -jar zipkin.jar
```

---

## 📊 Kullanım

### 1. Zipkin Server'ı Başlatın

```bash
docker-compose -f docker-compose-zipkin.yml up -d
```

### 2. Servisleri Başlatın

Tüm servisler otomatik olarak Zipkin'e trace gönderecektir.

### 3. Zipkin UI'ye Erişin

Tarayıcınızda açın: **http://localhost:9411**

### 4. Test İsteği Gönderin

Örnek: Bir sipariş oluşturun:

```bash
# API Gateway üzerinden
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "items": [
      {
        "productId": 1,
        "quantity": 2
      }
    ]
  }'
```

### 5. Zipkin'de Trace'i Görüntüleyin

1. Zipkin UI'de (http://localhost:9411) "Run Query" butonuna tıklayın
2. Son 15 dakikadaki trace'leri göreceksiniz
3. Bir trace'e tıklayarak detayları görebilirsiniz

---

## 🔍 Trace Yapısı

Bir istek şu şekilde trace edilir:

```
Client Request
    └── API Gateway (Port: 8080)
        ├── Order Service (Port: 8083)
        │   ├── User Service (Port: 8081) [Feign Client]
        │   ├── Product Service (Port: 8082) [Feign Client]
        │   └── Inventory Service (Port: 8084) [Feign Client]
        └── RabbitMQ
            └── Notification Service (Port: 8085) [Consumer]
```

### Trace ID ve Span ID

- **Trace ID**: Tüm servislerde aynı (bir isteği takip eder)
- **Span ID**: Her servis için farklı (her adımı temsil eder)
- **Parent Span**: Bir servisin hangi servisten çağrıldığını gösterir

---

## 📈 Sampling Rate

Production'da tüm istekleri trace etmek performans sorunlarına yol açabilir. Sampling rate'i ayarlayın:

```yaml
management:
  tracing:
    sampling:
      probability: 0.1  # %10 sampling (sadece 10 istekten 1'i trace edilir)
```

**Önerilen Değerler:**
- **Development**: 1.0 (%100)
- **Staging**: 0.5 (%50)
- **Production**: 0.1 (%10) veya daha düşük

---

## 🎯 Özellikler

### Otomatik Trace Propagation

- HTTP istekleri otomatik olarak trace edilir
- Feign Client çağrıları otomatik trace edilir
- RabbitMQ mesajları trace edilir
- Database query'leri trace edilir

### Custom Spans

Kod içinde custom span oluşturabilirsiniz:

```java
import io.micrometer.tracing.Tracer;

@Autowired
private Tracer tracer;

public void myMethod() {
    Span span = tracer.nextSpan().name("my-custom-operation").start();
    try (Tracer.SpanInScope ws = tracer.withSpan(span)) {
        // Your code here
    } finally {
        span.end();
    }
}
```

---

## 🔧 Troubleshooting

### Trace'ler Zipkin'de Görünmüyor

1. **Zipkin server çalışıyor mu?**
   ```bash
   curl http://localhost:9411/health
   ```

2. **Configuration doğru mu?**
   - `management.zipkin.tracing.endpoint` doğru mu?
   - Config Server'dan configuration alınıyor mu?

3. **Sampling rate 0 mı?**
   - `management.tracing.sampling.probability` kontrol edin

4. **Servisler yeniden başlatıldı mı?**
   - Dependencies eklendikten sonra servisleri yeniden başlatın

### Trace'ler Eksik

- **Feign Client**: Otomatik trace edilir, ekstra configuration gerekmez
- **RabbitMQ**: Otomatik trace edilir
- **Database**: JPA query'leri otomatik trace edilir

### Performance Sorunları

- Sampling rate'i düşürün
- Zipkin storage'ı Elasticsearch'e taşıyın (production için)
- Batch reporting kullanın

---

## 📚 Kaynaklar

- [Micrometer Tracing Documentation](https://micrometer.io/docs/tracing)
- [Zipkin Documentation](https://zipkin.io/)
- [Spring Boot 3 Observability](https://spring.io/blog/2022/10/12/observability-with-spring-boot-3)

---

## 🎉 Sonuç

Distributed Tracing ile:
- ✅ Request flow'u görselleştirebilirsiniz
- ✅ Performance bottleneck'leri tespit edebilirsiniz
- ✅ Error'ları hızlıca bulabilirsiniz
- ✅ Service dependencies'i anlayabilirsiniz

**Zipkin UI**: http://localhost:9411

