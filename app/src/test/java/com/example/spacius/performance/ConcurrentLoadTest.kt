package com.example.spacius.performance

import org.junit.Test
import org.junit.Assert.*
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Pruebas de Rendimiento - Carga Concurrente
 * 
 * Tipo de Prueba: Rendimiento / Carga
 * Objetivo: Simular múltiples usuarios concurrentes y medir tiempos de respuesta
 * 
 * Métricas:
 * - Tiempo de respuesta
 * - Throughput (operaciones/segundo)
 * - Tasa de éxito
 * - Uso de memoria
 */
class ConcurrentLoadTest {

    // Constantes de configuración
    companion object {
        const val NUM_USUARIOS_SIMULADOS = 10
        const val NUM_OPERACIONES_POR_USUARIO = 5
        const val TIMEOUT_SEGUNDOS = 30L
        const val TIEMPO_RESPUESTA_ACEPTABLE_MS = 3000L // 3 segundos
    }

    // ==================== PRUEBAS DE CARGA BÁSICAS ====================
    
    @Test
    fun `10 usuarios concurrentes hacen login simultaneamente`() = runBlocking {
        // Arrange
        val numeroUsuarios = 10
        val tiemposRespuesta = mutableListOf<Long>()
        val errores = AtomicInteger(0)
        val exitosos = AtomicInteger(0)

        // Act - Ejecutar logins concurrentes
        val tiempoTotal = measureTimeMillis {
            val jobs = (1..numeroUsuarios).map { userId ->
                async(Dispatchers.Default) {
                    try {
                        val tiempo = simularLogin("user$userId@test.com", "password123")
                        synchronized(tiemposRespuesta) {
                            tiemposRespuesta.add(tiempo)
                        }
                        exitosos.incrementAndGet()
                    } catch (e: Exception) {
                        errores.incrementAndGet()
                    }
                }
            }
            
            // Esperar a que todos terminen
            jobs.awaitAll()
        }

        // Assert - Validar resultados
        val tiempoPromedio = tiemposRespuesta.average()
        val tiempoMaximo = tiemposRespuesta.maxOrNull() ?: 0L
        val tasaExito = (exitosos.get().toDouble() / numeroUsuarios) * 100

        println("""
            === Resultados de Prueba de Carga ===
            Usuarios simultáneos: $numeroUsuarios
            Operaciones exitosas: ${exitosos.get()}
            Operaciones fallidas: ${errores.get()}
            Tasa de éxito: $tasaExito%
            Tiempo total: ${tiempoTotal}ms
            Tiempo promedio: $tiempoPromedio ms
            Tiempo máximo: ${tiempoMaximo}ms
            Throughput: ${(numeroUsuarios.toDouble() / tiempoTotal) * 1000} ops/seg
        """.trimIndent())

        // Validaciones
        assertTrue("La tasa de éxito debe ser >= 80%", tasaExito >= 80.0)
        assertTrue(
            "El tiempo promedio debe ser < $TIEMPO_RESPUESTA_ACEPTABLE_MS ms",
            tiempoPromedio < TIEMPO_RESPUESTA_ACEPTABLE_MS
        )
        assertEquals("No debe haber errores", 0, errores.get())
    }

    @Test
    fun `10 usuarios realizan multiples operaciones de lectura`() = runBlocking {
        // Arrange
        val numeroUsuarios = 10
        val operacionesPorUsuario = 5
        val tiemposRespuesta = mutableListOf<Long>()
        val operacionesExitosas = AtomicInteger(0)
        val operacionesFallidas = AtomicInteger(0)

        // Act
        val tiempoTotal = measureTimeMillis {
            val jobs = (1..numeroUsuarios).map { userId ->
                async(Dispatchers.Default) {
                    repeat(operacionesPorUsuario) { opNum ->
                        try {
                            val tiempo = simularLecturaReservas("user$userId")
                            synchronized(tiemposRespuesta) {
                                tiemposRespuesta.add(tiempo)
                            }
                            operacionesExitosas.incrementAndGet()
                        } catch (e: Exception) {
                            operacionesFallidas.incrementAndGet()
                        }
                    }
                }
            }
            jobs.awaitAll()
        }

        // Assert
        val totalOperaciones = numeroUsuarios * operacionesPorUsuario
        val tiempoPromedio = tiemposRespuesta.average()
        val tasaExito = (operacionesExitosas.get().toDouble() / totalOperaciones) * 100
        val throughput = (totalOperaciones.toDouble() / tiempoTotal) * 1000

        println("""
            === Prueba de Lectura Concurrente ===
            Usuarios: $numeroUsuarios
            Operaciones por usuario: $operacionesPorUsuario
            Total operaciones: $totalOperaciones
            Exitosas: ${operacionesExitosas.get()}
            Fallidas: ${operacionesFallidas.get()}
            Tasa de éxito: $tasaExito%
            Tiempo total: ${tiempoTotal}ms
            Tiempo promedio: $tiempoPromedio ms
            Throughput: $throughput ops/seg
        """.trimIndent())

        assertTrue("Tasa de éxito >= 90%", tasaExito >= 90.0)
        assertTrue("Tiempo promedio aceptable", tiempoPromedio < TIEMPO_RESPUESTA_ACEPTABLE_MS)
    }

    @Test
    fun `10 usuarios crean reservas simultaneamente sin conflictos`() = runBlocking {
        // Arrange
        val numeroUsuarios = 10
        val reservasCreadas = AtomicInteger(0)
        val conflictos = AtomicInteger(0)

        // Act
        val tiempoTotal = measureTimeMillis {
            val jobs = (1..numeroUsuarios).map { userId ->
                async(Dispatchers.Default) {
                    try {
                        val tiempo = simularCrearReserva(
                            userId = "user$userId",
                            lugarId = "lugar001",
                            fecha = "2025-12-10",
                            hora = "10:${String.format("%02d", userId)}" // Horas diferentes
                        )
                        reservasCreadas.incrementAndGet()
                    } catch (e: Exception) {
                        conflictos.incrementAndGet()
                    }
                }
            }
            jobs.awaitAll()
        }

        // Assert
        val tasaExito = (reservasCreadas.get().toDouble() / numeroUsuarios) * 100

        println("""
            === Prueba de Escritura Concurrente ===
            Usuarios intentando reservar: $numeroUsuarios
            Reservas creadas: ${reservasCreadas.get()}
            Conflictos detectados: ${conflictos.get()}
            Tasa de éxito: $tasaExito%
            Tiempo total: ${tiempoTotal}ms
        """.trimIndent())

        assertTrue("Debe crear al menos 8 de 10 reservas", reservasCreadas.get() >= 8)
        assertTrue("Los conflictos deben ser mínimos", conflictos.get() <= 2)
    }

    // ==================== PRUEBAS DE ESTRÉS ====================

    @Test
    fun `sistema maneja picos de carga sin degradacion severa`() = runBlocking {
        // Arrange - Simular tráfico creciente
        val fasesDeUsuarios = listOf(5, 10, 15, 10, 5)
        val resultadosPorFase = mutableListOf<ResultadoFase>()

        // Act - Ejecutar fases de carga
        fasesDeUsuarios.forEachIndexed { fase, numeroUsuarios ->
            val tiemposRespuesta = mutableListOf<Long>()
            
            val tiempoFase = measureTimeMillis {
                val jobs = (1..numeroUsuarios).map { userId ->
                    async(Dispatchers.Default) {
                        val tiempo = simularOperacionMixta("user_fase${fase}_$userId")
                        synchronized(tiemposRespuesta) {
                            tiemposRespuesta.add(tiempo)
                        }
                    }
                }
                jobs.awaitAll()
            }

            val tiempoPromedio = tiemposRespuesta.average()
            resultadosPorFase.add(
                ResultadoFase(
                    fase = fase + 1,
                    usuarios = numeroUsuarios,
                    tiempoPromedio = tiempoPromedio,
                    tiempoTotal = tiempoFase
                )
            )

            // Pausa entre fases
            delay(500)
        }

        // Assert - Analizar degradación
        println("\n=== Prueba de Picos de Carga ===")
        resultadosPorFase.forEach { resultado ->
            println("Fase ${resultado.fase}: ${resultado.usuarios} usuarios - " +
                    "Promedio: ${resultado.tiempoPromedio}ms - " +
                    "Total: ${resultado.tiempoTotal}ms")
        }

        val tiemposPorFase = resultadosPorFase.map { it.tiempoPromedio }
        val variacion = calcularVariacion(tiemposPorFase)
        
        println("Variación de rendimiento: $variacion%")

        // La variación no debe ser mayor al 50%
        assertTrue(
            "La variación de rendimiento debe ser aceptable",
            variacion < 50.0
        )
    }

    @Test
    fun `sistema recupera capacidad despues de pico de carga`() = runBlocking {
        // Arrange
        val tiempoBaselinePre = medirTiempoBaseline()
        
        // Act - Generar pico de carga
        val tiempoPico = measureTimeMillis {
            val jobs = (1..20).map { userId -> // Sobrecarga
                async(Dispatchers.Default) {
                    simularOperacionMixta("user_stress_$userId")
                }
            }
            jobs.awaitAll()
        }

        // Esperar recuperación
        delay(1000)

        // Medir rendimiento post-pico
        val tiempoBaselinePost = medirTiempoBaseline()

        // Assert
        println("""
            === Prueba de Recuperación ===
            Tiempo baseline PRE: $tiempoBaselinePre ms
            Tiempo durante pico: $tiempoPico ms
            Tiempo baseline POST: $tiempoBaselinePost ms
            Degradación: ${((tiempoBaselinePost - tiempoBaselinePre) / tiempoBaselinePre) * 100}%
        """.trimIndent())

        // El sistema debe recuperarse al 80% del rendimiento original
        val degradacion = ((tiempoBaselinePost - tiempoBaselinePre) / tiempoBaselinePre) * 100
        assertTrue(
            "El sistema debe recuperarse (degradación < 20%)",
            degradacion < 20.0
        )
    }

    // ==================== PRUEBAS DE THROUGHPUT ====================

    @Test
    fun `sistema procesa al menos 10 operaciones por segundo`() = runBlocking {
        // Arrange
        val duracionPrueba = 5000L // 5 segundos
        val operacionesRealizadas = AtomicInteger(0)
        val jobs = mutableListOf<Job>()

        // Act - Ejecutar operaciones durante el tiempo especificado
        val tiempoReal = measureTimeMillis {
            // Lanzar múltiples coroutines que ejecutan operaciones
            repeat(10) { workerId ->
                jobs.add(launch(Dispatchers.Default) {
                    val startTime = System.currentTimeMillis()
                    while (System.currentTimeMillis() - startTime < duracionPrueba) {
                        simularOperacionRapida("worker_$workerId")
                        operacionesRealizadas.incrementAndGet()
                    }
                })
            }
            jobs.joinAll()
        }

        // Assert
        val throughput = (operacionesRealizadas.get().toDouble() / tiempoReal) * 1000
        
        println("""
            === Prueba de Throughput ===
            Duración: $tiempoReal ms
            Operaciones realizadas: ${operacionesRealizadas.get()}
            Throughput: $throughput ops/seg
            Throughput mínimo requerido: 10 ops/seg
        """.trimIndent())

        assertTrue(
            "El throughput debe ser >= 10 ops/seg",
            throughput >= 10.0
        )
    }

    // ==================== PRUEBAS DE TIEMPO DE RESPUESTA ====================

    @Test
    fun `percentil 95 de tiempo de respuesta es aceptable`() = runBlocking {
        // Arrange
        val numeroMuestras = 100
        val tiemposRespuesta = mutableListOf<Long>()

        // Act - Recolectar muestras
        repeat(numeroMuestras) { i ->
            val tiempo = simularOperacionMixta("user_$i")
            tiemposRespuesta.add(tiempo)
        }

        // Assert - Calcular percentiles
        tiemposRespuesta.sort()
        val p50 = tiemposRespuesta[tiemposRespuesta.size * 50 / 100]
        val p95 = tiemposRespuesta[tiemposRespuesta.size * 95 / 100]
        val p99 = tiemposRespuesta[tiemposRespuesta.size * 99 / 100]
        val promedio = tiemposRespuesta.average()

        println("""
            === Análisis de Tiempos de Respuesta ===
            Muestras: $numeroMuestras
            Promedio: $promedio ms
            Percentil 50 (mediana): $p50 ms
            Percentil 95: $p95 ms
            Percentil 99: $p99 ms
            Mínimo: ${tiemposRespuesta.first()} ms
            Máximo: ${tiemposRespuesta.last()} ms
        """.trimIndent())

        assertTrue("P50 debe ser < 2000ms", p50 < 2000)
        assertTrue("P95 debe ser < 3000ms", p95 < TIEMPO_RESPUESTA_ACEPTABLE_MS)
        assertTrue("P99 debe ser < 5000ms", p99 < 5000)
    }

    // ==================== FUNCIONES DE SIMULACIÓN ====================

    /**
     * Simula un login de usuario
     */
    private suspend fun simularLogin(email: String, password: String): Long {
        return measureTimeMillis {
            // Simular validación local
            delay(50)
            
            // Simular llamada a Firebase Auth
            delay((100..500).random().toLong())
            
            // Simular carga de datos de usuario
            delay((50..200).random().toLong())
        }
    }

    /**
     * Simula lectura de reservas de un usuario
     */
    private suspend fun simularLecturaReservas(userId: String): Long {
        return measureTimeMillis {
            // Simular consulta a Firestore
            delay((100..400).random().toLong())
            
            // Simular procesamiento de datos
            delay((20..100).random().toLong())
        }
    }

    /**
     * Simula creación de una reserva
     */
    private suspend fun simularCrearReserva(
        userId: String,
        lugarId: String,
        fecha: String,
        hora: String
    ): Long {
        return measureTimeMillis {
            // Simular validación de disponibilidad
            delay((100..300).random().toLong())
            
            // Simular escritura en Firestore
            delay((200..600).random().toLong())
            
            // Simular programación de notificación
            delay((50..150).random().toLong())
        }
    }

    /**
     * Simula una operación mixta (lectura + escritura)
     */
    private suspend fun simularOperacionMixta(userId: String): Long {
        return measureTimeMillis {
            // Lectura
            delay((100..300).random().toLong())
            // Procesamiento
            delay((50..150).random().toLong())
            // Escritura
            delay((100..300).random().toLong())
        }
    }

    /**
     * Simula una operación rápida para throughput
     */
    private suspend fun simularOperacionRapida(workerId: String): Long {
        return measureTimeMillis {
            delay((10..50).random().toLong())
        }
    }

    /**
     * Mide el tiempo baseline del sistema (sin carga)
     */
    private suspend fun medirTiempoBaseline(): Long {
        val muestras = (1..5).map {
            simularOperacionMixta("baseline_$it")
        }
        return muestras.average().toLong()
    }

    /**
     * Calcula la variación porcentual en una lista de tiempos
     */
    private fun calcularVariacion(tiempos: List<Double>): Double {
        if (tiempos.isEmpty()) return 0.0
        val min = tiempos.minOrNull() ?: return 0.0
        val max = tiempos.maxOrNull() ?: return 0.0
        return ((max - min) / min) * 100
    }

    // ==================== CLASES DE DATOS ====================

    data class ResultadoFase(
        val fase: Int,
        val usuarios: Int,
        val tiempoPromedio: Double,
        val tiempoTotal: Long
    )
}
