# IDE Cache Temizleme ve Proje Yeniden Import

## IntelliJ IDEA

### Cache Temizleme:
1. **File** → **Invalidate Caches / Restart**
2. **Invalidate and Restart** seçin
3. IDE yeniden başlayacak

### Maven Projesini Yeniden Import:
1. **View** → **Tool Windows** → **Maven** (veya sağ taraftaki Maven penceresi)
2. **user-service** → **Lifecycle** → **clean** (çift tıklayın)
3. **user-service** → **Lifecycle** → **compile** (çift tıklayın)

### Alternatif:
1. **File** → **Reload Gradle/Maven Project**
2. Veya Maven penceresinde **Reload All Maven Projects** butonuna tıklayın
di
## Eclipse

### Cache Temizleme:
1. **Project** → **Clean**
2. **Clean all projects** seçin
3. **Clean** butonuna tıklayın

### Proje Yenileme:
1. Projeye sağ tıklayın
2. **Refresh** (F5) seçin
3. **Maven** → **Update Project** seçin

## VS Code

### Java Language Server Cache Temizleme:
1. **Ctrl+Shift+P** (veya **Cmd+Shift+P** Mac'te)
2. **Java: Clean Java Language Server Workspace** yazın
3. Enter'a basın
4. **Restart and delete** seçin

### Maven:
1. Terminal'de: `mvn clean compile`
2. VS Code otomatik olarak yeniden yükleyecek

## Manuel Temizleme (Tüm IDE'ler için)

### Terminal'den:
```bash
cd user-service
rm -rf target  # Linux/Mac
# veya
rmdir /s /q target  # Windows CMD
Remove-Item -Recurse -Force target  # Windows PowerShell

mvn clean compile
```

### Sonra:
1. IDE'yi kapatın
2. `user-service/target` klasörünü silin (eğer varsa)
3. IDE'yi açın
4. Projeyi yeniden import edin

## Önemli Notlar

- **IntelliJ IDEA**: Cache temizleme genellikle en etkili yöntemdir
- **Eclipse**: Project Clean genellikle yeterlidir
- **VS Code**: Java Language Server'ı yeniden başlatmak gerekir
- **Maven**: Her zaman `mvn clean compile` çalıştırın

## Sorun Devam Ederse

1. IDE'yi tamamen kapatın
2. `user-service/target` klasörünü silin
3. `user-service/.idea` klasörünü silin (IntelliJ için)
4. IDE'yi açın ve projeyi yeniden import edin

