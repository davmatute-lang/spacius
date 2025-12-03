# 🧪 Guía de Ejecución de Pruebas Automatizadas - Spacius

## 📋 Resumen de Pruebas Implementadas

### ✅ Pruebas Creadas

#### 1. **Pruebas Unitarias** (7 archivos, 50+ tests)
- ✅ `DateTimeUtilsTest.kt` - 18 tests de utilidades de fecha/hora
- ✅ `HorarioUtilsTest.kt` - 22 tests de gestión de horarios
- ✅ `ExampleUnitTest.kt` - Test básico de ejemplo

#### 2. **Pruebas de Integración** (4 archivos, 20+ tests)
- ✅ `LoginActivityTest.kt` - 3 tests de login con Firebase
- ✅ `NotificationPreferencesTest.kt` - 13 tests de preferencias
- ✅ `HomeFragmentTest.kt` - 3 tests de visualización del home
- ✅ `NotificationsFragmentTest.kt` - 6 tests de configuración de notificaciones

#### 3. **Pruebas Funcionales** (2 archivos, 12+ tests)
- ✅ `RegisterActivityTest.kt` - 7 tests de registro de usuario
- ✅ `ExampleInstrumentedTest.kt` - Test básico de contexto

#### 4. **Pruebas E2E** (1 archivo, 5 tests)
- ✅ `FlujoCompletoUsuarioTest.kt` - 5 tests de flujos completos

### 📊 Total de Pruebas: **80+** tests automatizados

---

## 🚀 Cómo Ejecutar las Pruebas

### Opción 1: Desde Android Studio (Recomendado)

#### Ejecutar TODAS las pruebas unitarias:
1. Click derecho en `app/src/test/java/`
2. Seleccionar **"Run Tests in 'spacius.app.test'"**

#### Ejecutar TODAS las pruebas instrumentadas:
1. Click derecho en `app/src/androidTest/java/`
2. Seleccionar **"Run Tests in 'spacius.app.androidTest'"**

#### Ejecutar una prueba específica:
1. Abrir el archivo de prueba (ej: `DateTimeUtilsTest.kt`)
2. Click en el ícono verde ▶️ al lado del nombre de la clase
3. O click en el ícono verde al lado de un test individual

### Opción 2: Desde la Terminal

#### Ejecutar todas las pruebas unitarias:
```powershell
.\gradlew test
```

#### Ejecutar todas las pruebas instrumentadas (requiere dispositivo/emulador):
```powershell
.\gradlew connectedAndroidTest
```

#### Ejecutar pruebas de un módulo específico:
```powershell
.\gradlew :app:test
```

#### Ejecutar con reporte detallado:
```powershell
.\gradlew test --info
```

---

## 📱 Requisitos para Pruebas Instrumentadas

### Para ejecutar pruebas instrumentadas necesitas:

1. **Dispositivo físico** conectado por USB con:
   - Depuración USB activada
   - O un **Emulador Android** ejecutándose

2. **Verificar dispositivo conectado:**
```powershell
adb devices
```

3. **Si no aparece, reiniciar ADB:**
```powershell
adb kill-server
adb start-server
```

---

## 📂 Estructura de Carpetas de Pruebas

```
app/src/
├── test/                           # Pruebas Unitarias (JVM)
│   └── java/com/example/spacius/
│       ├── ExampleUnitTest.kt
│       └── utils/
│           ├── DateTimeUtilsTest.kt    ✅ 18 tests
│           └── HorarioUtilsTest.kt     ✅ 22 tests
│
└── androidTest/                    # Pruebas Instrumentadas (Android)
    └── java/com/example/spacius/
        ├── ExampleInstrumentedTest.kt
        ├── LoginActivityTest.kt           ✅ 3 tests
        ├── LoginActivityIdlingResource.kt
        ├── LoginIdlingResource.kt
        ├── RegisterActivityTest.kt        ✅ 7 tests
        ├── e2e/
        │   └── FlujoCompletoUsuarioTest.kt ✅ 5 tests E2E
        ├── fragments/
        │   ├── HomeFragmentTest.kt         ✅ 3 tests
        │   └── NotificationsFragmentTest.kt ✅ 6 tests
        └── preferences/
            └── NotificationPreferencesTest.kt ✅ 13 tests
```

---

## 🎯 Tipos de Pruebas Implementadas

### 1. ✅ Pruebas Unitarias
- **Qué prueban:** Lógica de negocio aislada
- **Velocidad:** Muy rápidas (< 1 segundo)
- **Dónde:** `app/src/test/`
- **Framework:** JUnit 4
- **Ejemplos:**
  - Validación de fechas
  - Cálculos de horarios
  - Conversiones de datos

### 2. ✅ Pruebas de Integración
- **Qué prueban:** Interacción entre componentes
- **Velocidad:** Moderada (1-5 segundos)
- **Dónde:** `app/src/androidTest/`
- **Framework:** AndroidX Test + Espresso
- **Ejemplos:**
  - Persistencia de preferencias
  - Navegación entre pantallas
  - Interacción con Firebase

### 3. ✅ Pruebas Funcionales
- **Qué prueban:** Funcionalidad completa de UI
- **Velocidad:** Lenta (5-15 segundos)
- **Dónde:** `app/src/androidTest/`
- **Framework:** Espresso
- **Ejemplos:**
  - Flujo de login
  - Flujo de registro
  - Interacción con formularios

### 4. ✅ Pruebas E2E
- **Qué prueban:** Flujos completos de usuario
- **Velocidad:** Muy lenta (10-30 segundos)
- **Dónde:** `app/src/androidTest/e2e/`
- **Framework:** Espresso + Intents
- **Ejemplos:**
  - Login → Navegación → Reserva
  - Registro → Login → Home

---

## 📈 Reportes de Pruebas

### Ver reportes HTML:

Después de ejecutar las pruebas, los reportes se generan en:

**Pruebas Unitarias:**
```
app/build/reports/tests/testDebugUnitTest/index.html
```

**Pruebas Instrumentadas:**
```
app/build/reports/androidTests/connected/index.html
```

### Abrir reporte en navegador:
```powershell
# Desde PowerShell
Invoke-Item app/build/reports/tests/testDebugUnitTest/index.html
```

---

## 🐛 Solución de Problemas Comunes

### Problema 1: "No devices found"
**Solución:**
```powershell
adb devices
adb kill-server
adb start-server
```

### Problema 2: "Test failed to run to completion"
**Solución:**
- Cerrar otras apps en el emulador
- Aumentar RAM del emulador
- Desactivar animaciones en el dispositivo

### Problema 3: "Firebase Auth failed"
**Solución:**
- Verificar que `google-services.json` está actualizado
- Verificar conexión a internet
- Crear usuarios de prueba en Firebase Console

### Problema 4: Pruebas de Fragment fallan
**Solución:**
```kotlin
// Agregar el tema correcto en el test
launchFragmentInContainer<HomeFragment>(
    themeResId = R.style.Theme_Spacius
)
```

---

## ⚙️ Configuración Adicional

### Desactivar animaciones en dispositivo/emulador:
1. Ir a **Configuración > Opciones de desarrollador**
2. Desactivar:
   - Escala de animación de ventana
   - Escala de animación de transición
   - Escala de duración de animador

### Habilitar logs detallados:
```kotlin
// En cualquier test
import android.util.Log
Log.d("TEST", "Mensaje de debug")
```

---

## 📊 Cobertura de Código

### Generar reporte de cobertura:
```powershell
.\gradlew jacocoTestReport
```

### Ver reporte:
```
app/build/reports/jacoco/jacocoTestReport/html/index.html
```

---

## ✅ Checklist de Ejecución

Antes de hacer commit, ejecuta:

- [ ] `.\gradlew test` - Pruebas unitarias
- [ ] `.\gradlew connectedAndroidTest` - Pruebas instrumentadas
- [ ] `.\gradlew lint` - Análisis de código
- [ ] `.\gradlew build` - Build completo

---

## 🎓 Patrones de Prueba Utilizados

### Patrón AAA (Arrange-Act-Assert)
```kotlin
@Test
fun `ejemplo de test con patron AAA`() {
    // Arrange - Preparar datos y condiciones
    val input = "valor de prueba"
    
    // Act - Ejecutar la acción a probar
    val resultado = funcionAProbar(input)
    
    // Assert - Verificar el resultado
    assertEquals("valor esperado", resultado)
}
```

### Given-When-Then
```kotlin
@Test
fun `dado un usuario valido cuando hace login entonces navega a home`() {
    // Given (Dado)
    val email = "user@test.com"
    
    // When (Cuando)
    loginRepository.login(email)
    
    // Then (Entonces)
    assertTrue(navigationController.currentDestination == "home")
}
```

---

## 📚 Recursos Útiles

- [Android Testing Guide](https://developer.android.com/training/testing)
- [Espresso Documentation](https://developer.android.com/training/testing/espresso)
- [JUnit 4 Documentation](https://junit.org/junit4/)
- [Testing Best Practices](https://developer.android.com/training/testing/fundamentals)

---

## 🔄 Próximos Pasos

### Pruebas Pendientes de Implementar:

- [ ] Pruebas de ReservaFragment
- [ ] Pruebas de HistoryFragment
- [ ] Pruebas de MapsFragment
- [ ] Pruebas de ReminderWorker
- [ ] Pruebas de Firebase Repository
- [ ] Pruebas de ViewModel
- [ ] Pruebas de Room Database

---

## 📝 Notas Finales

- **Ejecuta las pruebas regularmente** durante el desarrollo
- **Mantén las pruebas actualizadas** cuando cambies código
- **Escribe pruebas ANTES** de arreglar bugs (TDD)
- **No comitees código** con pruebas fallando
- **Documenta pruebas complejas** con comentarios claros

---

**Creado:** 3 de Diciembre 2025  
**Última actualización:** 3 de Diciembre 2025  
**Autor:** Equipo Spacius  
**Estado:** ✅ Activo
