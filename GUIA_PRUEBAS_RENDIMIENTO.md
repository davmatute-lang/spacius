# 📊 Guía de Pruebas de Rendimiento - Spacius

## 🎯 Objetivo

Simular carga de **10 usuarios concurrentes** y medir el rendimiento del sistema bajo diferentes escenarios.

---

## 📋 Pruebas Implementadas

### ✅ Archivos Creados:

```
app/src/test/java/com/example/spacius/performance/
├── ✅ ConcurrentLoadTest.kt         (7 pruebas de carga)
└── ✅ ResourcePerformanceTest.kt    (8 pruebas de recursos)

Total: 15 pruebas de rendimiento
```

---

## 🧪 Tipos de Pruebas de Rendimiento

### 1. **Pruebas de Carga (Load Testing)** ✅
**Archivo:** `ConcurrentLoadTest.kt`

#### Tests Implementados:

##### `10 usuarios concurrentes hacen login simultaneamente`
```kotlin
// Simula 10 usuarios haciendo login al mismo tiempo
Métricas:
- Tiempo de respuesta promedio
- Tiempo máximo
- Tasa de éxito
- Throughput (operaciones/segundo)

Criterios de Éxito:
✅ Tasa de éxito >= 80%
✅ Tiempo promedio < 3000ms
✅ Sin errores
```

##### `10 usuarios realizan multiples operaciones de lectura`
```kotlin
// 10 usuarios realizan 5 lecturas cada uno (50 operaciones totales)
Métricas:
- Operaciones exitosas vs fallidas
- Tiempo promedio de lectura
- Throughput

Criterios de Éxito:
✅ Tasa de éxito >= 90%
✅ Tiempo promedio < 3000ms
```

##### `10 usuarios crean reservas simultaneamente sin conflictos`
```kotlin
// Simula reservas concurrentes para detectar race conditions
Métricas:
- Reservas creadas exitosamente
- Conflictos detectados
- Manejo de concurrencia

Criterios de Éxito:
✅ Al menos 8/10 reservas exitosas
✅ Conflictos <= 2
```

---

### 2. **Pruebas de Estrés (Stress Testing)** ✅

##### `sistema maneja picos de carga sin degradacion severa`
```kotlin
// Simula tráfico creciente en fases: 5→10→15→10→5 usuarios
Fases de Carga:
- Fase 1: 5 usuarios (carga baja)
- Fase 2: 10 usuarios (carga normal)
- Fase 3: 15 usuarios (carga alta)
- Fase 4: 10 usuarios (bajando)
- Fase 5: 5 usuarios (recuperación)

Criterios de Éxito:
✅ Variación de rendimiento < 50%
✅ Sistema responde en todas las fases
```

##### `sistema recupera capacidad despues de pico de carga`
```kotlin
// Mide tiempo baseline → aplica carga pesada → mide recuperación
Métricas:
- Tiempo baseline PRE
- Tiempo durante pico (20 usuarios)
- Tiempo baseline POST
- % de degradación

Criterios de Éxito:
✅ Degradación post-pico < 20%
✅ Sistema se recupera en < 1 segundo
```

---

### 3. **Pruebas de Throughput** ✅

##### `sistema procesa al menos 10 operaciones por segundo`
```kotlin
// Ejecuta operaciones durante 5 segundos y mide throughput
Configuración:
- Duración: 5 segundos
- Workers: 10 concurrentes
- Operaciones: Continuas

Criterios de Éxito:
✅ Throughput >= 10 ops/seg
```

---

### 4. **Pruebas de Latencia** ✅

##### `percentil 95 de tiempo de respuesta es aceptable`
```kotlin
// Mide distribución de tiempos de respuesta
Métricas Calculadas:
- Percentil 50 (mediana)
- Percentil 95 (P95)
- Percentil 99 (P99)
- Promedio
- Mínimo/Máximo

Criterios de Éxito:
✅ P50 < 2000ms
✅ P95 < 3000ms
✅ P99 < 5000ms
```

---

### 5. **Pruebas de Memoria** ✅
**Archivo:** `ResourcePerformanceTest.kt`

##### `procesamiento de lista grande no causa OutOfMemory`
```kotlin
// Procesa 10,000 elementos en memoria
Validaciones:
- No OutOfMemoryError
- Uso de memoria < 100MB
- Tiempo procesamiento < 1000ms
```

##### `carga de imagenes simulada no satura memoria`
```kotlin
// Simula carga de 50 imágenes de 2MB cada una
Validaciones:
- Memoria usada < 150MB
- Sin memory leaks
```

---

### 6. **Pruebas de Algoritmos** ✅

##### `filtrado y busqueda en lista grande es eficiente`
```kotlin
// Busca en 5,000 registros con filtros múltiples
Operaciones:
1. Filtrar por disponibilidad
2. Filtrar por capacidad >= 50
3. Ordenar por capacidad descendente
4. Tomar primeros 20

Criterio de Éxito:
✅ Tiempo < 500ms
```

##### `generacion de bloques horarios es eficiente`
```kotlin
// Genera 7,200 bloques horarios (20 lugares × 30 días × 12 bloques)
Criterio de Éxito:
✅ Tiempo < 2000ms
```

##### `validacion de conflictos de horarios es rapida`
```kotlin
// Valida conflicto contra 1,000 reservas existentes
Criterio de Éxito:
✅ Tiempo < 100ms (debe ser instantáneo)
```

---

### 7. **Pruebas de Cache** ✅

##### `cache mejora rendimiento en accesos repetidos`
```kotlin
// Compara acceso con y sin cache
Métricas:
- Tiempo sin cache: ~100ms
- Tiempo con cache: < 10ms
- Mejora: ~90%
```

---

## 🚀 Cómo Ejecutar las Pruebas

### Opción 1: Todas las pruebas de rendimiento

```powershell
# Ejecutar todas las pruebas de rendimiento
.\gradlew :app:testDebugUnitTest --tests "com.example.spacius.performance.*"
```

### Opción 2: Suite específica

```powershell
# Solo pruebas de carga
.\gradlew :app:testDebugUnitTest --tests "ConcurrentLoadTest"

# Solo pruebas de recursos
.\gradlew :app:testDebugUnitTest --tests "ResourcePerformanceTest"
```

### Opción 3: Test individual

```powershell
# Test específico de 10 usuarios concurrentes
.\gradlew :app:testDebugUnitTest --tests "ConcurrentLoadTest.10 usuarios concurrentes hacen login simultaneamente"
```

### Opción 4: Desde Android Studio

1. Navegar a `app/src/test/java/com/example/spacius/performance/`
2. Click derecho en la carpeta `performance`
3. Seleccionar **"Run Tests in 'performance'"**

---

## 📊 Interpretación de Resultados

### Ejemplo de Salida - Test de Carga:

```
=== Resultados de Prueba de Carga ===
Usuarios simultáneos: 10
Operaciones exitosas: 10
Operaciones fallidas: 0
Tasa de éxito: 100.0%
Tiempo total: 1523ms
Tiempo promedio: 452.3 ms
Tiempo máximo: 687ms
Throughput: 6.57 ops/seg

✅ TEST PASSED
```

### Métricas Clave:

| Métrica | Valor Ideal | Valor Aceptable | Valor Crítico |
|---------|-------------|-----------------|---------------|
| **Tasa de Éxito** | 100% | >= 80% | < 80% |
| **Tiempo Promedio** | < 1000ms | < 3000ms | > 3000ms |
| **P95** | < 2000ms | < 3000ms | > 5000ms |
| **Throughput** | > 20 ops/s | >= 10 ops/s | < 10 ops/s |
| **Memoria** | < 50MB | < 100MB | > 100MB |

---

## 🎯 Escenarios de Prueba Simulados

### Escenario 1: Login Masivo (Morning Rush)
```
Contexto: 10 usuarios inician sesión al mismo tiempo (8:00 AM)
Carga: Alta
Duración: Instantánea
Expectativa: Sistema responde en < 3 segundos
```

### Escenario 2: Navegación Concurrente
```
Contexto: 10 usuarios navegando y consultando reservas
Carga: Media
Duración: Continua
Expectativa: 50 operaciones exitosas sin errores
```

### Escenario 3: Reservas Simultáneas
```
Contexto: 10 usuarios intentan reservar al mismo tiempo
Carga: Alta + Conflictos
Duración: Instantánea
Expectativa: Manejo correcto de concurrencia
```

### Escenario 4: Pico de Tráfico
```
Contexto: Tráfico crece de 5 a 15 usuarios y baja
Carga: Variable
Duración: 5 fases
Expectativa: Variación de rendimiento < 50%
```

### Escenario 5: Recuperación Post-Pico
```
Contexto: Sistema sometido a carga de 20 usuarios
Carga: Extrema → Normal
Duración: 1 segundo recuperación
Expectativa: Degradación < 20%
```

---

## 🔧 Configuración de Pruebas

### Constantes Configurables:

```kotlin
// En ConcurrentLoadTest.kt
const val NUM_USUARIOS_SIMULADOS = 10
const val NUM_OPERACIONES_POR_USUARIO = 5
const val TIMEOUT_SEGUNDOS = 30L
const val TIEMPO_RESPUESTA_ACEPTABLE_MS = 3000L

// En ResourcePerformanceTest.kt
const val MEMORIA_MAXIMA_MB = 100
const val TIEMPO_PROCESAMIENTO_MAX_MS = 1000L
```

### Ajustar para tu Entorno:

```kotlin
// Aumentar número de usuarios
const val NUM_USUARIOS_SIMULADOS = 20  // Prueba más agresiva

// Reducir timeout para detección rápida
const val TIMEOUT_SEGUNDOS = 15L

// Criterios más estrictos
const val TIEMPO_RESPUESTA_ACEPTABLE_MS = 2000L
```

---

## 📈 Análisis de Bottlenecks

### Identificación de Problemas:

#### 1. **Si Tiempo de Respuesta es Alto:**
```
Posibles Causas:
- Queries ineficientes a Firestore
- Procesamiento síncrono bloqueante
- Red lenta
- Falta de cache

Soluciones:
✓ Implementar paginación
✓ Usar coroutines para operaciones async
✓ Agregar cache local
✓ Optimizar índices Firestore
```

#### 2. **Si Throughput es Bajo:**
```
Posibles Causas:
- Thread pool pequeño
- Operaciones secuenciales
- Locks innecesarios

Soluciones:
✓ Aumentar concurrencia
✓ Paralelizar operaciones independientes
✓ Usar estructuras lock-free
```

#### 3. **Si Memoria Crece:**
```
Posibles Causas:
- Memory leaks
- Cache sin límite
- Imágenes no comprimidas

Soluciones:
✓ Usar WeakReference
✓ Implementar LRU cache
✓ Comprimir imágenes
✓ Liberar recursos explícitamente
```

---

## 🛠️ Herramientas Complementarias

### Android Profiler (Pruebas Reales)
```
1. Abrir Android Studio
2. Run > Profile 'app'
3. Pestaña "Memory" para uso de RAM
4. Pestaña "CPU" para uso de procesador
5. Pestaña "Network" para tráfico de red
```

### Firebase Performance Monitoring
```kotlin
// Agregar en build.gradle
implementation 'com.google.firebase:firebase-perf'

// Medir operaciones críticas
val trace = Firebase.performance.newTrace("reserva_creation")
trace.start()
// ... operación ...
trace.stop()
```

### JMeter (Pruebas de Carga Reales)
```bash
# Instalar JMeter
# Configurar plan de prueba con:
- 10 threads (usuarios)
- Ramp-up: 5 segundos
- Loop count: 5
- HTTP Requests a tu API/Firebase
```

---

## 📊 Reportes Generados

### Reporte HTML:
```
Ubicación:
app/build/reports/tests/testDebugUnitTest/index.html

Contenido:
- Tests ejecutados
- Tests pasados/fallidos
- Tiempo de ejecución
- Stack traces de errores
```

### Métricas en Consola:
```
Cada test imprime:
=== Resultados de Prueba ===
- Métricas específicas
- Tiempos
- Tasas de éxito
- Throughput
- Uso de recursos
```

---

## ✅ Checklist de Rendimiento

### Antes de Producción:

- [x] Tests de carga con 10 usuarios concurrentes pasan
- [x] Throughput >= 10 ops/seg
- [x] P95 < 3000ms
- [x] Uso de memoria < 100MB
- [x] Sin memory leaks detectados
- [ ] Pruebas en dispositivos de gama baja
- [ ] Pruebas con red lenta (throttling)
- [ ] Monitoreo con Firebase Performance
- [ ] Pruebas de carga en producción (canary)

---

## 🔮 Próximos Pasos

### Implementar Pruebas Reales:

1. **Firebase Test Lab**
```kotlin
// Ejecutar tests en dispositivos reales
// 10 dispositivos simultáneos = 10 usuarios
```

2. **Espresso + UI Automator**
```kotlin
// Tests de UI con usuarios simulados
@Test
fun multipleUsersNavigateApp() {
    // Simular acciones de usuario real
}
```

3. **Benchmark Library**
```kotlin
// Micro-benchmarking de funciones críticas
@BenchmarkRule
val benchmarkRule = BenchmarkRule()

@Test
fun benchmarkReservaCreation() {
    benchmarkRule.measureRepeated {
        createReserva()
    }
}
```

4. **Monitoring en Producción**
```kotlin
// Analytics de rendimiento real
Firebase.analytics.logEvent("operation_time") {
    param("duration_ms", durationMs)
    param("operation", "create_reserva")
}
```

---

## 📚 Referencias

- [Android Performance Patterns](https://www.youtube.com/playlist?list=PLWz5rJ2EKKc9CBxr3BVjPTPoDPLdPIFCE)
- [JMeter Load Testing](https://jmeter.apache.org/)
- [Firebase Performance Monitoring](https://firebase.google.com/docs/perf-mon)
- [Kotlin Coroutines Performance](https://kotlinlang.org/docs/coroutines-guide.html)

---

## 🎉 Resumen

### ✅ Lo que Implementamos:

- **15 pruebas de rendimiento** automatizadas
- **Simulación de 10 usuarios concurrentes**
- **Métricas de latencia, throughput, memoria**
- **Tests de estrés y recuperación**
- **Validación de algoritmos críticos**
- **Análisis de percentiles**
- **Documentación completa**

### 📊 Métricas Validadas:

- ✅ Tiempo de respuesta (P50, P95, P99)
- ✅ Throughput (operaciones/segundo)
- ✅ Tasa de éxito
- ✅ Uso de memoria
- ✅ Manejo de concurrencia
- ✅ Recuperación tras picos
- ✅ Eficiencia de cache

---

**Creado:** 3 de Diciembre 2025  
**Versión:** 1.0  
**Estado:** ✅ Implementado  
**Tests de Rendimiento:** 15 tests
