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
import com.example.spacius.data.FirestoreRepository
import com.example.spacius.data.ReservaFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch

class CalendarFragment : Fragment() {

    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var monthYearText: TextView
    private lateinit var calendarGridView: GridView
    private lateinit var eventsContainer: LinearLayout
    private var currentDate = Calendar.getInstance()

    private lateinit var firestoreRepository: FirestoreRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestoreRepository = FirestoreRepository()

        initViews(view)
        setupCalendar()
        cargarReservasPersistentes()
        cargarEventosDelMes()
    }

    override fun onResume() {
        super.onResume()
        // Recargar reservas cada vez que el fragment se vuelve visible
        android.util.Log.d("CalendarFragment", "Fragment visible - recargando reservas...")
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

    // 🔹 Cargar reservas de Firestore y marcarlas
    private fun cargarReservasPersistentes() {
        lifecycleScope.launch {
            android.util.Log.d("CalendarFragment", "Cargando reservas desde Firestore...")
            
            val reservas: List<ReservaFirestore> = firestoreRepository.obtenerReservasUsuario()
            android.util.Log.d("CalendarFragment", "Reservas encontradas: ${reservas.size}")
            
            // 🔹 Limpiar fechas reservadas anteriores antes de recargar
            calendarAdapter.limpiarFechasReservadas()
            
            reservas.forEach { reserva ->
                android.util.Log.d("CalendarFragment", "Procesando reserva: ${reserva.fecha}")
                // Las fechas en Firestore están en formato YYYY-MM-DD
                val parts = reserva.fecha.split("-")
                if (parts.size == 3) {
                    val cal = Calendar.getInstance()
                    cal.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
                    calendarAdapter.marcarFechaReservada(cal)
                }
            }
            calendarAdapter.notifyDataSetChanged()
            android.util.Log.d("CalendarFragment", "Calendario actualizado con reservas")
        }
    }

    // ✅ Función pública para recargar reservas (llamada después de crear una nueva reserva)
    fun recargarReservas() {
        android.util.Log.d("CalendarFragment", "Recargando reservas...")
        cargarReservasPersistentes()
        cargarEventosDelMes()
    }

    // 🔹 Nueva función para cargar eventos del mes actual desde Firestore
    private fun cargarEventosDelMes() {
        lifecycleScope.launch {
            android.util.Log.d("CalendarFragment", "Cargando eventos del mes desde Firestore...")
            val reservas = firestoreRepository.obtenerReservasUsuario()
            val eventosDelMes = filtrarEventosDelMes(reservas)
            mostrarEventosEnUI(eventosDelMes)
        }
    }

    // 🔹 Filtrar reservas que pertenecen al mes actual mostrado
    private fun filtrarEventosDelMes(reservas: List<ReservaFirestore>): List<ReservaFirestore> {
        val mesActual = currentDate.get(Calendar.MONTH) + 1
        val anioActual = currentDate.get(Calendar.YEAR)
        
        return reservas.filter { reserva ->
            // Las fechas en Firestore están en formato YYYY-MM-DD
            val parts = reserva.fecha.split("-")
            if (parts.size == 3) {
                val anioReserva = parts[0].toInt()
                val mesReserva = parts[1].toInt()
                mesReserva == mesActual && anioReserva == anioActual
            } else {
                false
            }
        }
    }

    // 🔹 Mostrar eventos en la sección de eventos
    private fun mostrarEventosEnUI(eventos: List<ReservaFirestore>) {
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
    private fun agregarEventoALista(reserva: ReservaFirestore) {
        val eventoView = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_evento, eventsContainer, false)

        val txtNombreLugar = eventoView.findViewById<TextView>(R.id.txtNombreLugarEvento)
        val txtFechaHora = eventoView.findViewById<TextView>(R.id.txtFechaHoraEvento)

        // Usar el nombre del lugar directamente desde la reserva de Firestore
        txtNombreLugar.text = reserva.lugarNombre
        
        // Formatear fecha y hora para mostrar en formato legible
        val fechaLegible = formatearFechaParaMostrar(reserva.fecha)
        val fechaHora = if (reserva.horaInicio.isNotEmpty() && reserva.horaFin.isNotEmpty()) {
            "$fechaLegible • ${reserva.horaInicio} - ${reserva.horaFin}"
        } else {
            fechaLegible
        }
        txtFechaHora.text = fechaHora

        // 🔹 Configurar click para abrir detalles
        eventoView.setOnClickListener {
            abrirDetalleReserva(reserva)
        }

        eventsContainer.addView(eventoView)
    }

    // 🔹 Formatear fecha de YYYY-MM-DD a DD/MM/YYYY
    private fun formatearFechaParaMostrar(fecha: String): String {
        val parts = fecha.split("-")
        return if (parts.size == 3) {
            "${parts[2]}/${parts[1]}/${parts[0]}"
        } else {
            fecha
        }
    }

    // 🔹 Nueva función para abrir el detalle de una reserva de Firestore
    private fun abrirDetalleReserva(reserva: ReservaFirestore) {
        val fragment = DetalleReservaFragment()
        fragment.arguments = Bundle().apply {
            putString("reservaId", reserva.id)
            putString("nombreLugar", reserva.lugarNombre)
            putString("fecha", formatearFechaParaMostrar(reserva.fecha))
            putString("horaInicio", reserva.horaInicio)
            putString("horaFin", reserva.horaFin)
            putString("usuario", reserva.usuarioNombre)
            putString("estado", reserva.estado)
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
