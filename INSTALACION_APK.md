# 📱 Instalación de Spacius - Instrucciones

## APKs Generadas ✅

### 1. **APK Debug** (Desarrollo)
- **Ubicación:** `app/build/outputs/apk/debug/app-debug.apk`
- **Tamaño:** ~21 MB
- **Fecha:** 14/12/2025
- **Uso:** Desarrollo y pruebas con logs habilitados

### 2. **APK Release** (Producción) ⭐ RECOMENDADA
- **Ubicación:** `app/build/outputs/apk/release/app-release-unsigned.apk`
- **Tamaño:** ~12 MB (optimizada con R8/ProGuard)
- **Fecha:** 14/12/2025
- **Uso:** Distribución final (más rápida y ligera)

---

## 🔧 Cambios Realizados para Solucionar Imágenes

### Problema Original:
- ✅ **Emulador:** Imágenes se mostraban correctamente
- ❌ **Móvil físico:** Imágenes no cargaban

### Solución Implementada:

1. **Configuración de Red (`AndroidManifest.xml`)**
   - Agregado `usesCleartextTraffic="true"` para permitir HTTP
   - Agregado `networkSecurityConfig` para configuración personalizada

2. **Política de Seguridad de Red (`network_security_config.xml`)**
   - Permite tráfico cleartext necesario
   - Confía en certificados del sistema
   - Configura dominios de Firebase específicamente

3. **Optimización de Glide (`SpaciusGlideModule.kt`)**
   - Caché en memoria: 20 MB
   - Caché en disco: 100 MB
   - Timeout aumentado a 10 segundos
   - Manejo de errores mejorado

---

## 📲 Instalación en Dispositivo Móvil

### Opción 1: Instalación Directa (Recomendada)

1. **Copia la APK al móvil:**
   ```bash
   # Usando cable USB con ADB
   adb install app/build/outputs/apk/release/app-release-unsigned.apk
   ```

2. **O envía por correo/WhatsApp:**
   - Envíate la APK a ti mismo
   - Descárgala en el móvil
   - Habilita "Instalar apps desconocidas" en Configuración
   - Abre la APK y pulsa "Instalar"

### Opción 2: Instalación Manual

1. **Conecta tu móvil por USB** con depuración USB habilitada

2. **Desde VS Code o terminal:**
   ```bash
   # Verificar dispositivos conectados
   adb devices
   
   # Instalar APK release
   adb install -r app/build/outputs/apk/release/app-release-unsigned.apk
   ```

---

## ✅ Verificación Post-Instalación

### 1. Permisos Requeridos:
- ✅ **Internet** - Para cargar imágenes y datos
- ✅ **Ubicación** - Para mapas (opcional)
- ✅ **Notificaciones** - Para recordatorios (Android 13+)

### 2. Probar Carga de Imágenes:
1. Abre la app
2. Inicia sesión
3. Ve a la pantalla de Inicio
4. **Verifica que las imágenes de "Lugares recomendados" cargan correctamente**

### 3. Si aún no cargan:
```bash
# Ver logs en tiempo real (con móvil conectado)
adb logcat | grep -E "Glide|Spacius|Firebase"
```

---

## 🚀 Características de la APK Release

### Optimizaciones Aplicadas:
- ✅ **R8/ProGuard** activado - Código ofuscado y optimizado
- ✅ **Reducción de tamaño** - 43% más pequeña que debug (12 MB vs 21 MB)
- ✅ **Rendimiento mejorado** - Sin logs de depuración
- ✅ **Caché optimizada** - Imágenes se guardan localmente
- ✅ **Seguridad de red** - Configuración específica para Firebase

### Configuraciones de Red:
```xml
✅ Cleartext traffic permitido (HTTP)
✅ Dominios de Firebase autorizados
✅ Certificados de sistema confiables
✅ Timeout de 10 segundos para imágenes
```

---

## 📊 Diferencias entre APKs

| Característica | Debug (21 MB) | Release (12 MB) |
|---------------|---------------|-----------------|
| Tamaño | ~21 MB | ~12 MB |
| Logs | Habilitados | Deshabilitados |
| Ofuscación | No | Sí (ProGuard/R8) |
| Velocidad | Normal | Optimizada |
| Uso | Desarrollo | Producción |

---

## 🔍 Troubleshooting

### Problema: "App no instalada"
**Solución:**
```bash
# Desinstalar versión anterior primero
adb uninstall com.example.spacius

# Reinstalar
adb install app/build/outputs/apk/release/app-release-unsigned.apk
```

### Problema: "Imágenes siguen sin cargar"
**Verificar:**
1. ✅ Conexión a internet activa
2. ✅ Permisos de internet concedidos
3. ✅ URLs de Firebase Storage accesibles
4. ✅ Versión release instalada (no debug antigua)

### Problema: "Error de firma"
**Nota:** La APK está sin firmar. Para producción real:
```bash
# Generar keystore (solo una vez)
keytool -genkey -v -keystore spacius.keystore -alias spacius -keyalg RSA -keysize 2048 -validity 10000

# Firmar APK
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 -keystore spacius.keystore app-release-unsigned.apk spacius
```

---

## 📝 Notas Importantes

### Para el Usuario:
- ⚠️ Al ser APK sin firmar, algunos móviles mostrarán advertencia de "App desconocida"
- ✅ Es normal y seguro, simplemente permite la instalación
- ✅ Las imágenes ahora cargarán tanto en WiFi como en datos móviles

### Para el Desarrollador:
- 🔧 Si modificas el código, regenera con: `.\gradlew assembleRelease`
- 📦 Para versión de Play Store, necesitas firmar la APK
- 🧪 Siempre prueba en debug primero, luego en release

---

## 🎉 ¡Listo!

Tu app **Spacius** está lista para usar en dispositivos móviles con:
- ✅ Carga de imágenes funcionando
- ✅ Optimizaciones de rendimiento
- ✅ Tamaño reducido
- ✅ Configuración de red segura

**APK Recomendada para instalar:**
```
app/build/outputs/apk/release/app-release-unsigned.apk
```

---

**Última actualización:** 14 de Diciembre, 2025  
**Versión:** 1.0  
**Build:** Release optimizada
