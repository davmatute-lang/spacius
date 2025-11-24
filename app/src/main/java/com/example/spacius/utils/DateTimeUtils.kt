package com.example.spacius.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {
    
    /**
     * Verifica si una fecha y hora están en el futuro
     */
    fun esFechaHoraFutura(fecha: String, hora: String): Boolean {
        return try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val fechaHora = dateFormat.parse("$fecha $hora") ?: return false
            fechaHora.after(Date())
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Verifica si hay conflicto entre dos rangos de horarios
     */
    fun hayConflictoHorario(
        inicio1: String,
        fin1: String,
        inicio2: String,
        fin2: String
    ): Boolean {
        return try {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            
            val start1 = timeFormat.parse(inicio1)?.time ?: return false
            val end1 = timeFormat.parse(fin1)?.time ?: return false
            val start2 = timeFormat.parse(inicio2)?.time ?: return false
            val end2 = timeFormat.parse(fin2)?.time ?: return false
            
            // Verifica si hay solapamiento
            (start1 < end2 && end1 > start2)
        } catch (e: Exception) {
            false
        }
    }
}