# 🔒 Parche de Seguridad - API Keys Protegidas

**Fecha:** 4 de Noviembre, 2025  
**Autor:** GitHub Copilot  
**Severidad:** CRÍTICA  
**Estado:** ✅ RESUELTO

## 🚨 Problema Identificado

### Vulnerabilidad: API Keys Expuestas en Repositorio Público

**Archivos afectados:**
- `app/src/main/AndroidManifest.xml` - Google Maps API Key hardcodeada
- `app/google-services.json` - Credenciales de Firebase expuestas
- Carpetas `build/` - Artefactos compilados con keys en el repositorio

**Riesgo:**
- 🔴 **CRÍTICO:** Cualquier persona podía usar la cuota de Google Maps
- 🔴 **ALTO:** Posible facturación no autorizada
- 🟡 **MEDIO:** Acceso a configuración de Firebase

## ✅ Solución Implementada

### 1. Protección de Google Maps API Key

**Cambios realizados:**

#### a) `app/build.gradle.kts`
```kotlin
// ✅ AGREGADO: Lectura segura de API Key desde local.properties
defaultConfig {
    val localProperties = java.util.Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { localProperties.load(it) }
    }
    
    val mapsApiKey = localProperties.getProperty("MAPS_API_KEY") 
                     ?: System.getenv("MAPS_API_KEY") ?: ""
    manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
}
```

#### b) `app/src/main/AndroidManifest.xml`
```xml
<!-- ❌ ANTES: Key expuesta -->
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="AIzaSyCpC1GdhoYWZ9jpbHrow_mEkFCUDDYfSgA" />

<!-- ✅ DESPUÉS: Placeholder seguro -->
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="${MAPS_API_KEY}" />
```

#### c) `local.properties`
```properties
# ✅ AGREGADO: API Key local (NO se sube a Git)
MAPS_API_KEY=AIzaSyCpC1GdhoYWZ9jpbHrow_mEkFCUDDYfSgA
```

### 2. Actualización de .gitignore

```gitignore
# ✅ AGREGADO: Protección de archivos sensibles
app/google-services.json
**/google-services.json
**/build/
**/.gradle/
secrets.properties
```

### 3. Configuración de CI/CD

**Archivo:** `.github/workflows/android-tests.yml`

```yaml
# ✅ AGREGADO: Creación de local.properties desde secrets
- name: 🔒 Create local.properties with secrets
  run: |
    echo "sdk.dir=$ANDROID_HOME" > local.properties
    echo "MAPS_API_KEY=${{ secrets.MAPS_API_KEY }}" >> local.properties
  env:
    MAPS_API_KEY: ${{ secrets.MAPS_API_KEY }}
```

### 4. Documentación de Seguridad

**Archivos creados:**
- ✅ `SECURITY.md` - Guía completa de seguridad
- ✅ `GITHUB_SECRETS.md` - Configuración de secrets en CI/CD
- ✅ `local.properties.example` - Plantilla para desarrolladores
- ✅ `README.md` - Actualizado con instrucciones de configuración

## 📋 Archivos Modificados

| Archivo | Acción | Descripción |
|---------|--------|-------------|
| `.gitignore` | 🔧 Modificado | Agregadas exclusiones de archivos sensibles |
| `local.properties` | 🔧 Modificado | Agregada MAPS_API_KEY |
| `app/build.gradle.kts` | 🔧 Modificado | Lógica de lectura de API Key |
| `app/src/main/AndroidManifest.xml` | 🔧 Modificado | Key reemplazada por placeholder |
| `.github/workflows/android-tests.yml` | 🔧 Modificado | Configuración de secrets |
| `README.md` | 🔧 Modificado | Documentación actualizada |
| `SECURITY.md` | ✨ Creado | Guía de seguridad |
| `GITHUB_SECRETS.md` | ✨ Creado | Instrucciones de CI/CD |
| `local.properties.example` | ✨ Creado | Plantilla para devs |

## ⚠️ Acciones Pendientes (URGENTE)

### 1. Rotar Google Maps API Key 🔴
La key `AIzaSyCpC1GdhoYWZ9jpbHrow_mEkFCUDDYfSgA` está **expuesta en el historial de Git**.

**Pasos a seguir:**

1. **Ir a Google Cloud Console:**
   - https://console.cloud.google.com/google/maps-apis

2. **Revocar la API Key antigua:**
   - APIs & Services → Credentials
   - Buscar la key expuesta
   - Click en "Delete" o "Disable"

3. **Crear nueva API Key:**
   - Click en "Create Credentials" → "API Key"
   - Restricciones recomendadas:
     ```
     Application restrictions:
       - Android apps
       - Package name: com.example.spacius
       - SHA-1: (opcional para desarrollo)
     
     API restrictions:
       - Maps SDK for Android
     ```

4. **Actualizar en local.properties:**
   ```properties
   MAPS_API_KEY=TU_NUEVA_KEY_AQUI
   ```

5. **Actualizar GitHub Secret:**
   - Settings → Secrets → Actions
   - Editar `MAPS_API_KEY`
   - Pegar nueva key

### 2. Configurar GitHub Secrets 🟡

**Requerido para CI/CD:**
- `MAPS_API_KEY` - Para builds automáticos

**Ver:** [GITHUB_SECRETS.md](GITHUB_SECRETS.md)

### 3. Compartir google-services.json con el equipo 🟡

El archivo ya no está en Git. Opciones:
- Compartir por canal seguro (Slack, email encriptado)
- Usar variables de entorno en CI/CD
- Documentar cómo obtenerlo en README

## ✅ Verificación

### Checklist de Seguridad Completado:

- [x] API Key movida a `local.properties`
- [x] Placeholder en `AndroidManifest.xml`
- [x] `.gitignore` actualizado
- [x] CI/CD configurado para usar secrets
- [x] Documentación creada
- [ ] **TODO:** API Key antigua rotada ⚠️
- [ ] **TODO:** GitHub Secrets configurados
- [ ] **TODO:** google-services.json distribuido al equipo

### Cómo verificar localmente:

```bash
# 1. Verificar que local.properties existe
cat local.properties

# 2. Debe contener MAPS_API_KEY
# 3. Verificar que NO está en Git
git status local.properties  # Debe decir "ignored"

# 4. Compilar el proyecto
./gradlew assembleDebug

# 5. Verificar que el APK funciona correctamente
```

## 📚 Referencias

- [SECURITY.md](SECURITY.md) - Guía completa de seguridad
- [GITHUB_SECRETS.md](GITHUB_SECRETS.md) - Configuración de CI/CD
- [Google API Key Best Practices](https://developers.google.com/maps/api-security-best-practices)

## 🎯 Impacto

**Antes:**
- 🔴 API Keys visibles en repositorio público
- 🔴 Historial de Git expone credenciales
- 🔴 Riesgo de uso no autorizado

**Después:**
- ✅ API Keys protegidas en archivos locales
- ✅ CI/CD usa GitHub Secrets
- ✅ Documentación clara para el equipo
- ⚠️ Historial antiguo aún contiene keys (rotación pendiente)

---

**Próximos pasos:** Rotar la API Key expuesta INMEDIATAMENTE después de aplicar este parche.
