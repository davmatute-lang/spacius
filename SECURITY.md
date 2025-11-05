# 🔒 Guía de Seguridad - Spacius

## ⚠️ Credenciales y API Keys

### Para Desarrolladores Locales

1. **NO commitees** archivos con credenciales sensibles:
   - ❌ `local.properties` con API Keys
   - ❌ `google-services.json`
   - ❌ Carpetas `build/`

2. **Configuración Inicial:**
   ```bash
   # Copia el archivo de ejemplo
   cp local.properties.example local.properties
   
   # Edita local.properties y agrega tu Google Maps API Key
   MAPS_API_KEY=TU_API_KEY_AQUI
   ```

3. **Obtener Google Maps API Key:**
   - Ve a [Google Cloud Console](https://console.cloud.google.com/google/maps-apis)
   - Crea un proyecto o selecciona uno existente
   - Habilita "Maps SDK for Android"
   - Crea credenciales → API Key
   - **Importante:** Restringe la API Key a tu app:
     ```
     Tipo: Android
     Package name: com.example.spacius
     SHA-1: (opcional para desarrollo)
     ```

### Para CI/CD (GitHub Actions)

La API Key se carga desde variables de entorno:

```yaml
# En GitHub: Settings → Secrets → Actions
MAPS_API_KEY=tu_api_key_para_ci
```

El `build.gradle.kts` automáticamente intenta:
1. Leer desde `local.properties` (desarrollo local)
2. Leer desde variable de entorno (CI/CD)

## 🔐 Archivos Protegidos por .gitignore

```
local.properties           # Contiene MAPS_API_KEY
app/google-services.json   # Credenciales de Firebase
**/build/                  # Artefactos compilados
secrets.properties         # Cualquier otro secreto
```

## ✅ Checklist de Seguridad

- [x] API Keys movidas a `local.properties`
- [x] `google-services.json` en `.gitignore`
- [x] Carpetas `build/` en `.gitignore`
- [x] Documentación de seguridad creada
- [ ] **TODO:** Rotar API Key antigua expuesta en commits previos
- [ ] **TODO:** Habilitar ProGuard para ofuscación en release
- [ ] **TODO:** Configurar reglas de seguridad de Firestore más estrictas

## 🚨 Si Expusiste una API Key por Accidente

1. **Revocar inmediatamente** en Google Cloud Console
2. **Crear nueva API Key** con restricciones apropiadas
3. **Actualizar** en `local.properties` local
4. **Actualizar** en GitHub Secrets (si aplica)
5. **NO intentar** borrar del historial de Git (es complicado)

## 📚 Recursos Adicionales

- [Mejores prácticas de seguridad en Android](https://developer.android.com/topic/security/best-practices)
- [Proteger API Keys en Android](https://developers.google.com/maps/api-security-best-practices)
- [Firebase Security Rules](https://firebase.google.com/docs/firestore/security/get-started)

## 📧 Contacto

Si encuentras alguna vulnerabilidad de seguridad, reporta a: [correo del equipo]
