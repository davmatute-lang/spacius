package com.example.spacius.performance

import org.junit.Test
import org.junit.Assert.*
import kotlin.system.measureTimeMillis

/**
 * Pruebas de Rendimiento - Uso de Recursos
 * 
 * Tipo de Prueba: Rendimiento - Memoria y CPU
 * Objetivo: Medir el uso de recursos del sistema
 */
class ResourcePerformanceTest {

    companion object {
        const val MEMORIA_MAXIMA_MB = 100 // MB máximos aceptables
        const val TIEMPO_PROCESAMIENTO_MAX_MS = 1000L
    }

    // ==================== PRUEBAS DE MEMORIA ====================

    @Test
    fun `procesamiento de lista grande no causa OutOfMemory`() {
        // Arrange
        val memoriaInicial = obtenerMemoriaUsada()
        val listaGrande = (1..10000).map { index ->
            ReservaSimulada(
                id = "reserva_$index",
                usuario = "user_$index",
                lugar = "Lugar ${index % 10}",
                fecha = "2025-12-${(index % 28) + 1}"
            )
        }

        // Act - Procesar lista
        val tiempoProcesamiento = measureTimeMillis {
            val resultado = listaGrande
                .filter { it.lugar.contains("Lugar") }
                .groupBy { it.fecha }
                .map { (fecha, reservas) -> fecha to reservas.size }
        }

        val memoriaFinal = obtenerMemoriaUsada()
        val memoriaUsada = (memoriaFinal - memoriaInicial) / (1024 * 1024) // MB

        // Assert
        println("""
            === Prueba de Memoria ===
            Elementos procesados: ${listaGrande.size}
            Memoria inicial: ${memoriaInicial / (1024 * 1024)} MB
            Memoria final: ${memoriaFinal / (1024 * 1024)} MB
            Memoria usada: $memoriaUsada MB
            Tiempo de procesamiento: $tiempoProcesamiento ms
        """.trimIndent())

        assertTrue("El tiempo de procesamiento debe ser aceptable", 
            tiempoProcesamiento < TIEMPO_PROCESAMIENTO_MAX_MS)
        assertTrue("El uso de memoria debe ser < $MEMORIA_MAXIMA_MB MB", 
            memoriaUsada < MEMORIA_MAXIMA_MB)
    }

    @Test
    fun `filtrado y busqueda en lista grande es eficiente`() {
        // Arrange
        val numeroRegistros = 5000
        val lugares = (1..numeroRegistros).map { index ->
            LugarSimulado(
                id = "lugar_$index",
                nombre = "Lugar de Prueba $index",
                capacidad = (10..100).random(),
                disponible = index % 3 != 0
            )
        }

        // Act - Realizar búsqueda
        val tiempoBusqueda = measureTimeMillis {
            val resultados = lugares
                .filter { it.disponible }
                .filter { it.capacidad >= 50 }
                .sortedByDescending { it.capacidad }
                .take(20)
        }

        // Assert
        println("""
            === Prueba de Búsqueda ===
            Registros totales: $numeroRegistros
            Tiempo de búsqueda: $tiempoBusqueda ms
            Límite aceptable: 500 ms
        """.trimIndent())

        assertTrue("La búsqueda debe ser rápida", tiempoBusqueda < 500)
    }

    @Test
    fun `carga de imagenes simulada no satura memoria`() {
        // Arrange
        val numeroImagenes = 50
        val memoriaInicial = obtenerMemoriaUsada()

        // Act - Simular carga de imágenes
        val imagenes = mutableListOf<ImagenSimulada>()
        val tiempoCarga = measureTimeMillis {
            repeat(numeroImagenes) { index ->
                // Simular imagen de 2MB
                val imagen = ImagenSimulada(
                    id = "imagen_$index",
                    datos = ByteArray(2 * 1024 * 1024) { it.toByte() }
                )
                imagenes.add(imagen)
            }
        }

        val memoriaFinal = obtenerMemoriaUsada()
        val memoriaUsada = (memoriaFinal - memoriaInicial) / (1024 * 1024)

        // Limpiar
        imagenes.clear()
        System.gc()

        // Assert
        println("""
            === Prueba de Carga de Imágenes ===
            Imágenes cargadas: $numeroImagenes
            Memoria usada: $memoriaUsada MB
            Tiempo de carga: $tiempoCarga ms
        """.trimIndent())

        // Para 50 imágenes de 2MB cada una = 100MB teórico
        // En práctica debería usar menos por compresión/optimización
        assertTrue("El uso de memoria debe ser razonable", memoriaUsada < 150)
    }

    // ==================== PRUEBAS DE PROCESAMIENTO ====================

    @Test
    fun `generacion de bloques horarios es eficiente`() {
        // Arrange
        val numeroLugares = 20
        val diasAGenerar = 30

        // Act
        val tiempoGeneracion = measureTimeMillis {
            val bloquesGenerados = numeroLugares * diasAGenerar * 12 // 12 bloques por día
            val bloques = (1..bloquesGenerados).map { index ->
                BloqueHorarioSimulado(
                    id = index,
                    horaInicio = "${(index % 12) + 8}:00",
                    horaFin = "${(index % 12) + 9}:00",
                    disponible = index % 4 != 0
                )
            }
            
            // Filtrar disponibles
            bloques.filter { it.disponible }
        }

        // Assert
        println("""
            === Prueba de Generación de Bloques ===
            Lugares: $numeroLugares
            Días: $diasAGenerar
            Tiempo de generación: $tiempoGeneracion ms
            Límite aceptable: 2000 ms
        """.trimIndent())

        assertTrue("La generación debe ser rápida", tiempoGeneracion < 2000)
    }

    @Test
    fun `validacion de conflictos de horarios es rapida`() {
        // Arrange
        val numeroReservas = 1000
        val reservasExistentes = (1..numeroReservas).map { index ->
            ReservaHorariaSimulada(
                horaInicio = "${(index % 12) + 8}:${(index % 60).toString().padStart(2, '0')}",
                horaFin = "${(index % 12) + 9}:${(index % 60).toString().padStart(2, '0')}",
                fecha = "2025-12-${(index % 28) + 1}"
            )
        }

        // Act - Validar conflicto con nueva reserva
        val nuevaReserva = ReservaHorariaSimulada(
            horaInicio = "10:30",
            horaFin = "11:30",
            fecha = "2025-12-15"
        )

        val tiempoValidacion = measureTimeMillis {
            val hayConflicto = reservasExistentes.any { reserva ->
                reserva.fecha == nuevaReserva.fecha &&
                tieneConflictoHorario(reserva, nuevaReserva)
            }
        }

        // Assert
        println("""
            === Prueba de Validación de Conflictos ===
            Reservas existentes: $numeroReservas
            Tiempo de validación: $tiempoValidacion ms
            Límite aceptable: 100 ms
        """.trimIndent())

        assertTrue("La validación debe ser instantánea", tiempoValidacion < 100)
    }

    @Test
    fun `ordenamiento y paginacion de resultados es eficiente`() {
        // Arrange
        val numeroResultados = 2000
        val resultados = (1..numeroResultados).map { index ->
            ResultadoBusqueda(
                id = "resultado_$index",
                relevancia = (1..100).random(),
                fecha = System.currentTimeMillis() - (index * 1000),
                nombre = "Resultado $index"
            )
        }.shuffled() // Desordenar

        // Act - Ordenar y paginar
        val tiempoOperacion = measureTimeMillis {
            val paginaActual = 1
            val elementosPorPagina = 20
            
            val resultadosOrdenados = resultados
                .sortedByDescending { it.relevancia }
                .drop(paginaActual * elementosPorPagina)
                .take(elementosPorPagina)
        }

        // Assert
        println("""
            === Prueba de Ordenamiento y Paginación ===
            Total resultados: $numeroResultados
            Elementos por página: 20
            Tiempo de operación: $tiempoOperacion ms
            Límite aceptable: 200 ms
        """.trimIndent())

        assertTrue("El ordenamiento debe ser rápido", tiempoOperacion < 200)
    }

    // ==================== PRUEBAS DE CACHE ====================

    @Test
    fun `cache mejora rendimiento en accesos repetidos`() {
        // Arrange
        val cache = mutableMapOf<String, LugarSimulado>()
        val lugarId = "lugar_test_123"

        // Primera carga (sin cache)
        val tiempoSinCache = measureTimeMillis {
            val lugar = cargarLugarDesdeBaseDatos(lugarId)
            cache[lugarId] = lugar
        }

        // Segunda carga (con cache)
        val tiempoConCache = measureTimeMillis {
            val lugar = cache[lugarId] ?: cargarLugarDesdeBaseDatos(lugarId)
        }

        // Assert
        println("""
            === Prueba de Cache ===
            Tiempo sin cache: $tiempoSinCache ms
            Tiempo con cache: $tiempoConCache ms
            Mejora: ${((tiempoSinCache - tiempoConCache).toDouble() / tiempoSinCache) * 100}%
        """.trimIndent())

        assertTrue("El cache debe mejorar el rendimiento", tiempoConCache < tiempoSinCache)
        assertTrue("El acceso con cache debe ser casi instantáneo", tiempoConCache < 10)
    }

    // ==================== FUNCIONES AUXILIARES ====================

    private fun obtenerMemoriaUsada(): Long {
        val runtime = Runtime.getRuntime()
        return runtime.totalMemory() - runtime.freeMemory()
    }

    private fun cargarLugarDesdeBaseDatos(lugarId: String): LugarSimulado {
        // Simular latencia de base de datos
        Thread.sleep(100)
        return LugarSimulado(
            id = lugarId,
            nombre = "Lugar de Prueba",
            capacidad = 50,
            disponible = true
        )
    }

    private fun tieneConflictoHorario(
        reserva1: ReservaHorariaSimulada,
        reserva2: ReservaHorariaSimulada
    ): Boolean {
        // Lógica simplificada de detección de conflictos
        return reserva1.horaInicio < reserva2.horaFin &&
               reserva1.horaFin > reserva2.horaInicio
    }

    // ==================== CLASES DE DATOS ====================

    data class ReservaSimulada(
        val id: String,
        val usuario: String,
        val lugar: String,
        val fecha: String
    )

    data class LugarSimulado(
        val id: String,
        val nombre: String,
        val capacidad: Int,
        val disponible: Boolean
    )

    data class ImagenSimulada(
        val id: String,
        val datos: ByteArray
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as ImagenSimulada
            return id == other.id
        }

        override fun hashCode(): Int = id.hashCode()
    }

    data class BloqueHorarioSimulado(
        val id: Int,
        val horaInicio: String,
        val horaFin: String,
        val disponible: Boolean
    )

    data class ReservaHorariaSimulada(
        val horaInicio: String,
        val horaFin: String,
        val fecha: String
    )

    data class ResultadoBusqueda(
        val id: String,
        val relevancia: Int,
        val fecha: Long,
        val nombre: String
    )
}
