package com.example.spacius.utils

import android.util.Log
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object DateTimeUtils {

    /**
     * Valida si la combinación de fecha (YYYY-MM-DD) y hora (HH:mm) es posterior al momento actual.
     *
     * @param fechaStr Fecha en formato "yyyy-MM-dd".
     * @param horaStr Hora en formato "HH:mm".
     * @return `true` si la fecha y hora son en el futuro, `false` si ya pasaron o son en el presente.
     */
    fun esFechaHoraFutura(fechaStr: String, horaStr: String): Boolean {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val fechaHora = LocalDateTime.parse("$fechaStr $horaStr", formatter)
            fechaHora.isAfter(LocalDateTime.now())
        } catch (e: DateTimeParseException) {
            Log.e("DateTimeUtils", "Error al parsear fecha/hora: '$fechaStr $horaStr'", e)
            false // Si el formato es incorrecto, no se puede validar, se asume que no es futura.
        }
    }
}
