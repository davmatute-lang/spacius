# 🐛 Guía de Debug - Crashes en Dispositivo Móvil Real

## Problema Identificado
La aplicación se cierra (crash) al iniciar en el dispositivo móvil real, pero funciona correctamente en el emulador.

## ✅ Correcciones Aplicadas

### 1. **Mejoras en HomeFragment.kt**
- ✅ **Logs de debug agregados** para rastrear el flujo de ejecución
- ✅ **Manejo de excepciones robusto** en todos los métodos
- ✅ **Uso de Dispatchers.IO** para operaciones de red/base de datos
- ✅ **withContext(Dispatchers.Main)** para actualizaciones de UI
- ✅ **Try-catch en adaptadores** para prevenir crashes en RecyclerView
- ✅ **Manejo mejorado de Glide** con:
  - `diskCacheStrategy(DiskCacheStrategy.ALL)` - Cache completo
  - `timeout(10000)` - Timeout de 10 segundos
  - `error()` - Imagen de fallback
  - Verificación de URL vacía antes de cargar

### 2. **Mejoras en MainActivity.kt**
- ✅ **Logs de debug** en onCreate
- ✅ **Try-catch** en inicialización
- ✅ **Manejo de errores** en permisos de notificaciones

## 🔍 Cómo Ver los Logs en Dispositivo Real

### Opción 1: Logcat en Android Studio
1. Conecta tu dispositivo por USB
2. Abre **Logcat** en Android Studio (View → Tool Windows → Logcat)
3. Selecciona tu dispositivo en el dropdown
4. Filtra por:
   - **Tag:** `HomeFragment` o `MainActivity`
   - **Level:** Error (para ver solo errores)
5. Ejecuta la app y observa los logs

### Opción 2: Comando ADB (Terminal)
```powershell
# Ver todos los logs
adb logcat

# Filtrar solo errores de la app
adb logcat | Select-String -Pattern "HomeFragment|MainActivity|AndroidRuntime"

# Guardar logs en archivo
adb logcat > logs_spacius.txt
```

### Opción 3: Capturar Stack Trace del Crash
```powershell
# Limpiar logs anteriores
adb logcat -c

# Ejecutar la app y esperar el crash

# Ver el crash completo
adb logcat -d > crash_log.txt
```

## 🛠️ Pasos para Diagnosticar el Problema

### 1. Verificar Conexión a Internet
```kotlin
// El dispositivo real necesita internet para:
// - Conectarse a Firebase
// - Cargar imágenes con Glide
// - Obtener datos de Firestore
```

**Verificar:**
- ✅ WiFi o datos móviles activos
- ✅ Firebase accesible (abrir navegador, ir a google.com)
- ✅ Firewall del dispositivo no bloqueando la app

### 2. Verificar Configuración de Firebase
```powershell
# Verificar que google-services.json existe
ls app/google-services.json

# Debe mostrar el archivo. Si no existe, pídelo al equipo.
```

### 3. Verificar Permisos
Los siguientes permisos están en AndroidManifest.xml:
```xml
✅ INTERNET
✅ ACCESS_NETWORK_STATE
✅ ACCESS_FINE_LOCATION
✅ ACCESS_COARSE_LOCATION
✅ POST_NOTIFICATIONS (Android 13+)
```

### 4. Verificar Maps API Key
```powershell
# Verificar que local.properties tiene la API Key
cat local.properties

# Debe contener:
# MAPS_API_KEY=AIza...
```

## 📱 Diferencias Emulador vs Dispositivo Real

| Aspecto | Emulador | Dispositivo Real |
|---------|----------|------------------|
| **Conexión** | Siempre estable | Puede ser lenta/inestable |
| **Memoria** | Configurable, alta | Limitada, apps en segundo plano |
| **Permisos** | Auto-concedidos | Usuario debe aprobar |
| **Imágenes** | Cache rápido | Cache más lento |
| **GPS/Maps** | Simulado | Real (puede no tener señal) |
| **Firebase** | Conexión directa | Puede requerir más tiempo |

## 🔧 Soluciones Comunes

### Problema 1: Crash Inmediato al Abrir
**Causa:** Falta `google-services.json` o configuración incorrecta
**Solución:**
```powershell
# 1. Verificar que existe
ls app/google-services.json

# 2. Si no existe, obtener del equipo o Firebase Console
# 3. Rebuild del proyecto
.\gradlew clean assembleDebug
```

### Problema 2: Pantalla en Blanco (Sin Crash)
**Causa:** Error de red al cargar datos de Firestore
**Solución:**
- Verificar conexión a internet del dispositivo
- Ver logs: `adb logcat | Select-String "FirestoreRepository"`
- Timeout aumentado a 10 segundos en las queries

### Problema 3: Imágenes No Cargan
**Causa:** URLs externas bloqueadas o timeout
**Solución:**
- ✅ Ya aplicado: `network_security_config.xml` permite cleartext
- ✅ Ya aplicado: Glide con timeout de 10s
- ✅ Ya aplicado: Cache en disco habilitado

### Problema 4: Crash en RecyclerView
**Causa:** Datos nulos o error en adaptador
**Solución:**
- ✅ Ya aplicado: Try-catch en `onBindViewHolder`
- ✅ Ya aplicado: Verificación de URL vacía
- ✅ Ya aplicado: Placeholder en todas las imágenes

## 📊 Logs Importantes a Buscar

### Logs de Éxito ✅
```
D/HomeFragment: onCreateView - Iniciando
D/HomeFragment: onCreateView - Completado exitosamente
D/HomeFragment: onResume - Cargando datos
D/HomeFragment: Iniciando carga de datos...
D/HomeFragment: Lugares predefinidos inicializados
D/HomeFragment: Datos cargados: 10 lugares, 0 reservas
```

### Logs de Error ❌
```
E/HomeFragment: Error en onCreateView: [mensaje]
E/HomeFragment: Error al cargar datos: [mensaje]
E/FirestoreRepository: Error al obtener lugares: [mensaje]
E/AndroidRuntime: FATAL EXCEPTION: main
```

## 🚀 Compilar y Probar

### Build Debug Optimizado
```powershell
# Limpiar build anterior
.\gradlew clean

# Compilar versión debug
.\gradlew assembleDebug

# Instalar en dispositivo conectado
.\gradlew installDebug

# O todo en uno
.\gradlew clean installDebug
```

### Monitorear Durante la Ejecución
```powershell
# Terminal 1: Ver logs en tiempo real
adb logcat | Select-String -Pattern "HomeFragment|FirestoreRepository"

# Terminal 2: Instalar y ejecutar
.\gradlew installDebug
```

## 🎯 Checklist de Verificación

Antes de probar en el dispositivo:

- [ ] ✅ Conexión a internet activa en el dispositivo
- [ ] ✅ `google-services.json` presente en `app/`
- [ ] ✅ `local.properties` con `MAPS_API_KEY`
- [ ] ✅ Permisos de ubicación concedidos manualmente
- [ ] ✅ "Depuración USB" activada en el dispositivo
- [ ] ✅ Build limpio ejecutado (`gradlew clean`)
- [ ] ✅ Logcat abierto en Android Studio
- [ ] ✅ App anterior desinstalada del dispositivo

## 📞 Obtener Ayuda

Si el problema persiste:

1. **Capturar logs completos:**
   ```powershell
   adb logcat -c  # Limpiar
   # Ejecutar la app hasta que crashee
   adb logcat -d > crash_full_log.txt
   ```

2. **Información del dispositivo:**
   ```powershell
   adb shell getprop ro.build.version.release  # Versión Android
   adb shell getprop ro.product.model          # Modelo
   ```

3. **Compartir:**
   - `crash_full_log.txt`
   - Modelo y versión de Android
   - Pasos exactos para reproducir

## 🔄 Próximos Pasos

Después de aplicar estas correcciones:

1. **Rebuild completo** del proyecto
2. **Desinstalar** app anterior del dispositivo
3. **Instalar** nueva versión
4. **Monitorear logs** durante la ejecución
5. **Reportar** el primer error que aparezca en Logcat

---

**Última actualización:** Diciembre 15, 2025
**Estado:** ✅ Correcciones aplicadas, pendiente de prueba en dispositivo real
