package com.example.spacius

import android.os.Bundle
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.enableEdgeToEdge
import java.util.*

class CalendarActivity : AppCompatActivity() {
    
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var monthYearText: TextView
    private lateinit var calendarGridView: GridView
    private var currentDate = Calendar.getInstance()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calendar)
        
        // Manejo de EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.calendarMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        initViews()
        setupCalendar()
    }
    
    private fun initViews() {
        monthYearText = findViewById(R.id.monthYearText)
        calendarGridView = findViewById(R.id.calendarGridView)
        
        // Botones de navegación
        findViewById<TextView>(R.id.btnPrevMonth).setOnClickListener {
            currentDate.add(Calendar.MONTH, -1)
            updateCalendar()
        }
        
        findViewById<TextView>(R.id.btnNextMonth).setOnClickListener {
            currentDate.add(Calendar.MONTH, 1)
            updateCalendar()
        }
    }
    
    private fun setupCalendar() {
        calendarAdapter = CalendarAdapter(this, currentDate)
        calendarGridView.adapter = calendarAdapter
        
        // Listener para selección de fecha
        calendarGridView.setOnItemClickListener { _, _, position, _ ->
            val selectedDate = calendarAdapter.getItem(position)
            if (selectedDate != null) {
                val dateString = "${selectedDate.get(Calendar.DAY_OF_MONTH)}/${selectedDate.get(Calendar.MONTH) + 1}/${selectedDate.get(Calendar.YEAR)}"
                Toast.makeText(this, "Fecha seleccionada: $dateString", Toast.LENGTH_SHORT).show()
                
                // Aquí puedes agregar más funcionalidad como:
                // - Abrir un diálogo para agregar eventos
                // - Mostrar eventos del día
                // - Navegar a una vista de detalles del día
            }
        }
        
        updateCalendar()
    }
    
    private fun updateCalendar() {
        calendarAdapter.updateCalendar(currentDate)
        updateMonthYearText()
    }
    
    private fun updateMonthYearText() {
        val monthNames = arrayOf(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )
        val month = monthNames[currentDate.get(Calendar.MONTH)]
        val year = currentDate.get(Calendar.YEAR)
        monthYearText.text = "$month $year"
    }
}
