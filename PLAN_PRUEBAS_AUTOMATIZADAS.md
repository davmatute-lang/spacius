# 📋 Plan de Pruebas Automatizadas - Spacius

## 📱 Información del Proyecto

- **Proyecto:** Spacius - Sistema de Reserva de Espacios
- **Plataforma:** Android (Kotlin)
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 36
- **Rama Actual:** Desarrollo-Notificaciones

---

## 🎯 Objetivos de las Pruebas

### Objetivos Principales
1. Garantizar la funcionalidad correcta de todas las características críticas
2. Validar la integración con Firebase (Auth, Firestore, Storage)
3. Verificar el sistema de notificaciones y recordatorios
4. Asegurar la navegación entre fragmentos
5. Validar la gestión de reservas y horarios

### Objetivos Secundarios
1. Mejorar la cobertura de código
2. Detectar regresiones tempranamente
3. Facilitar el mantenimiento del código
4. Documentar el comportamiento esperado

---

## 🔧 Stack Tecnológico de Testing

### Frameworks y Herramientas
- **JUnit 4.13.2** - Framework base de testing
- **AndroidX Test (JUnit 1.2.1)** - Testing para Android
- **Espresso 3.6.1** - UI Testing
- **Mockito/MockK** - Mocking de dependencias
- **Truth** - Assertions más legibles
- **WorkManager Testing** - Testing de tareas en background

### Configuración Actual
```kotlin
dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
```

---

## 📊 Estrategia de Testing

### Pirámide de Testing

```
           E2E
          /   \
         /  5% \
        /_______\
       /         \
      / Integr.  \
     /    15%     \
    /_____________\
   /               \
  /   Unitarias    \
 /      80%         \
/___________________\
```

### Distribución Propuesta
- **80% Pruebas Unitarias** - Lógica de negocio, utils, repositorios
- **15% Pruebas de Integración** - Interacción entre componentes
- **5% Pruebas E2E** - Flujos completos de usuario

---

## 🧪 Tipos de Pruebas

### 1. Pruebas Unitarias (Unit Tests)
**Ubicación:** `app/src/test/java/com/example/spacius/`

#### 1.1 Utilidades
- **DateTimeUtils**
  - `testEsFechaHoraFutura_FechaAnterior_RetornaFalse()`
  - `testEsFechaHoraFutura_FechaFutura_RetornaTrue()`
  - `testHayConflictoHorario_HorariosSuperpuestos_RetornaTrue()`
  - `testHayConflictoHorario_HorariosSeparados_RetornaFalse()`

- **HorarioUtils**
  - `testValidarHorario_HoraInicioMenorQueHoraFin_RetornaTrue()`
  - `testValidarHorario_HoraInicioMayorQueHoraFin_RetornaFalse()`
  - `testGenerarBloquesHorarios_RangoValido_RetornaListaNoDe0()`
  - `testGenerarBloquesHorarios_DuracionPersonalizada_RetornaBloquesCo rectos()`
  - `testHorarioAMinutos_HoraValida_RetornaMinutosCorrectos()`

#### 1.2 Modelos de Datos
- **FirestoreModels**
  - `testLugar_InstanciaConDatosValidos_SeCreaCorrectamente()`
  - `testReserva_ValidacionDeFechas_FuncionaCorrectamente()`
  - `testUsuario_CamposOpcionales_ManejaNulos()`

#### 1.3 Preferencias
- **NotificationPreferences**
  - `testSetAllNotificationsEnabled_GuardaValorCorrectamente()`
  - `testIsBookingRemindersEnabled_ValorPorDefecto_RetornaTrue()`
  - `testTodasLasPreferencias_GuardanYRecuperanValores()`

### 2. Pruebas de Integración (Integration Tests)
**Ubicación:** `app/src/androidTest/java/com/example/spacius/`

#### 2.1 Repositorio Firebase
- **FirestoreRepositoryTest**
  - `testObtenerLugares_ConexionExitosa_RetornaListaDeLugares()`
  - `testCrearReserva_DatosValidos_RetornaExito()`
  - `testObtenerBloquesDisponibles_FechaValida_RetornaBloquesFiltrados()`
  - `testValidarReserva_HorarioOcupado_RetornaError()`

#### 2.2 Base de Datos Local (Room)
- **AppDatabaseTest**
  - `testInsertarNotificacion_DatosValidos_SeGuardaCorrectamente()`
  - `testObtenerHistorialNotificaciones_RetornaListaOrdenada()`

#### 2.3 WorkManager
- **ReminderWorkerTest**
  - `testDoWork_ConDatosValidos_CreaNotificacion()`
  - `testDoWork_PreferenciasDesactivadas_NoCreaNotificacion()`

### 3. Pruebas de UI (UI Tests)
**Ubicación:** `app/src/androidTest/java/com/example/spacius/ui/`

#### 3.1 Autenticación
- **LoginActivityTest**
  - ✅ `testMuestraErrorSiCamposVacios()` (Existente)
  - ✅ `testMuestraErrorSiCredencialesInvalidas()` (Existente)
  - `testLoginExitoso_NavegaAMainActivity()`
  - `testBotonRegistro_NavegaARegistroActivity()`

- **RegisterActivityTest**
  - `testRegistroExitoso_CreaUsuarioYNaveaLogin()`
  - `testValidacionEmail_EmailInvalido_MuestraError()`
  - `testValidacionPassword_PasswordDebil_MuestraError()`

#### 3.2 Flujo de Reserva
- **ReservaFragmentTest**
  - `testSeleccionarFecha_MuestraHorariosDisponibles()`
  - `testSeleccionarHorario_HabilitaBotonReservar()`
  - `testReservarSinFecha_MuestraError()`
  - `testReservaExitosa_NavegaAReservaExitosaFragment()`

- **ReservaExitosaFragmentTest**
  - `testMostrarDetallesReserva_DatosCargadosCorrectamente()`
  - `testBotonVolverInicio_NavegaAHome()`
  - `testCreacionNotificacionRecordatorio_SeProgramaCorrectamente()`

#### 3.3 Navegación
- **MainActivityTest**
  - `testBottomNavigation_CambiEntreFragmentos_FuncionaCorrectamente()`
  - `testNavigationDrawer_OpcionesDisponibles_NavegaCorrectamente()`

#### 3.4 Perfil y Configuración
- **EditProfileFragmentTest**
  - `testActualizarNombre_GuardaCambios()`
  - `testCargarFotoPerfil_FuncionaCorrectamente()`

- **SettingsFragmentTest**
  - `testCerrarSesion_NavegaALoginActivity()`
  - `testCambiarConfiguraciones_GuardaPreferencias()`

- **NotificationsFragmentTest**
  - `testToggleNotificaciones_ActualizaPreferencias()`
  - `testBotonTestNotificacion_EnviaNotificacion()`

### 4. Pruebas End-to-End (E2E)
**Ubicación:** `app/src/androidTest/java/com/example/spacius/e2e/`

#### 4.1 Flujo Completo de Usuario
- **FlujoCompletoReservaTest**
  - `testFlujoCompleto_RegistroLoginReserva_ExitoCompleto()`
  - `testFlujoCompleto_ConsultarHistorial_MuestraReservas()`
  - `testFlujoCompleto_ModificarPerfil_GuardaCambios()`

---

## 📝 Casos de Prueba Detallados

### Ejemplo 1: Test Unitario - DateTimeUtils

```kotlin
package com.example.spacius.utils

import org.junit.Test
import org.junit.Assert.*

class DateTimeUtilsTest {
    
    @Test
    fun `esFechaHoraFutura con fecha pasada retorna false`() {
        // Arrange
        val fechaPasada = "01/01/2020"
        val horaPasada = "10:00"
        
        // Act
        val resultado = DateTimeUtils.esFechaHoraFutura(fechaPasada, horaPasada)
        
        // Assert
        assertFalse("Una fecha pasada debería retornar false", resultado)
    }
    
    @Test
    fun `esFechaHoraFutura con fecha futura retorna true`() {
        // Arrange
        val fechaFutura = "31/12/2030"
        val horaFutura = "15:00"
        
        // Act
        val resultado = DateTimeUtils.esFechaHoraFutura(fechaFutura, horaFutura)
        
        // Assert
        assertTrue("Una fecha futura debería retornar true", resultado)
    }
    
    @Test
    fun `hayConflictoHorario con horarios superpuestos retorna true`() {
        // Arrange
        val inicio1 = "10:00"
        val fin1 = "12:00"
        val inicio2 = "11:00"
        val fin2 = "13:00"
        
        // Act
        val resultado = DateTimeUtils.hayConflictoHorario(inicio1, fin1, inicio2, fin2)
        
        // Assert
        assertTrue("Horarios superpuestos deberían generar conflicto", resultado)
    }
}
```

### Ejemplo 2: Test de UI - LoginActivity

```kotlin
package com.example.spacius

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun loginExitoso_navegaAMainActivity() {
        // Arrange
        val emailValido = "test@example.com"
        val passwordValida = "password123"
        
        // Act
        onView(withId(R.id.etEmail))
            .perform(typeText(emailValido), closeSoftKeyboard())
        onView(withId(R.id.etPassword))
            .perform(typeText(passwordValida), closeSoftKeyboard())
        onView(withId(R.id.btnLogin))
            .perform(click())
        
        // Assert
        Thread.sleep(2000) // Esperar navegación
        onView(withId(R.id.bottom_navigation))
            .check(matches(isDisplayed()))
    }
    
    @Test
    fun camposVacios_muestraErrores() {
        // Act
        onView(withId(R.id.btnLogin))
            .perform(click())
        
        // Assert
        onView(withId(R.id.etEmail))
            .check(matches(hasErrorText("El email es requerido")))
    }
}
```

### Ejemplo 3: Test de Integración - FirestoreRepository

```kotlin
package com.example.spacius.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class FirestoreRepositoryIntegrationTest {
    
    private lateinit var repository: FirestoreRepository
    
    @Before
    fun setup() {
        repository = FirestoreRepository()
    }
    
    @Test
    fun obtenerLugares_retornaListaNoVacia() = runBlocking {
        // Act
        val lugares = repository.obtenerLugares()
        
        // Assert
        assertTrue("La lista de lugares no debe estar vacía", 
            lugares.isNotEmpty())
    }
    
    @Test
    fun obtenerBloquesDisponibles_fechaValida_retornaBloques() = runBlocking {
        // Arrange
        val lugarId = "lugar_test_id"
        val fecha = "25/11/2025"
        
        // Act
        val bloques = repository.obtenerBloquesDisponibles(lugarId, fecha)
        
        // Assert
        assertNotNull("Los bloques no deben ser null", bloques)
    }
}
```

---

## 🎯 Cobertura de Código Objetivo

### Por Módulo

| Módulo | Cobertura Objetivo | Prioridad |
|--------|-------------------|-----------|
| `utils/` | 90% | Alta |
| `data/` | 85% | Alta |
| `preferences/` | 80% | Media |
| Fragments principales | 70% | Alta |
| Adapters | 60% | Media |
| Activities | 75% | Alta |

### Métricas Globales
- **Cobertura de líneas:** ≥ 70%
- **Cobertura de ramas:** ≥ 60%
- **Cobertura de métodos:** ≥ 75%

---

## 📅 Cronograma de Implementación

### Fase 1: Pruebas Unitarias (Semana 1-2)
- [x] Crear tests para `DateTimeUtils` ✅ 18 tests implementados
- [x] Crear tests para `HorarioUtils` ✅ 22 tests implementados
- [x] Crear tests para `NotificationPreferences` ✅ 13 tests implementados
- [ ] Crear tests para modelos de datos

### Fase 2: Pruebas de Integración (Semana 3-4)
- [ ] Configurar Firebase Test Lab
- [ ] Crear tests para `FirestoreRepository`
- [ ] Crear tests para `AppDatabase`
- [ ] Crear tests para `ReminderWorker`

### Fase 3: Pruebas de UI (Semana 5-6)
- [x] Ampliar tests de `LoginActivity` ✅ Existentes
- [x] Crear tests para `RegisterActivity` ✅ 7 tests implementados
- [x] Crear tests para navegación ✅ HomeFragment y NotificationsFragment
- [ ] Crear tests para flujo de reserva

### Fase 4: Pruebas E2E y Optimización (Semana 7-8)
- [x] Crear tests de flujos completos ✅ 5 tests E2E implementados
- [ ] Optimizar tiempos de ejecución
- [x] Integrar con CI/CD ✅ GitHub Actions configurado
- [x] Documentación final ✅ GUIA_EJECUCION_PRUEBAS.md creada

---

## 🔄 Integración Continua (CI/CD)

### GitHub Actions Propuesto

```yaml
name: Android Tests

on:
  push:
    branches: [ Desarrollo-Notificaciones, main ]
  pull_request:
    branches: [ Desarrollo-Notificaciones, main ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 21
      uses: actions/setup-java@v3
      with:
        java-version: '21'
        distribution: 'temurin'
    
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
    
    - name: Run Unit Tests
      run: ./gradlew test
    
    - name: Run Instrumentation Tests
      uses: reactivecircus/android-emulator-runner@v2
      with:
        api-level: 29
        script: ./gradlew connectedAndroidTest
    
    - name: Upload Test Reports
      uses: actions/upload-artifact@v3
      if: always()
      with:
        name: test-reports
        path: app/build/reports/
```

---

## 🛠️ Configuración Adicional Necesaria

### 1. Agregar Dependencias de Testing

```kotlin
dependencies {
    // Testing existente
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    
    // Testing adicional recomendado
    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation("com.google.truth:truth:1.1.5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("androidx.work:work-testing:2.9.0")
}
```

### 2. Configurar Gradle para Testing

```kotlin
android {
    // ...
    
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
        animationsDisabled = true
    }
}
```

---

## 📈 Métricas y Reportes

### Herramientas de Análisis
1. **JaCoCo** - Cobertura de código
2. **SonarQube** - Calidad de código
3. **Firebase Test Lab** - Tests en dispositivos reales

### Reportes Generados
- Reporte de cobertura de código (HTML)
- Reporte de tests unitarios (XML/HTML)
- Reporte de tests de UI (Screenshots + Videos)
- Análisis de rendimiento

---

## 🚨 Manejo de Casos Especiales

### Tests con Firebase
- Usar emuladores locales para desarrollo
- Usar proyectos de test en Firebase para CI/CD
- Mock de servicios Firebase cuando sea necesario

### Tests con WorkManager
- Usar `WorkManagerTestInitHelper`
- Tests síncronos con `TestListenableWorkerBuilder`

### Tests con Permisos
- Usar `GrantPermissionRule` para permisos en runtime
- Mockear contextos cuando sea necesario

---

## ✅ Criterios de Aceptación

### Para Considerar una Prueba Válida
1. ✅ Sigue el patrón AAA (Arrange, Act, Assert)
2. ✅ Nombre descriptivo que explica qué se prueba
3. ✅ Independiente de otras pruebas
4. ✅ Rápida de ejecutar (< 1 segundo unit, < 5 segundos UI)
5. ✅ Determinística (mismo resultado siempre)

### Para Merge a Main
1. ✅ Todas las pruebas pasan
2. ✅ Cobertura mínima cumplida
3. ✅ No hay warnings de lint
4. ✅ Build exitoso en CI/CD

---

## 📚 Recursos y Referencias

### Documentación Oficial
- [Android Testing Guide](https://developer.android.com/training/testing)
- [Espresso Documentation](https://developer.android.com/training/testing/espresso)
- [JUnit 4 Documentation](https://junit.org/junit4/)

### Best Practices
- [Testing Codelab](https://developer.android.com/codelabs/advanced-android-kotlin-training-testing-basics)
- [Firebase Test Lab](https://firebase.google.com/docs/test-lab)

---

## 👥 Responsables

| Rol | Responsable | Responsabilidad |
|-----|-------------|-----------------|
| **Tech Lead** | - | Supervisión general |
| **QA Lead** | - | Diseño de estrategia de testing |
| **Developers** | Equipo | Implementación de tests |
| **DevOps** | - | Configuración CI/CD |

---

## 📝 Notas Adicionales

### Consideraciones Importantes
1. Los tests de UI deben ejecutarse en dispositivos con configuración consistente
2. Los tests con Firebase requieren conexión a internet o emuladores
3. Mantener los tests actualizados con los cambios de código
4. Revisar y actualizar este documento trimestralmente

### Próximos Pasos
1. Revisar y aprobar este plan con el equipo
2. Agregar dependencias de testing al proyecto
3. Configurar entorno de CI/CD
4. Comenzar implementación según cronograma

---

**Documento creado:** 24/11/2025  
**Última actualización:** 24/11/2025  
**Versión:** 1.0  
**Estado:** ✅ Aprobado para implementación
