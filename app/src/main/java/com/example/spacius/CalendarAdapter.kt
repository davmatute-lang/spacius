package com.example.spacius

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.util.*

class CalendarAdapter(
    private val context: Context,
    private var currentDate: Calendar
) : BaseAdapter() {

    private val days = mutableListOf<Calendar>()
    private val reservedDates = mutableListOf<Calendar>()

    init {
        generateDays()
    }

    override fun getCount(): Int = days.size

    override fun getItem(position: Int): Calendar? = days.getOrNull(position)

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.calendar_day_item, parent, false) // ✅ corregido nombre del layout

        val dayText = view.findViewById<TextView>(R.id.dayText)
        val day = days[position]

        dayText.text = day.get(Calendar.DAY_OF_MONTH).toString()

        // Color del texto según si pertenece al mes actual o no
        if (isSameMonth(day, currentDate)) {
            dayText.setTextColor(Color.BLACK)
        } else {
            dayText.setTextColor(Color.GRAY)
        }

        // Si el día está reservado, se marca en morado
        if (reservedDates.any { isSameDay(it, day) }) {
            dayText.setBackgroundColor(Color.parseColor("#FFBB86FC")) // Morado
            dayText.setTextColor(Color.WHITE)
        } else {
            dayText.setBackgroundColor(Color.TRANSPARENT)
        }

        return view
    }

    // Actualiza el calendario cuando cambia el mes
    fun updateCalendar(newDate: Calendar) {
        currentDate = newDate
        generateDays()
        notifyDataSetChanged()
    }

    // Marca una fecha reservada (desde ReservaFragment o CalendarActivity)
    fun marcarFechaReservada(fecha: Calendar) {
        reservedDates.add(fecha)
    }

    // Genera los días a mostrar (6 filas de 7 días)
    private fun generateDays() {
        days.clear()

        val calendar = currentDate.clone() as Calendar
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val monthStart = calendar.get(Calendar.DAY_OF_WEEK) - 1
        calendar.add(Calendar.DAY_OF_MONTH, -monthStart)

        val totalCells = 42 // 6 semanas
        for (i in 0 until totalCells) {
            days.add(calendar.clone() as Calendar)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }

    private fun isSameMonth(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
    }
}
