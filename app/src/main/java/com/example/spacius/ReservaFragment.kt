package com.example.spacius.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
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
import com.example.spacius.CalendarFragment
import com.example.spacius.MainActivity
import com.example.spacius.R
import com.example.spacius.data.FirestoreRepository
import com.example.spacius.data.ReservaFirestore
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import java.util.Calendar

class ReservaFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var latLugar: Double = -2.170998
    private var lngLugar: Double = -79.922359
    private lateinit var firestoreRepository: FirestoreRepository

    // Variables para almacenar datos del lugar
    private var lugarId: String = ""
    private var nombreLugar: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reserva_exitosa, container, false)

        // Inicializar Firestore
        firestoreRepository = FirestoreRepository()

        val txtNombre: TextView = view.findViewById(R.id.btnNombreLugar)
        val txtDescripcion: TextView = view.findViewById(R.id.btnDescripcionLugar)
        val txtCapacidad: TextView = view.findViewById(R.id.btnCapacidad)
        val imgLugar: ImageView = view.findViewById(R.id.imgLugar)

        val btnSeleccionarFecha: Button = view.findViewById(R.id.btnFecha)
        val btnHoraInicio: Button = view.findViewById(R.id.btnHoraInicio)
        val btnHoraFin: Button = view.findViewById(R.id.btnHoraFin)

        val btnReservar: Button = view.findViewById(R.id.btnReservar)
        val btnCancelar: Button = view.findViewById(R.id.btnCancelar)

        // Cargar datos del lugar seleccionado desde Firestore
        arguments?.let { bundle ->
            lugarId = bundle.getString("lugarId") ?: ""
            nombreLugar = bundle.getString("nombreLugar") ?: "Lugar desconocido"
            
            txtNombre.text = nombreLugar
            txtDescripcion.text = bundle.getString("descripcion")
            txtCapacidad.text = "📅 ${bundle.getString("fecha")} | 🕐 ${bundle.getString("hora")}"

            Glide.with(this).load(bundle.getString("imagenUrl"))
                .placeholder(R.drawable.ic_launcher_background)
                .into(imgLugar)

            latLugar = bundle.getDouble("latitud", -2.170998)
            lngLugar = bundle.getDouble("longitud", -79.922359)
        }

        // Variables para almacenar selecciones del usuario
        var fechaSeleccionada = ""
        var horaInicioSeleccionada = ""
        var horaFinSeleccionada = ""

        // Selección de fecha
        btnSeleccionarFecha.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d ->
                fechaSeleccionada = String.format("%04d-%02d-%02d", y, m + 1, d)
                btnSeleccionarFecha.text = "$d/${m + 1}/$y"
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Selección de hora inicio
        btnHoraInicio.setOnClickListener {
            val c = Calendar.getInstance()
            TimePickerDialog(requireContext(), { _, h, m ->
                horaInicioSeleccionada = String.format("%02d:%02d", h, m)
                btnHoraInicio.text = horaInicioSeleccionada
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
        }

        // Selección de hora fin
        btnHoraFin.setOnClickListener {
            val c = Calendar.getInstance()
            TimePickerDialog(requireContext(), { _, h, m ->
                horaFinSeleccionada = String.format("%02d:%02d", h, m)
                btnHoraFin.text = horaFinSeleccionada
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
        }

        // Botón Reservar con Firestore
        btnReservar.setOnClickListener {
            // Validaciones
            if (fechaSeleccionada.isBlank()) {
                Toast.makeText(requireContext(), "Por favor, selecciona una fecha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (horaInicioSeleccionada.isBlank()) {
                Toast.makeText(requireContext(), "Por favor, selecciona hora de inicio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (horaFinSeleccionada.isBlank()) {
                Toast.makeText(requireContext(), "Por favor, selecciona hora de fin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar que la hora de fin sea posterior a la de inicio
            if (horaInicioSeleccionada >= horaFinSeleccionada) {
                Toast.makeText(requireContext(), "La hora de fin debe ser posterior a la de inicio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear reserva con Firestore
            val reserva = ReservaFirestore(
                lugarId = lugarId,
                lugarNombre = nombreLugar,
                fecha = fechaSeleccionada,
                horaInicio = horaInicioSeleccionada,
                horaFin = horaFinSeleccionada,
                estado = "activa"
            )

            // Guardar en Firestore
            lifecycleScope.launch {
                try {
                    btnReservar.isEnabled = false
                    btnReservar.text = "Reservando..."

                    val exito = firestoreRepository.crearReserva(reserva)
                    
                    if (exito) {
                        Toast.makeText(requireContext(), "¡Reserva realizada exitosamente! 🎉", Toast.LENGTH_LONG).show()
                        
                        // Volver al calendario para ver la nueva reserva
                        navegarAlCalendario()
                    } else {
                        Toast.makeText(requireContext(), "Error al crear la reserva. Inténtalo nuevamente.", Toast.LENGTH_LONG).show()
                        btnReservar.isEnabled = true
                        btnReservar.text = "Reservar"
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    android.util.Log.e("ReservaFragment", "Error al crear reserva", e)
                } finally {
                    // Asegurar que el botón vuelva a su estado normal
                    btnReservar.isEnabled = true
                    btnReservar.text = "Reservar"
                }
            }
        }

        // Botón Cancelar
        btnCancelar.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // Cargar mapa
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragmentContainer) as? SupportMapFragment
            ?: SupportMapFragment.newInstance().also {
                childFragmentManager.beginTransaction()
                    .replace(R.id.mapFragmentContainer, it)
                    .commit()
            }
        mapFragment.getMapAsync(this)

        return view
    }

    /**
     * Navegar directamente al calendario después de una reserva exitosa
     */
    private fun navegarAlCalendario() {
        // Usar el método específico de MainActivity para navegar al calendario
        (requireActivity() as? MainActivity)?.navegarACalendario()
            ?: navegarManualmenteAlCalendario() // Fallback si no es MainActivity
    }
    
    /**
     * Navegación manual al calendario como fallback
     */
    private fun navegarManualmenteAlCalendario() {
        val calendarFragment = CalendarFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, calendarFragment)
            .commit()
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
