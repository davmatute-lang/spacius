package com.example.spacius

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.spacius.data.AppDatabase
import com.example.spacius.data.Reserva
import java.util.*
import kotlinx.coroutines.launch

class CalendarFragment : Fragment() {

    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var monthYearText: TextView
    private lateinit var calendarGridView: GridView
    private var currentDate = Calendar.getInstance()

    private lateinit var db: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = AppDatabase.getDatabase(requireContext())

        initViews(view)
        setupCalendar()
        cargarReservasPersistentes()
    }

    private fun initViews(view: View) {
        monthYearText = view.findViewById(R.id.monthYearText)
        calendarGridView = view.findViewById(R.id.calendarGridView)

        view.findViewById<TextView>(R.id.btnPrevMonth).setOnClickListener {
            currentDate.add(Calendar.MONTH, -1)
            updateCalendar()
        }

        view.findViewById<TextView>(R.id.btnNextMonth).setOnClickListener {
            currentDate.add(Calendar.MONTH, 1)
            updateCalendar()
        }
    }

    private fun setupCalendar() {
        calendarAdapter = CalendarAdapter(requireContext(), currentDate)
        calendarGridView.adapter = calendarAdapter

        calendarGridView.setOnItemClickListener { _, _, position, _ ->
            val selectedDate = calendarAdapter.getItem(position)
            selectedDate?.let {
                val dateString = "${it.get(Calendar.DAY_OF_MONTH)}/${it.get(Calendar.MONTH) + 1}/${it.get(Calendar.YEAR)}"
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

    // 🔹 Cargar reservas de la base de datos y marcarlas
    private fun cargarReservasPersistentes() {
        lifecycleScope.launch {
            val reservas: List<Reserva> = db.reservaDao().getAllReservas()
            reservas.forEach { reserva ->
                val parts = reserva.fecha.split("/")
                if (parts.size == 3) {
                    val cal = Calendar.getInstance()
                    cal.set(parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())
                    calendarAdapter.marcarFechaReservada(cal)
                }
            }
            calendarAdapter.notifyDataSetChanged()
        }
    }

    // ✅ Función pública para marcar una fecha desde ReservaFragment
    fun marcarFechaDesdeReserva(fecha: String) {
        val parts = fecha.split("/")
        if (parts.size != 3) return

        val dia = parts[0].toInt()
        val mes = parts[1].toInt() - 1
        val anio = parts[2].toInt()
        val fechaCal = Calendar.getInstance()
        fechaCal.set(anio, mes, dia)

        // Marcar visualmente
        calendarAdapter.marcarFechaReservada(fechaCal)
        calendarAdapter.notifyDataSetChanged()

        // Guardar en base de datos
        lifecycleScope.launch {
            val reserva = Reserva(
                idLugar = 0, // puedes pasar idLugar real si quieres
                fecha = fecha,
                horaInicio = "",
                horaFin = "",
                nombreUsuario = "Usuario" // reemplazar con nombre real si lo tienes
            )
            db.reservaDao().insertReserva(reserva)
        }
    }
}
