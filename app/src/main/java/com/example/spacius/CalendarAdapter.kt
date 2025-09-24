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
    
    private val calendar = Calendar.getInstance()
    private val today = Calendar.getInstance()
    private val daysInMonth = mutableListOf<Calendar?>()
    
    init {
        updateCalendar(currentDate)
    }
    
    fun updateCalendar(newDate: Calendar) {
        currentDate = newDate
        calendar.time = newDate.time
        generateDaysInMonth()
        notifyDataSetChanged()
    }
    
    private fun generateDaysInMonth() {
        daysInMonth.clear()
        
        // Obtener el primer día del mes
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        
        // Obtener el número de días en el mes
        val daysInCurrentMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        
        // Agregar días vacíos para alinear el primer día
        for (i in 1 until firstDayOfWeek) {
            daysInMonth.add(null)
        }
        
        // Agregar todos los días del mes
        for (day in 1..daysInCurrentMonth) {
            val dayCalendar = Calendar.getInstance()
            dayCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
            dayCalendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
            dayCalendar.set(Calendar.DAY_OF_MONTH, day)
            daysInMonth.add(dayCalendar)
        }
    }
    
    override fun getCount(): Int = 42 // 6 semanas * 7 días
    
    override fun getItem(position: Int): Calendar? = daysInMonth.getOrNull(position)
    
    override fun getItemId(position: Int): Long = position.toLong()
    
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.calendar_day_item, parent, false)
        
        val dayText = view.findViewById<TextView>(R.id.dayText)
        val dayCalendar = getItem(position)
        
        if (dayCalendar != null) {
            val day = dayCalendar.get(Calendar.DAY_OF_MONTH)
            dayText.text = day.toString()
            dayText.visibility = View.VISIBLE
            
            // Estilo para el día actual
            if (isToday(dayCalendar)) {
                dayText.setBackgroundColor(Color.parseColor("#1976D2"))
                dayText.setTextColor(Color.WHITE)
            } else {
                dayText.setBackgroundColor(Color.TRANSPARENT)
                dayText.setTextColor(Color.BLACK)
            }
            
            // Estilo para días del mes actual vs otros meses
            if (dayCalendar.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH)) {
                dayText.alpha = 1.0f
            } else {
                dayText.alpha = 0.3f
            }
        } else {
            dayText.text = ""
            dayText.visibility = View.INVISIBLE
        }
        
        return view
    }
    
    private fun isToday(calendar: Calendar): Boolean {
        return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
    }
}
