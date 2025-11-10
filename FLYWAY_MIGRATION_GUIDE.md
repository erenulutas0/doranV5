# 🗄️ Flyway Database Migration Kılavuzu

## 📋 Genel Bakış

Bu projede **Flyway** kullanarak database schema versioning ve otomatik migration implementasyonu yapılmıştır.

### Ne İşe Yarar?

- ✅ **Database Schema Versioning**: Her schema değişikliği versiyonlanır
- ✅ **Otomatik Migration**: Uygulama başlarken migration'lar otomatik çalışır
- ✅ **Migration History**: Hangi migration'ların çalıştığı takip edilir
- ✅ **Team Collaboration**: Herkes aynı schema'yı kullanır
- ✅ **Rollback Capability**: Migration'lar geri alınabilir (manuel)

---

## 🛠️ Teknoloji

- **Flyway**: Database migration tool
- **Spring Boot Integration**: Otomatik migration on startup
- **PostgreSQL**: Production database

---

## 📁 Migration Dosyaları

Migration script'leri her servisin `src/main/resources/db/migration/` dizininde bulunur:

```
user-service/src/main/resources/db/migration/
  └── V1__Initial_schema.sql

product-service/src/main/resources/db/migration/
  └── V1__Initial_schema.sql

order-service/src/main/resources/db/migration/
  └── V1__Initial_schema.sql

inventory-service/src/main/resources/db/migration/
  └── V1__Initial_schema.sql

notification-service/src/main/resources/db/migration/
  └── V1__Initial_schema.sql
```

### Dosya İsimlendirme

Flyway migration dosyaları şu formatta olmalıdır:

```
V{version}__{description}.sql
```

Örnekler:
- `V1__Initial_schema.sql` - İlk schema
- `V2__Add_user_roles.sql` - Kullanıcı rolleri eklendi
- `V3__Add_product_reviews.sql` - Ürün yorumları eklendi

**Önemli:**
- `V` büyük harf olmalı
- Version numarası artan sırada olmalı
- İki alt çizgi (`__`) zorunlu
- Açıklama snake_case formatında

---

## 🚀 Kullanım

### Otomatik Migration

Uygulama başlatıldığında Flyway otomatik olarak:

1. Migration history tablosunu kontrol eder (`flyway_schema_history`)
2. Henüz çalışmamış migration'ları tespit eder
3. Sırayla migration'ları çalıştırır
4. Migration history'ye kaydeder

**Log Örneği:**
```
Flyway Community Edition 9.x.x by Redgate
Database: jdbc:postgresql://localhost:5432/user_db (PostgreSQL 15)
Successfully validated 1 migration (execution time 00:00.012s)
Creating Schema History table "public"."flyway_schema_history" ...
Current version of schema "public": << Empty Schema >>
Migrating schema "public" to version "1 - Initial schema"
Successfully applied 1 migration to schema "public" (execution time 00:00.045s)
```

### Manuel Migration

Maven ile manuel migration çalıştırabilirsiniz:

```bash
cd user-service
mvn flyway:migrate
```

---

## 📝 Yeni Migration Oluşturma

### 1. Yeni Migration Dosyası Oluşturun

Örnek: `user-service/src/main/resources/db/migration/V2__Add_user_roles.sql`

```sql
-- Migration: V2__Add_user_roles.sql
-- Description: Adds user roles table and relationship

CREATE TABLE IF NOT EXISTS user_roles (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles(user_id);
```

### 2. Servisi Yeniden Başlatın

Migration otomatik olarak çalışacaktır.

---

## 🔍 Migration History

### Flyway Schema History Tablosu

Her database'de `flyway_schema_history` tablosu oluşturulur. Bu tablo:

- Hangi migration'ların çalıştığını
- Ne zaman çalıştığını
- Başarılı/başarısız durumunu
- Checksum bilgisini

saklar.

### History'yi Görüntüleme

```sql
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

---

## ⚙️ Configuration

Config repository'de (`C:\Users\pc\config-repo\application.yaml`) Flyway configuration:

```yaml
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true  # Mevcut database'de migration history yoksa baseline oluştur
    validate-on-migrate: true  # Migration'ları validate et
    locations: classpath:db/migration  # Migration script'lerinin yeri
    clean-disabled: true  # Production'da clean komutunu devre dışı bırak
```

### Hibernate ddl-auto

Flyway kullanıldığı için Hibernate'in `ddl-auto` ayarı `validate` olarak ayarlanmıştır:

```yaml
jpa:
  hibernate:
    ddl-auto: validate  # Flyway migration'ları kullanıldığı için validate
```

Bu sayede:
- Hibernate schema'yı değiştirmez
- Flyway migration'ları tek kaynak olur
- Schema değişiklikleri kontrol altında olur

---

## 🔧 Troubleshooting

### Migration Başarısız Oldu

1. **Logları kontrol edin:**
   ```
   Flyway migration failed: ...
   ```

2. **Database'i kontrol edin:**
   ```sql
   SELECT * FROM flyway_schema_history WHERE success = false;
   ```

3. **Migration'ı düzeltin ve tekrar deneyin**

### Migration Zaten Çalıştı Hatası

Eğer bir migration'ı değiştirirseniz, Flyway checksum hatası verir:

```
Validate failed: Migration checksum mismatch
```

**Çözüm:**
1. Migration'ı geri alın (manuel SQL ile)
2. Migration dosyasını düzeltin
3. `flyway_schema_history` tablosundan ilgili kaydı silin
4. Servisi yeniden başlatın

**Not:** Production'da migration'ları değiştirmeyin! Yeni bir migration oluşturun.

### Baseline Oluşturma

Eğer mevcut bir database'iniz varsa ve Flyway'i ilk kez kullanıyorsanız:

```yaml
spring:
  flyway:
    baseline-on-migrate: true  # Otomatik baseline oluşturur
```

Veya manuel:

```bash
mvn flyway:baseline
```

---

## 📊 Best Practices

### 1. Migration'ları Küçük Tutun

Her migration tek bir değişiklik yapmalı:
- ✅ İyi: `V2__Add_user_roles.sql`
- ❌ Kötü: `V2__Add_user_roles_and_products_and_orders.sql`

### 2. Geriye Dönük Uyumluluk

Migration'lar geriye dönük uyumlu olmalı:
- Yeni kolon eklerken `NOT NULL` kullanmayın (önce ekleyin, sonra default değer verin, sonra NOT NULL yapın)
- Tablo silerken dikkatli olun

### 3. Test Edin

Migration'ları test environment'ta test edin:
```bash
# Test database'de test et
mvn flyway:migrate -Dflyway.url=jdbc:postgresql://localhost:5432/test_db
```

### 4. Version Numaralarını Artırın

Her yeni migration için version numarasını artırın:
- V1, V2, V3, ...

### 5. Açıklayıcı İsimler Kullanın

Migration dosya isimleri açıklayıcı olmalı:
- ✅ `V2__Add_user_roles.sql`
- ✅ `V3__Add_product_reviews.sql`
- ❌ `V2__Update.sql`
- ❌ `V3__Fix.sql`

---

## 🎯 Örnek Senaryolar

### Senaryo 1: Yeni Kolon Ekleme

```sql
-- V2__Add_user_phone_verified.sql
ALTER TABLE users ADD COLUMN phone_verified BOOLEAN DEFAULT FALSE;
```

### Senaryo 2: Yeni Tablo Ekleme

```sql
-- V3__Create_user_preferences.sql
CREATE TABLE IF NOT EXISTS user_preferences (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    theme VARCHAR(20) DEFAULT 'light',
    language VARCHAR(10) DEFAULT 'en',
    CONSTRAINT fk_user_preferences_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### Senaryo 3: Index Ekleme

```sql
-- V4__Add_user_created_at_index.sql
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);
```

---

## 📚 Kaynaklar

- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Spring Boot Flyway Integration](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway)

---

## ✅ Avantajlar

- ✅ Schema değişiklikleri versiyonlanır
- ✅ Otomatik migration (uygulama başlarken)
- ✅ Migration history tracking
- ✅ Team collaboration (herkes aynı schema)
- ✅ Production'a geçiş hazır
- ✅ Rollback capability (manuel)

---

## 🎉 Sonuç

Flyway ile:
- ✅ Database schema'nız versiyonlanır
- ✅ Migration'lar otomatik çalışır
- ✅ Team collaboration kolaylaşır
- ✅ Production deployment güvenli hale gelir

