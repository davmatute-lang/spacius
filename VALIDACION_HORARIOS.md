qiue # ⏰ Validación de Horarios - Sistema de Reservas

**Fecha:** 4 de Noviembre, 2025  
**Feature:** Validación de reservas en horarios pasados  
**Prioridad:** ALTA

## 🎯 Problema Resuelto

Anteriormente, el sistema permitía hacer reservas para fechas y horarios que ya habían pasado. Esto causaba:
- ❌ Reservas inválidas en el pasado
- ❌ Confusión para los usuarios
- ❌ Datos inconsistentes en la base de datos
- ❌ Bloques horarios pasados aparecían como disponibles

## ✅ Solución Implementada

### 1. **Validación en el Backend (FirestoreRepository)**

Se agregó la función `esFechaHoraFutura()` que valida:
- La fecha de la reserva
- La hora de inicio
- Compara con la fecha/hora actual del sistema

```kotlin
private fun esFechaHoraFutura(fecha: String, hora: String): Boolean {
    val formatoFechaHora = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val fechaHoraReserva = formatoFechaHora.parse("$fecha $hora")
    val ahora = Date()
    
    return fechaHoraReserva?.after(ahora) ?: false
}
```

### 2. **Validación en verificarDisponibilidad()**

```kotlin
suspend fun verificarDisponibilidad(...): Boolean {
    // 🆕 VALIDACIÓN: Verificar que la fecha/hora no haya pasado
    if (!esFechaHoraFutura(fecha, horaInicio)) {
        Log.d(TAG, "❌ Reserva rechazada: La fecha u hora ya pasó")
        return false
    }
    
    // ... resto de validaciones
}
```

### 3. **Filtrado de Bloques Disponibles**

Los bloques horarios ahora se filtran automáticamente:

```kotlin
suspend fun obtenerBloquesDisponibles(...): List<BloqueHorario> {
    val bloquesDisponibles = todosLosBloques.filter { bloque ->
        // ✅ Verificar que el bloque no haya pasado
        val noHaPasado = esFechaHoraFutura(fecha, bloque.horaInicio)
        
        // ✅ Verificar que no esté reservado
        val noEstaReservado = reservasDelLugar.none { ... }
        
        noHaPasado && noEstaReservado
    }
}
```

### 4. **Validación en la UI (ReservaFragment)**

Doble validación antes de crear la reserva:

```kotlin
btnReservar.setOnClickListener {
    // 🆕 Validación 1: Verificar en el cliente
    if (!esFechaHoraFutura(fechaSeleccionada, horaInicioSeleccionada)) {
        Toast.makeText(context, 
            "⏰ No puedes reservar en el pasado.\nSelecciona una fecha y hora futura.", 
            Toast.LENGTH_LONG
        ).show()
        return
    }
    
    // Validación 2: Verificar disponibilidad en servidor
    val disponible = firestoreRepository.verificarDisponibilidad(...)
    if (!disponible) {
        Toast.makeText(context, 
            "❌ Horario no disponible o ya pasó.", 
            Toast.LENGTH_LONG
        ).show()
        return
    }
}
```

## 📋 Casos de Uso Validados

### ✅ Caso 1: Reservar hoy en hora futura
```
Fecha: 2025-11-04
Hora actual: 14:30
Hora reserva: 16:00 - 17:45
Resultado: ✅ PERMITIDO
```

### ❌ Caso 2: Reservar hoy en hora pasada
```
Fecha: 2025-11-04
Hora actual: 14:30
Hora reserva: 08:00 - 09:45
Resultado: ❌ RECHAZADO
Mensaje: "⏰ No puedes reservar en el pasado"
```

### ✅ Caso 3: Reservar mañana
```
Fecha: 2025-11-05
Hora actual: 14:30 (hoy)
Hora reserva: 08:00 - 09:45
Resultado: ✅ PERMITIDO
```

### ❌ Caso 4: Reservar ayer
```
Fecha: 2025-11-03
Hora actual: 14:30 (hoy 04/11)
Hora reserva: 16:00 - 17:45
Resultado: ❌ RECHAZADO
```

### ❌ Caso 5: Bloque horario pasado no aparece
```
Fecha hoy: 2025-11-04
Hora actual: 14:30
Bloques mostrados:
  - 08:00 - 09:45 ❌ No se muestra (ya pasó)
  - 10:00 - 11:45 ❌ No se muestra (ya pasó)
  - 12:00 - 13:45 ❌ No se muestra (ya pasó)
  - 14:00 - 15:45 ⚠️ En curso (se puede mostrar según lógica)
  - 16:00 - 17:45 ✅ Se muestra
  - 18:00 - 19:45 ✅ Se muestra
  - 20:00 - 21:45 ✅ Se muestra
```

## 🔍 Flujo Completo de Validación

```
Usuario selecciona fecha y hora
         ↓
[ReservaFragment] Validación local
         ↓ (si pasa)
Usuario hace clic en "Reservar"
         ↓
[ReservaFragment] esFechaHoraFutura()
         ↓ (si pasa)
[FirestoreRepository] verificarDisponibilidad()
         ↓
    esFechaHoraFutura() ✅
         ↓
    Verificar conflictos horarios ✅
         ↓
    Crear reserva en Firestore ✅
         ↓
    Mostrar confirmación al usuario
```

## 🛡️ Seguridad y Robustez

### Manejo de Errores
```kotlin
try {
    val fechaHoraReserva = formatoFechaHora.parse("$fecha $hora")
    return fechaHoraReserva?.after(ahora) ?: false
} catch (e: Exception) {
    Log.e(TAG, "Error al validar fecha futura: ${e.message}")
    return false // Por seguridad, rechazar en caso de error
}
```

### Validación en Múltiples Capas
1. **UI (ReservaFragment):** Validación inmediata con feedback
2. **Repository:** Validación en backend antes de guardar
3. **Filtrado de listas:** Bloques pasados no aparecen

## 📊 Impacto

**Antes:**
- ❌ Usuarios podían reservar en el pasado
- ❌ Bloques pasados aparecían como disponibles
- ❌ Datos inconsistentes en la base de datos

**Después:**
- ✅ Validación automática de fecha/hora
- ✅ Solo se muestran bloques futuros
- ✅ Mensajes claros al usuario
- ✅ Datos consistentes y válidos

## 🧪 Testing Recomendado

### Test Manual
1. ✅ Intentar reservar ayer → Debe rechazar
2. ✅ Intentar reservar hoy en hora pasada → Debe rechazar
3. ✅ Intentar reservar hoy en hora futura → Debe permitir
4. ✅ Intentar reservar mañana → Debe permitir
5. ✅ Verificar que bloques pasados no aparecen en selector

### Test Automatizado (Sugerido)
```kotlin
@Test
fun `no permite reservar en el pasado`() = runTest {
    val repository = FirestoreRepository()
    val fechaAyer = "2025-11-03"
    val horaInicio = "16:00"
    val horaFin = "17:45"
    
    val disponible = repository.verificarDisponibilidad(
        lugarId, fechaAyer, horaInicio, horaFin
    )
    
    assertFalse(disponible)
}

@Test
fun `permite reservar en el futuro`() = runTest {
    val repository = FirestoreRepository()
    val fechaFutura = "2025-11-10"
    val horaInicio = "16:00"
    val horaFin = "17:45"
    
    val disponible = repository.verificarDisponibilidad(
        lugarId, fechaFutura, horaInicio, horaFin
    )
    
    // Debe ser true si no hay otras reservas
    assertTrue(disponible)
}
```

## 📝 Archivos Modificados

| Archivo | Cambios |
|---------|---------|
| `data/FirestoreRepository.kt` | ✅ Función `esFechaHoraFutura()` agregada |
| `data/FirestoreRepository.kt` | ✅ Validación en `verificarDisponibilidad()` |
| `data/FirestoreRepository.kt` | ✅ Filtrado en `obtenerBloquesDisponibles()` |
| `data/FirestoreRepository.kt` | ✅ Imports: SimpleDateFormat, Locale |
| `ReservaFragment.kt` | ✅ Validación pre-submit en UI |
| `ReservaFragment.kt` | ✅ Función local `esFechaHoraFutura()` |
| `ReservaFragment.kt` | ✅ Mensajes de error mejorados |

## 🚀 Próximas Mejoras

- [ ] Validación de tiempo mínimo de anticipación (ej: 30 minutos)
- [ ] Bloquear horarios muy cercanos (ej: no permitir reservas con menos de 1 hora de anticipación)
- [ ] Notificación cuando una reserva activa pase su hora de inicio
- [ ] Limpieza automática de reservas vencidas
- [ ] Dashboard de estadísticas de reservas pasadas vs futuras

## 📚 Referencias

- [SimpleDateFormat Documentation](https://developer.android.com/reference/java/text/SimpleDateFormat)
- [Date Comparison in Kotlin](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.comparisons/)

---

**Estado:** ✅ Implementado y funcional  
**Versión:** 1.0  
**Última actualización:** 4 de Noviembre, 2025
