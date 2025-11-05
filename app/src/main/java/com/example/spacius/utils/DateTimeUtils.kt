package com.example.spacius.utils

import com.example.spacius.data.BloqueHorario
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utilidades para manejo de fechas y horarios en el sistema de reservas
 */
object DateTimeUtils {
    
    // Formatos de fecha y hora consistentes
    private const val FORMATO_FECHA = "yyyy-MM-dd"
    private const val FORMATO_FECHA_HORA = "yyyy-MM-dd HH:mm"
    private const val FORMATO_HORA = "HH:mm"
    
    /**
     * Obtener SimpleDateFormat para fecha (yyyy-MM-dd)
     */
    fun getDateFormat(): SimpleDateFormat {
        return SimpleDateFormat(FORMATO_FECHA, Locale.getDefault())
    }
    
    /**
     * Obtener SimpleDateFormat para fecha y hora (yyyy-MM-dd HH:mm)
     */
    fun getDateTimeFormat(): SimpleDateFormat {
        return SimpleDateFormat(FORMATO_FECHA_HORA, Locale.getDefault())
    }
    
    /**
     * Obtener SimpleDateFormat para hora (HH:mm)
     */
    fun getTimeFormat(): SimpleDateFormat {
        return SimpleDateFormat(FORMATO_HORA, Locale.getDefault())
    }
    
    /**
     * Verificar si una fecha y hora están en el futuro
     * @param fecha Fecha en formato yyyy-MM-dd
     * @param hora Hora en formato HH:mm
     * @return true si la fecha/hora es futura, false si es pasada o hay error
     */
    fun esFechaHoraFutura(fecha: String, hora: String): Boolean {
        return try {
            val fechaHoraReserva = getDateTimeFormat().parse("$fecha $hora")
            val ahora = Date()
            
            fechaHoraReserva?.after(ahora) ?: false
        } catch (e: Exception) {
            android.util.Log.e("DateTimeUtils", "Error al validar fecha futura: ${e.message}")
            false // Por seguridad, rechazar en caso de error
        }
    }
    
    /**
     * Convertir hora en formato HH:mm a minutos desde medianoche
     * @param hora Hora en formato HH:mm
     * @return Minutos desde medianoche (0-1439)
     */
    fun convertirHoraAMinutos(hora: String): Int {
        return try {
            val partes = hora.split(":")
            val horas = partes[0].toInt()
            val minutos = partes[1].toInt()
            horas * 60 + minutos
        } catch (e: Exception) {
            android.util.Log.e("DateTimeUtils", "Error al convertir hora a minutos: ${e.message}")
            0
        }
    }
    
    /**
     * Verificar si dos rangos de horarios se solapan
     * @return true si hay conflicto/solapamiento, false si no hay conflicto
     */
    fun hayConflictoHorario(
        nuevaInicio: String, 
        nuevaFin: String, 
        existenteInicio: String, 
        existenteFin: String
    ): Boolean {
        return try {
            val nuevaInicioMin = convertirHoraAMinutos(nuevaInicio)
            val nuevaFinMin = convertirHoraAMinutos(nuevaFin)
            val existenteInicioMin = convertirHoraAMinutos(existenteInicio)
            val existenteFinMin = convertirHoraAMinutos(existenteFin)
            
            // No hay conflicto si: nueva termina antes de que existente comience O nueva comienza después de que existente termine
            val noHayConflicto = (nuevaFinMin <= existenteInicioMin) || (nuevaInicioMin >= existenteFinMin)
            
            !noHayConflicto
            
        } catch (e: Exception) {
            android.util.Log.e("DateTimeUtils", "Error al verificar conflicto: ${e.message}")
            true // En caso de error, asumir conflicto por seguridad
        }
    }
    
    /**
     * Formatear fecha desde Date a String (yyyy-MM-dd)
     */
    fun formatearFecha(fecha: Date): String {
        return getDateFormat().format(fecha)
    }
    
    /**
     * Formatear fecha y hora desde Date a String (yyyy-MM-dd HH:mm)
     */
    fun formatearFechaHora(fecha: Date): String {
        return getDateTimeFormat().format(fecha)
    }
}

/**
 * Utilidades para generar bloques horarios del sistema
 */
object HorarioUtils {
    
    /**
     * Generar lista de bloques horarios disponibles
     * Horario: 8:00 AM - 9:45 PM
     * Duración: 1h45min por bloque
     * Descanso: 15 minutos entre bloques
     */
    fun generarBloquesHorarios(): List<BloqueHorario> {
        return listOf(
            BloqueHorario(1, "08:00", "09:45", "8:00 AM - 9:45 AM"),
            BloqueHorario(2, "10:00", "11:45", "10:00 AM - 11:45 AM"),
            BloqueHorario(3, "12:00", "13:45", "12:00 PM - 1:45 PM"),
            BloqueHorario(4, "14:00", "15:45", "2:00 PM - 3:45 PM"),
            BloqueHorario(5, "16:00", "17:45", "4:00 PM - 5:45 PM"),
            BloqueHorario(6, "18:00", "19:45", "6:00 PM - 7:45 PM"),
            BloqueHorario(7, "20:00", "21:45", "8:00 PM - 9:45 PM")
        )
    }
}
