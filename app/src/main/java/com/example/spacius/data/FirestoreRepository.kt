package com.example.spacius.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlin.Exception

/**
 * Repositorio para manejar operaciones de Firestore
 * Centraliza toda la lógica de base de datos en la nube
 */
class FirestoreRepository {
    
    private val db: FirebaseFirestore = Firebase.firestore
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    
    companion object {
        private const val TAG = "FirestoreRepository"
        
        // Nombres de colecciones
        const val COLLECTION_LUGARES = "lugares"
        const val COLLECTION_RESERVAS = "reservas"
        const val COLLECTION_ESTADISTICAS = "estadisticas"
    }
    
    // ============================================
    // GESTIÓN DE LUGARES
    // ============================================
    
    /**
     * Obtener todos los lugares disponibles en Firestore
     */
    suspend fun obtenerLugares(): List<LugarFirestore> {
        return try {
            val snapshot = db.collection(COLLECTION_LUGARES)
                .whereEqualTo("activo", true)
                .get()
                .await()
            
            snapshot.toObjects(LugarFirestore::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener lugares: ${e.message}")
            emptyList()
        }
    }
    
    /**
     * Obtener lugares disponibles (no reservados por el usuario actual)
     */
    suspend fun obtenerLugaresDisponibles(): List<LugarFirestore> {
        return try {
            Log.d(TAG, "Iniciando búsqueda de lugares disponibles...")
            
            // Primero obtenemos todos los lugares
            val todosLugares = obtenerLugares()
            Log.d(TAG, "Total lugares encontrados: ${todosLugares.size}")
            
            // Luego obtenemos las reservas activas del usuario
            val lugaresReservados = obtenerLugaresReservados()
            Log.d(TAG, "Lugares reservados por usuario: ${lugaresReservados.size}")
            
            // Filtramos en memoria para evitar consultas complejas
            val lugaresDisponibles = todosLugares.filter { lugar -> 
                val estaReservado = lugaresReservados.any { reserva -> 
                    reserva.lugarId == lugar.id 
                }
                !estaReservado
            }
            
            Log.d(TAG, "Lugares disponibles: ${lugaresDisponibles.size}")
            lugaresDisponibles
            
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener lugares disponibles: ${e.message}")
            emptyList()
        }
    }
    
    /**
     * Obtener un lugar específico por ID
     */
    suspend fun obtenerLugarPorId(lugarId: String): LugarFirestore? {
        return try {
            val snapshot = db.collection(COLLECTION_LUGARES)
                .document(lugarId)
                .get()
                .await()
            
            snapshot.toObject(LugarFirestore::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener lugar por ID: ${e.message}")
            null
        }
    }
    
    /**
     * Inicializar lugares predefinidos (solo la primera vez)
     */
    suspend fun inicializarLugaresPredefinidos(): Boolean {
        return try {
            val lugaresExistentes = db.collection(COLLECTION_LUGARES).get().await()
            
            if (lugaresExistentes.isEmpty) {
                val lugaresPredefinidos = obtenerLugaresPredefinidos()
                
                lugaresPredefinidos.forEach { lugar ->
                    db.collection(COLLECTION_LUGARES).add(lugar).await()
                }
                
                Log.d(TAG, "Lugares predefinidos inicializados: ${lugaresPredefinidos.size}")
                true
            } else {
                Log.d(TAG, "Los lugares ya están inicializados: ${lugaresExistentes.size()} lugares encontrados")
                
                // Verificar y limpiar duplicados si existen
                limpiarDuplicados()
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al inicializar lugares: ${e.message}")
            false
        }
    }
    
    /**
     * Limpiar lugares duplicados por nombre
     */
    private suspend fun limpiarDuplicados() {
        try {
            val lugares = db.collection(COLLECTION_LUGARES).get().await()
            val lugaresAgrupados = lugares.documents.groupBy { 
                it.toObject(LugarFirestore::class.java)?.nombre 
            }
            
            // Para cada grupo, mantener solo el primero y eliminar el resto
            lugaresAgrupados.forEach { (nombre, documentos) ->
                if (documentos.size > 1) {
                    Log.d(TAG, "Encontrados ${documentos.size} duplicados de: $nombre")
                    // Mantener el primero, eliminar el resto
                    documentos.drop(1).forEach { documento ->
                        db.collection(COLLECTION_LUGARES).document(documento.id).delete().await()
                        Log.d(TAG, "Eliminado duplicado: ${documento.id}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al limpiar duplicados: ${e.message}")
        }
    }
    
    /**
     * Función pública para limpiar duplicados manualmente
     */
    suspend fun limpiarDuplicadosManualmente(): Boolean {
        return try {
            limpiarDuplicados()
            Log.d(TAG, "Limpieza de duplicados completada")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error en limpieza manual: ${e.message}")
            false
        }
    }
    
    // ============================================
    // GESTIÓN DE RESERVAS
    // ============================================
    
    /**
     * Crear una nueva reserva
     */
    suspend fun crearReserva(reserva: ReservaFirestore): Boolean {
        return try {
            val usuarioActual = auth.currentUser
            if (usuarioActual == null) {
                Log.e(TAG, "Usuario no autenticado")
                return false
            }
            
            val reservaCompleta = reserva.copy(
                usuarioId = usuarioActual.uid,
                usuarioEmail = usuarioActual.email ?: "",
                usuarioNombre = usuarioActual.displayName ?: usuarioActual.email ?: "Usuario"
            )
            
            db.collection(COLLECTION_RESERVAS).add(reservaCompleta).await()
            Log.d(TAG, "Reserva creada exitosamente")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al crear reserva: ${e.message}")
            false
        }
    }
    
    /**
     * Obtener reservas del usuario actual
     */
    suspend fun obtenerReservasUsuario(): List<ReservaFirestore> {
        return try {
            val usuarioActual = auth.currentUser
            if (usuarioActual == null) {
                Log.e(TAG, "Usuario no autenticado")
                return emptyList()
            }
            
            // Consulta simplificada - solo por usuario, filtrar estado en memoria
            val snapshot = db.collection(COLLECTION_RESERVAS)
                .whereEqualTo("usuarioId", usuarioActual.uid)
                .get()
                .await()
            
            val todasReservas = snapshot.toObjects(ReservaFirestore::class.java)
            // Filtrar por estado en memoria y ordenar
            todasReservas
                .filter { it.estado == "activa" }
                .sortedByDescending { it.fechaCreacion }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener reservas del usuario: ${e.message}")
            emptyList()
        }
    }
    
    /**
     * Obtener lugares reservados por el usuario actual
     */
    suspend fun obtenerLugaresReservados(): List<ReservaFirestore> {
        return try {
            val usuarioActual = auth.currentUser
            if (usuarioActual == null) {
                Log.d(TAG, "Usuario no autenticado - no hay reservas")
                return emptyList()
            }
            
            Log.d(TAG, "Obteniendo reservas para usuario: ${usuarioActual.uid}")
            
            // Consulta simplificada - solo por usuario, filtrar estado en memoria
            val snapshot = db.collection(COLLECTION_RESERVAS)
                .whereEqualTo("usuarioId", usuarioActual.uid)
                .get()
                .await()
            
            val todasReservas = snapshot.toObjects(ReservaFirestore::class.java)
            Log.d(TAG, "Total reservas del usuario: ${todasReservas.size}")
            
            // Filtrar solo las activas en memoria
            val reservasActivas = todasReservas.filter { it.estado == "activa" }
            Log.d(TAG, "Reservas activas: ${reservasActivas.size}")
            
            reservasActivas
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener lugares reservados: ${e.message}")
            emptyList()
        }
    }
    
    /**
     * Cancelar una reserva
     */
    suspend fun cancelarReserva(reservaId: String): Boolean {
        return try {
            db.collection(COLLECTION_RESERVAS)
                .document(reservaId)
                .update("estado", "cancelada")
                .await()
            
            Log.d(TAG, "Reserva cancelada exitosamente")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al cancelar reserva: ${e.message}")
            false
        }
    }
    
    /**
     * Eliminar una reserva completamente de Firestore
     */
    suspend fun eliminarReserva(reservaId: String): Boolean {
        return try {
            db.collection(COLLECTION_RESERVAS)
                .document(reservaId)
                .delete()
                .await()
            
            Log.d(TAG, "Reserva eliminada exitosamente")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar reserva: ${e.message}")
            false
        }
    }
    
    // ============================================
    // DATOS PREDEFINIDOS
    // ============================================
    
    private fun obtenerLugaresPredefinidos(): List<LugarFirestore> = listOf(
        LugarFirestore(
            nombre = "Canchas del Parque Samanes",
            descripcion = "En el Parque Samanes encontrarás más de 50 canchas modernas y seguras, diseñadas para que vivas la pasión del deporte al máximo.",
            latitud = -2.1022530106411046,
            longitud = -79.90182885626803,
            imagenUrl = "https://lh3.googleusercontent.com/gps-cs-s/AC9h4noMQWJj0YAyB1nXFdKLZ8XTw5JqL0HcG5BkFJX8pJN0E2RLV2YkL4tFxVNpY5Cs5g2X9QF7X6zT9v5E8Xg7Y8qM3Wp1Rc9Lc1F2vZ5XBf8Yj0Px7Qz5VcW8Wr3Fg5Hm6Kj8Qx2VZ1T9wE7N=w426-h240-k-no",
            fechaDisponible = "Lunes a Domingo",
            horaDisponible = "06h00 a 20h00",
            categoria = "deportivo",
            capacidadMaxima = 22
        ),
        LugarFirestore(
            nombre = "Área de picnic del Parque Samanes",
            descripcion = "El área de picnic del Parque Samanes es el rincón ideal para escapar de la rutina y disfrutar al aire libre.",
            latitud = -2.105220191117523,
            longitud = -79.90329556145007,
            imagenUrl = "https://lh3.googleusercontent.com/gps-cs-s/AC9h4nrqjHXzMCHOf74sqpNq9ir-rhobLV9fBSXxXlIdaZV9dYVX-jjmgUx9yaCLVLL38Vm3GZ-hAU_Q6TIZbUY8Sze25lTvLcAFxW_M0EUSa1cWRSkAG525JkdPeUkXr_tFXXwm0_p4=w408-h306-k-no",
            fechaDisponible = "Lunes a Domingo",
            horaDisponible = "10h00 a 18h00",
            categoria = "recreativo",
            capacidadMaxima = 200
        ),
        LugarFirestore(
            nombre = "Canchas de Vóley Playero",
            descripcion = "En el Parque Samanes tienes la oportunidad de sentirte en la playa sin salir de la ciudad gracias a sus modernas canchas de vóley playero.",
            latitud = -2.1014850476965927,
            longitud = -79.89866715429523,
            imagenUrl = "https://lh3.googleusercontent.com/gps-cs-s/AC9h4noE7va4JdGf2zQmeTXtokNPeKQ7FW2lkC8zSyWPjec9wlcb22t0SJyQa3UIkZqYINFxAkYKvvTxsZwKYUw9Wk6DKIw0CUVZjq18W6BsH8dJwgot8s6hlpPrrNjKbAJ22kxHNfc-DA=w426-h240-k-no",
            fechaDisponible = "Lunes a Domingo",
            horaDisponible = "10h00 a 18h00",
            categoria = "deportivo",
            capacidadMaxima = 40
        ),
        LugarFirestore(
            nombre = "Parque de la Octava Alborada",
            descripcion = "En el corazón de la Alborada Octava Etapa se encuentra este parque lleno de vida, perfecto para compartir con la familia y amigos.",
            latitud = -2.137780953469543,
            longitud = -79.90288114274225,
            imagenUrl = "https://lh3.googleusercontent.com/gps-cs-s/AC9h4nrV67Hero0IbxysevA2Y-LcP6V0TeHDIXB5Rwt3v0sXbTSerXLVOlxN6BnaaIpWdjeVRRrihnJV9OVNVFYC6Pmw4ESZs0bgxQBOAtxtjIZLA9KN8rngOa-aEw94JHT7WMIshekS3g=w425-h240-k-no",
            fechaDisponible = "Jueves a Domingo",
            horaDisponible = "12h00 a 16h00",
            categoria = "recreativo",
            capacidadMaxima = 150
        ),
        LugarFirestore(
            nombre = "Parque Acuático Bastión Popular",
            descripcion = "En el norte de Guayaquil, el Parque Acuático Bastión Popular es el lugar ideal para refrescarse y divertirse en familia.",
            latitud = -2.090274535360757,
            longitud = -79.93658413155697,
            imagenUrl = "https://lh3.googleusercontent.com/gps-cs-s/AC9h4notjwG3qSwr2OLOEH0-pC0fmEn1CjPMjIpf-8rEVsCaFKAFdAaO6bOmc4EcNB-2hGTzYMjHwfSRfvnwn9Pf-clP5IyIFsZmLylU3fdRv5XVCdJa5tEmwkvBO_tiqIWFU6_2FZWJ=w408-h271-k-no",
            fechaDisponible = "Miércoles a Sábado",
            horaDisponible = "10h00 a 18h00",
            categoria = "recreativo",
            capacidadMaxima = 300
        ),
        LugarFirestore(
            nombre = "Parque Acuático Juan Montalvo",
            descripcion = "Ubicado en el sector norte de Guayaquil, el Parque Acuático Juan Montalvo es el lugar perfecto para pasar un día lleno de diversión.",
            latitud = -2.1226221947933346,
            longitud = -79.92231074271157,
            imagenUrl = "https://lh3.googleusercontent.com/gps-cs-s/AC9h4noJOi1UDlOpot23ysVmq_Bm8WhjVD50EUzGFNRxmyzmlYUztgaznnac-IQIjlSYPRR4LbgLCai1cp71F5Xszk3UMI6GYc3YDCOP0urVSp7TT12i9jkzd4aJO7mX0DksGZT_LMlP=w408-h306-k-no",
            fechaDisponible = "Miércoles a Sábado",
            horaDisponible = "11h00 a 18h00",
            categoria = "recreativo",
            capacidadMaxima = 500
        ),
        LugarFirestore(
            nombre = "Piscina Pública del Sur",
            descripcion = "Disfruta de un refrescante baño en la Piscina Pública del Sur, un espacio moderno y seguro para toda la familia.",
            latitud = -2.2235555052952903,
            longitud = -79.90513890926766,
            imagenUrl = "https://lh3.googleusercontent.com/gps-cs-s/AC9h4noxzKOgQFxN2YvPc8T1VpKs6Yz5QrW9XbDkMcTgL7Rf2HpNvB4Js8K6M0zA3PbQc5E9Rf1Lk7Xt2YhNdG6F8YtQ0M2Rv7Wj9Hp3Sk1BgC5XqR8Nt4LmPz6VcJ3=w408-h271-k-no",
            fechaDisponible = "Martes a Sábado",
            horaDisponible = "09h00 a 17h00",
            categoria = "recreativo",
            capacidadMaxima = 80
        ),
        LugarFirestore(
            nombre = "Centro Cultural Guayaquil",
            descripcion = "Sumérgete en la cultura ecuatoriana en el Centro Cultural Guayaquil, donde el arte y la historia cobran vida.",
            latitud = -2.1969722123139033,
            longitud = -79.88670921075344,
            imagenUrl = "https://lh3.googleusercontent.com/gps-cs-s/AC9h4noxctYUeQffpBdPb7OGi5xhpPD5iOTeQGk5UPRxyYmC7sobcu1DtyYfiZJ1EUfbnLSj79BHSALIEvRcpzTtsxNGu4iHN8H_lS5x35Jy-S3YC6BzRSW2ycW3eOa_fTbLybVXtI8hJQ=w408-h317-k-no",
            fechaDisponible = "Solo Sábado",
            horaDisponible = "14h00 a 16h00",
            categoria = "cultural",
            capacidadMaxima = 50
        )
    )
}