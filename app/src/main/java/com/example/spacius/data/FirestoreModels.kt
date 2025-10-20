package com.example.spacius.data

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Modelo de Lugar para Firestore
 * Representa los espacios públicos disponibles para reservar
 */
data class LugarFirestore(
    @DocumentId
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val imagenUrl: String = "",
    val fechaDisponible: String = "",
    val horaDisponible: String = "",
    val categoria: String = "deportivo", // deportivo, recreativo, cultural
    val capacidadMaxima: Int = 50,
    val activo: Boolean = true,
    @ServerTimestamp
    val fechaCreacion: Date? = null,
    @ServerTimestamp
    val fechaActualizacion: Date? = null
) {
    // Constructor vacío requerido por Firestore
    constructor() : this(
        id = "",
        nombre = "",
        descripcion = "",
        latitud = 0.0,
        longitud = 0.0,
        imagenUrl = "",
        fechaDisponible = "",
        horaDisponible = "",
        categoria = "deportivo",
        capacidadMaxima = 50,
        activo = true,
        fechaCreacion = null,
        fechaActualizacion = null
    )
}

/**
 * Modelo de Reserva para Firestore
 * Representa las reservas realizadas por los usuarios
 */
data class ReservaFirestore(
    @DocumentId
    val id: String = "",
    val lugarId: String = "",
    val lugarNombre: String = "", // Desnormalizado para consultas rápidas
    val usuarioId: String = "", // ID del usuario de Firebase Auth
    val usuarioEmail: String = "", // Email del usuario para referencia
    val usuarioNombre: String = "", // Nombre del usuario
    val fecha: String = "", // Formato: "yyyy-MM-dd"
    val horaInicio: String = "", // Formato: "HH:mm"
    val horaFin: String = "", // Formato: "HH:mm"
    val estado: String = "activa", // activa, cancelada, completada
    val notas: String = "", // Notas adicionales del usuario
    @ServerTimestamp
    val fechaCreacion: Date? = null,
    @ServerTimestamp
    val fechaActualizacion: Date? = null
) {
    // Constructor vacío requerido por Firestore
    constructor() : this(
        id = "",
        lugarId = "",
        lugarNombre = "",
        usuarioId = "",
        usuarioEmail = "",
        usuarioNombre = "",
        fecha = "",
        horaInicio = "",
        horaFin = "",
        estado = "activa",
        notas = "",
        fechaCreacion = null,
        fechaActualizacion = null
    )
}

/**
 * Modelo para estadísticas de uso (opcional)
 */
data class EstadisticaLugar(
    @DocumentId
    val lugarId: String = "",
    val totalReservas: Int = 0,
    val totalUsuarios: Int = 0,
    val promedioCalificacion: Double = 0.0,
    @ServerTimestamp
    val ultimaActualizacion: Date? = null
) {
    constructor() : this("", 0, 0, 0.0, null)
}