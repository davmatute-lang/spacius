package com.example.spacius

import android.app.DatePickerDialog
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.spacius.data.FirestoreRepository
import com.example.spacius.data.BloqueHorario
import com.example.spacius.data.ReservaFirestore
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ReservaFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var latLugar: Double = -2.170998
    private var lngLugar: Double = -79.922359
    private lateinit var firestoreRepository: FirestoreRepository

    // Variables para almacenar datos del lugar
    private var lugarId: String = ""
    private var nombreLugar: String = ""
    
    // Variables para almacenar selecciones del usuario
    private var fechaSeleccionada = ""
    private var horaInicioSeleccionada = ""
    private var horaFinSeleccionada = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reserva, container, false)

        // Inicializar Firestore
        firestoreRepository = FirestoreRepository()

        val txtNombre: TextView = view.findViewById(R.id.txtNombreLugarDetalle)
        val txtDescripcion: TextView = view.findViewById(R.id.txtDescripcionLugarDetalle)
        val txtCapacidad: TextView = view.findViewById(R.id.txtCapacidad)
        val imgLugar: ImageView = view.findViewById(R.id.imgLugarDetalle)

        val btnSeleccionarFecha: Button = view.findViewById(R.id.btnFecha)
        val btnHoraInicio: Button = view.findViewById(R.id.btnHoraInicio)
        val btnHoraFin: Button = view.findViewById(R.id.btnHoraFin)

        val btnReservar: Button = view.findViewById(R.id.btnReservar)
        val btnCancelar: Button = view.findViewById(R.id.btnCancelarReserva)

        // Cargar datos del lugar seleccionado desde arguments
        arguments?.let { bundle ->
            lugarId = bundle.getString("lugarId") ?: ""
            nombreLugar = bundle.getString("nombreLugar") ?: "Lugar desconocido"
            
            txtNombre.text = nombreLugar
            txtDescripcion.text = bundle.getString("descripcion")
            
            val disponibilidad = bundle.getString("fecha") ?: "Disponible"
            val capacidad = bundle.getInt("capacidad", 0)
            
            txtCapacidad.text = if (capacidad > 0) {
                "👥 Capacidad: $capacidad personas • 📅 $disponibilidad"
            } else {
                "📅 $disponibilidad"
            }

            Glide.with(this).load(bundle.getString("imagenUrl"))
                .placeholder(R.drawable.ic_launcher_background)
                .into(imgLugar)

            latLugar = bundle.getDouble("latitud", -2.170998)
            lngLugar = bundle.getDouble("longitud", -79.922359)
        }

        // Selección de fecha (solo fechas presentes y futuras)
        btnSeleccionarFecha.setOnClickListener {
            val calendario = Calendar.getInstance()
            val año = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, añoSeleccionado, mesSeleccionado, diaSeleccionado ->
                    fechaSeleccionada = String.format("%04d-%02d-%02d", añoSeleccionado, mesSeleccionado + 1, diaSeleccionado)
                    btnSeleccionarFecha.text = "📅 $fechaSeleccionada"
                },
                año, mes, dia
            )
            
            // Establecer fecha mínima como hoy
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        }

        // Selección de hora de inicio (bloques de tiempo)
        btnHoraInicio.setOnClickListener {
            mostrarSelectorBloqueHorario { bloque ->
                horaInicioSeleccionada = bloque.horaInicio
                horaFinSeleccionada = bloque.horaFin
                btnHoraInicio.text = "⏰ ${bloque.horaInicio}"
                btnHoraFin.text = "⏰ ${bloque.horaFin}"
            }
        }

        // Botón reservar
        btnReservar.setOnClickListener {
            if (fechaSeleccionada.isEmpty() || horaInicioSeleccionada.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor selecciona fecha y hora", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar disponibilidad antes de crear la reserva
            lifecycleScope.launch {
                try {
                    val disponible = firestoreRepository.verificarDisponibilidad(
                        lugarId, fechaSeleccionada, horaInicioSeleccionada, horaFinSeleccionada
                    )
                    
                    if (!disponible) {
                        Toast.makeText(requireContext(), "❌ Horario no disponible. Selecciona otro.", Toast.LENGTH_LONG).show()
                        return@launch
                    }

                    // Crear la reserva
                    val reserva = ReservaFirestore(
                        lugarId = lugarId,
                        lugarNombre = nombreLugar,
                        fecha = fechaSeleccionada,
                        horaInicio = horaInicioSeleccionada,
                        horaFin = horaFinSeleccionada,
                        notas = "Reserva creada desde la app"
                    )

                    firestoreRepository.crearReserva(reserva)
                    Toast.makeText(requireContext(), "✅ Reserva creada exitosamente", Toast.LENGTH_SHORT).show()
                    
                    // Navegar al calendario
                    navegarAlCalendario()
                    
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error al crear reserva: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Botón cancelar
        btnCancelar.setOnClickListener {
            navegarAlCalendario()
        }

        // Configurar el mapa
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragmentContainer) as? SupportMapFragment
            ?: SupportMapFragment.newInstance().also {
                childFragmentManager.beginTransaction()
                    .replace(R.id.mapFragmentContainer, it)
                    .commit()
            }
        mapFragment.getMapAsync(this)

        return view
    }

    private fun mostrarSelectorBloqueHorario(onBloqueSeleccionado: (BloqueHorario) -> Unit) {
        lifecycleScope.launch {
            try {
                // Obtener bloques disponibles usando el sistema de disponibilidad
                val bloquesDisponibles = if (fechaSeleccionada.isNotEmpty()) {
                    firestoreRepository.obtenerBloquesDisponibles(lugarId, fechaSeleccionada)
                } else {
                    obtenerBloquesHorarios() // Mostrar todos si no hay fecha seleccionada
                }

                if (bloquesDisponibles.isEmpty()) {
                    Toast.makeText(requireContext(), "❌ No hay horarios disponibles para esta fecha", Toast.LENGTH_LONG).show()
                    return@launch
                }

                val opciones = bloquesDisponibles.map { "${it.horaInicio} - ${it.horaFin}" }.toTypedArray()
                
                AlertDialog.Builder(requireContext())
                    .setTitle("⏰ Seleccionar Horario Disponible")
                    .setItems(opciones) { _, posicion ->
                        onBloqueSeleccionado(bloquesDisponibles[posicion])
                    }
                    .show()
                    
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al cargar horarios: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun obtenerBloquesHorarios(): List<BloqueHorario> {
        val bloques = mutableListOf<BloqueHorario>()
        var id = 1
        
        // Generar bloques de 1h45min desde 8:00 AM hasta 9:45 PM
        val horaInicio = 8 * 60 // 8:00 AM en minutos
        val horaFin = 21 * 60 + 45 // 9:45 PM en minutos
        val duracionBloque = 105 // 1h45min en minutos
        
        var horaActual = horaInicio
        while (horaActual + duracionBloque <= horaFin + 60) {
            val horas1 = horaActual / 60
            val minutos1 = horaActual % 60
            val horas2 = (horaActual + duracionBloque) / 60
            val minutos2 = (horaActual + duracionBloque) % 60
            
            val inicio = String.format("%02d:%02d", horas1, minutos1)
            val fin = String.format("%02d:%02d", horas2, minutos2)
            
            bloques.add(BloqueHorario(id++, inicio, fin, "Bloque $id"))
            horaActual += duracionBloque + 15 // +15 min de descanso
        }
        
        return bloques
    }

    private fun navegarAlCalendario() {
        try {
            val calendarFragment = CalendarFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, calendarFragment)
                .addToBackStack(null)
                .commit()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error al navegar: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val lugar = LatLng(latLugar, lngLugar)
        map.clear()
        map.addMarker(MarkerOptions().position(lugar).title("📍 $nombreLugar"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(lugar, 15f))
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isMapToolbarEnabled = true
    }
}