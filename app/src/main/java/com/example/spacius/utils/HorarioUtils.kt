package com.example.spacius.utils

import com.example.spacius.data.BloqueHorario
import java.text.SimpleDateFormat
import java.util.*

object HorarioUtils {
    
    /**
     * Valida que la hora de inicio sea anterior a la hora de fin
     */
    fun validarHorario(horaInicio: String, horaFin: String): Boolean {
        return try {
            val formato = SimpleDateFormat("HH:mm", Locale.getDefault())
            val inicio = formato.parse(horaInicio)?.time ?: return false
            val fin = formato.parse(horaFin)?.time ?: return false
            inicio < fin
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Convierte un horario a minutos desde medianoche
     */
    fun horarioAMinutos(hora: String): Int {
        return try {
            val partes = hora.split(":")
            val horas = partes[0].toInt()
            val minutos = partes[1].toInt()
            horas * 60 + minutos
        } catch (e: Exception) {
            0
        }
    }
    
    /**
     * Genera bloques de horarios disponibles basados en el horario de apertura y cierre
     * @param horaInicio Hora de apertura (formato HH:mm)
     * @param horaFin Hora de cierre (formato HH:mm)
     * @param duracionMinutos Duración de cada bloque en minutos (por defecto 60)
     * @return Lista de bloques horarios
     */
    fun generarBloquesHorarios(
        horaInicio: String = "08:00",
        horaFin: String = "20:00",
        duracionMinutos: Int = 60
    ): List<BloqueHorario> {
        val bloques = mutableListOf<BloqueHorario>()
        
        try {
            val formato = SimpleDateFormat("HH:mm", Locale.getDefault())
            val calendar = Calendar.getInstance()
            
            val inicio = formato.parse(horaInicio) ?: return emptyList()
            val fin = formato.parse(horaFin) ?: return emptyList()
            
            calendar.time = inicio
            var contador = 1
            
            while (calendar.time.before(fin)) {
                val bloqueInicio = formato.format(calendar.time)
                calendar.add(Calendar.MINUTE, duracionMinutos)
                
                if (calendar.time.after(fin)) break
                
                val bloqueFin = formato.format(calendar.time)
                bloques.add(
                    BloqueHorario(
                        id = contador++,
                        horaInicio = bloqueInicio,
                        horaFin = bloqueFin,
                        descripcion = "$bloqueInicio - $bloqueFin"
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return bloques
    }
}
