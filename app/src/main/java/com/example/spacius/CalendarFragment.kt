package com.example.spacius

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.spacius.data.AppDatabase
import com.example.spacius.data.Reserva
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch

class CalendarFragment : Fragment() {

    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var monthYearText: TextView
    private lateinit var calendarGridView: GridView
    private lateinit var eventsContainer: LinearLayout
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
        cargarEventosDelMes()
    }

    private fun initViews(view: View) {
        monthYearText = view.findViewById(R.id.monthYearText)
        calendarGridView = view.findViewById(R.id.calendarGridView)
        eventsContainer = view.findViewById(R.id.eventsContainer)

        view.findViewById<TextView>(R.id.btnPrevMonth).setOnClickListener {
            currentDate.add(Calendar.MONTH, -1)
            updateCalendar()
            cargarEventosDelMes()
        }

        view.findViewById<TextView>(R.id.btnNextMonth).setOnClickListener {
            currentDate.add(Calendar.MONTH, 1)
            updateCalendar()
            cargarEventosDelMes()
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
            
            // 🔹 Limpiar fechas reservadas anteriores antes de recargar
            calendarAdapter.limpiarFechasReservadas()
            
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
            
            // 🔹 Actualizar eventos después de agregar reserva
            cargarEventosDelMes()
        }
    }

    // 🔹 Nueva función para manejar reservas completas
    fun marcarReservaCompleta(
        idLugar: Int,
        nombreLugar: String,
        fecha: String,
        horaInicio: String,
        horaFin: String
    ) {
        val parts = fecha.split("/")
        if (parts.size != 3) return

        val dia = parts[0].toInt()
        val mes = parts[1].toInt() - 1
        val anio = parts[2].toInt()
        val fechaCal = Calendar.getInstance()
        fechaCal.set(anio, mes, dia)

        // Marcar visualmente en el calendario
        calendarAdapter.marcarFechaReservada(fechaCal)
        calendarAdapter.notifyDataSetChanged()

        // Guardar en base de datos con información completa
        lifecycleScope.launch {
            val reserva = Reserva(
                idLugar = idLugar,
                fecha = fecha,
                horaInicio = horaInicio,
                horaFin = horaFin,
                nombreUsuario = "Usuario" // En el futuro, obtener nombre real del usuario logueado
            )
            db.reservaDao().insertReserva(reserva)
            
            // 🔹 Actualizar eventos después de agregar reserva
            cargarEventosDelMes()
        }
    }

    // 🔹 Nueva función para cargar eventos del mes actual
    private fun cargarEventosDelMes() {
        lifecycleScope.launch {
            val reservas = db.reservaDao().getAllReservas()
            val eventosDelMes = filtrarEventosDelMes(reservas)
            mostrarEventosEnUI(eventosDelMes)
        }
    }

    // 🔹 Filtrar reservas que pertenecen al mes actual mostrado
    private fun filtrarEventosDelMes(reservas: List<Reserva>): List<Reserva> {
        val mesActual = currentDate.get(Calendar.MONTH) + 1
        val anioActual = currentDate.get(Calendar.YEAR)
        
        return reservas.filter { reserva ->
            val parts = reserva.fecha.split("/")
            if (parts.size == 3) {
                val mesReserva = parts[1].toInt()
                val anioReserva = parts[2].toInt()
                mesReserva == mesActual && anioReserva == anioActual
            } else {
                false
            }
        }
    }

    // 🔹 Mostrar eventos en la sección de eventos
    private fun mostrarEventosEnUI(eventos: List<Reserva>) {
        eventsContainer.removeAllViews()
        
        if (eventos.isEmpty()) {
            val sinEventos = TextView(requireContext())
            sinEventos.text = "📅 No hay eventos programados para este mes"
            sinEventos.textSize = 14f
            sinEventos.setTextColor(resources.getColor(R.color.text_secondary, null))
            sinEventos.setPadding(16, 16, 16, 16)
            sinEventos.gravity = android.view.Gravity.CENTER
            eventsContainer.addView(sinEventos)
            return
        }

        for (evento in eventos) {
            agregarEventoALista(evento)
        }
    }

    // 🔹 Crear y agregar una vista de evento individual
    private fun agregarEventoALista(reserva: Reserva) {
        val eventoView = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_evento, eventsContainer, false)

        val txtNombreLugar = eventoView.findViewById<TextView>(R.id.txtNombreLugarEvento)
        val txtFechaHora = eventoView.findViewById<TextView>(R.id.txtFechaHoraEvento)

        // Obtener nombre del lugar desde la base de datos
        lifecycleScope.launch {
            val lugar = db.lugarDao().getLugarById(reserva.idLugar)
            val nombreLugar = lugar?.nombre ?: "Lugar reservado"
            
            txtNombreLugar.text = nombreLugar
            
            // Formatear fecha y hora
            val fechaHora = if (reserva.horaInicio.isNotEmpty() && reserva.horaFin.isNotEmpty()) {
                "${reserva.fecha} • ${reserva.horaInicio} - ${reserva.horaFin}"
            } else {
                reserva.fecha
            }
            txtFechaHora.text = fechaHora

            // 🔹 Configurar click para abrir detalles
            eventoView.setOnClickListener {
                abrirDetalleReserva(reserva, lugar)
            }
        }

        eventsContainer.addView(eventoView)
    }

    // 🔹 Nueva función para abrir el detalle de una reserva
    private fun abrirDetalleReserva(reserva: Reserva, lugar: com.example.spacius.data.Lugar?) {
        val fragment = DetalleReservaFragment()
        fragment.arguments = Bundle().apply {
            putInt("reservaId", reserva.id)
            putString("nombreLugar", lugar?.nombre ?: "Lugar desconocido")
            putString("descripcionLugar", lugar?.descripcion ?: "")
            putString("fecha", reserva.fecha)
            putString("horaInicio", reserva.horaInicio)
            putString("horaFin", reserva.horaFin)
            putString("usuario", reserva.nombreUsuario)
            putString("imagenUrl", lugar?.imagenUrl ?: "")
            putDouble("latitud", lugar?.latitud ?: -2.170998)
            putDouble("longitud", lugar?.longitud ?: -79.922359)
        }

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    // 🔹 Nueva función para actualizar después de cancelar una reserva
    fun actualizarDespuesDeCancelacion() {
        lifecycleScope.launch {
            // Recargar reservas y actualizar calendario
            cargarReservasPersistentes()
            cargarEventosDelMes()
        }
    }
}
