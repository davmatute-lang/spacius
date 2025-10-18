package com.example.spacius.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.example.spacius.CalendarFragment
import com.example.spacius.MainActivity
import com.example.spacius.R
import com.example.spacius.data.FirestoreRepository
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.spacius.data.ReservaFirestore
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Data class para representar un bloque horario de reserva
 */
data class BloqueHorario(
    val id: Int,
    val horaInicio: String,
    val horaFin: String,
    val descripcion: String
)

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
            
            // Mostrar información útil: disponibilidad y capacidad
            val disponibilidad = bundle.getString("fecha") ?: "Disponible"
            val capacidad = bundle.getInt("capacidad", 0)
            val categoria = bundle.getString("categoria") ?: "deportivo"
            
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

        // Variables para almacenar selecciones del usuario
        var fechaSeleccionada = ""
        var horaInicioSeleccionada = ""
        var horaFinSeleccionada = ""

        // Selección de fecha (solo fechas presentes y futuras)
        btnSeleccionarFecha.setOnClickListener {
            val c = Calendar.getInstance()
            val fechaPickerDialog = DatePickerDialog(
                requireContext(), 
                { _, y, m, d ->
                    fechaSeleccionada = String.format("%04d-%02d-%02d", y, m + 1, d)
                    btnSeleccionarFecha.text = "$d/${m + 1}/$y"
                }, 
                c.get(Calendar.YEAR), 
                c.get(Calendar.MONTH), 
                c.get(Calendar.DAY_OF_MONTH)
            )
            
            // Establecer fecha mínima (hoy) - no permitir fechas pasadas
            fechaPickerDialog.datePicker.minDate = System.currentTimeMillis()
            
            // Opcional: Establecer fecha máxima (por ejemplo, 3 meses en el futuro)
            val fechaMaxima = Calendar.getInstance()
            fechaMaxima.add(Calendar.MONTH, 3)
            fechaPickerDialog.datePicker.maxDate = fechaMaxima.timeInMillis
            
            fechaPickerDialog.show()
        }

        // Selección de bloque horario (sistema de bloques de 1h45min)
        btnHoraInicio.setOnClickListener {
            android.util.Log.d("ReservaFragment", "Botón seleccionar bloque horario presionado")
            mostrarSelectorBloqueHorario { bloqueSeleccionado ->
                horaInicioSeleccionada = bloqueSeleccionado.horaInicio
                horaFinSeleccionada = bloqueSeleccionado.horaFin
                btnHoraInicio.text = "${bloqueSeleccionado.horaInicio} - ${bloqueSeleccionado.horaFin}"
                android.util.Log.d("ReservaFragment", "Bloque seleccionado: ${bloqueSeleccionado.descripcion}")
            }
        }

        // Botón Reservar con Firestore
        btnReservar.setOnClickListener {
            android.util.Log.d("ReservaFragment", "Botón Reservar clickeado")
            
            // Validaciones
            if (fechaSeleccionada.isBlank()) {
                android.util.Log.d("ReservaFragment", "Error: Fecha no seleccionada")
                Toast.makeText(requireContext(), "Por favor, selecciona una fecha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (horaInicioSeleccionada.isBlank()) {
                android.util.Log.d("ReservaFragment", "Error: Hora no seleccionada")
                Toast.makeText(requireContext(), "Por favor, selecciona un bloque horario", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (horaFinSeleccionada.isBlank()) {
                android.util.Log.d("ReservaFragment", "Error: Hora fin no determinada")
                Toast.makeText(requireContext(), "Error: No se pudo determinar la hora de fin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            android.util.Log.d("ReservaFragment", "Validaciones básicas pasadas - Fecha: $fechaSeleccionada, Hora inicio: $horaInicioSeleccionada, Hora fin: $horaFinSeleccionada")

            // Validar que la fecha y hora no sean del pasado
            android.util.Log.d("ReservaFragment", "Iniciando validación de fecha y hora...")
            if (!validarFechaYHoraNoEsPasada(fechaSeleccionada, horaInicioSeleccionada)) {
                android.util.Log.d("ReservaFragment", "Validación de fecha y hora falló")
                return@setOnClickListener
            }
            
            android.util.Log.d("ReservaFragment", "Todas las validaciones pasaron, creando reserva...")

            // Los bloques ya están validados, no necesitamos validaciones adicionales de rango

            // Crear reserva con Firestore
            val reserva = ReservaFirestore(
                lugarId = lugarId,
                lugarNombre = nombreLugar,
                fecha = fechaSeleccionada,
                horaInicio = horaInicioSeleccionada,
                horaFin = horaFinSeleccionada,
                estado = "activa"
            )

            android.util.Log.d("ReservaFragment", "Reserva creada, iniciando guardado en Firestore...")

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
     * Mostrar selector de hora limitado de 8:00 AM a 10:00 PM
     */
    private fun mostrarSelectorHora(esHoraInicio: Boolean, onHoraSeleccionada: (String) -> Unit) {
        // Hora inicial por defecto (8:00 AM para inicio, 9:00 AM para fin)
        val horaInicial = if (esHoraInicio) 8 else 9
        
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, horaSeleccionada, minutoSeleccionado ->
                // Validar rango de horas (8:00 AM - 10:00 PM)
                when {
                    horaSeleccionada < 8 -> {
                        Toast.makeText(
                            requireContext(),
                            "⏰ La hora mínima es 8:00 AM",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@TimePickerDialog
                    }
                    horaSeleccionada > 22 -> {
                        Toast.makeText(
                            requireContext(),
                            "⏰ La hora máxima es 10:00 PM",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@TimePickerDialog
                    }
                    else -> {
                        val horaFormateada = String.format("%02d:%02d", horaSeleccionada, minutoSeleccionado)
                        onHoraSeleccionada(horaFormateada)
                    }
                }
            },
            horaInicial, // Hora inicial
            0, // Minuto inicial
            true // Formato 24 horas
        )
        
        timePickerDialog.show()
    }

    /**
     * Obtener bloques horarios disponibles (1h45min cada uno)
     */
    private fun obtenerBloquesHorarios(): List<BloqueHorario> {
        return listOf(
            BloqueHorario(1, "08:00", "09:45", "8:00 AM - 9:45 AM"),
            BloqueHorario(2, "10:00", "11:45", "10:00 AM - 11:45 AM"),
            BloqueHorario(3, "12:00", "13:45", "12:00 PM - 1:45 PM"),
            BloqueHorario(4, "14:00", "15:45", "2:00 PM - 3:45 PM"),
            BloqueHorario(5, "16:00", "17:45", "4:00 PM - 5:45 PM"),
            BloqueHorario(6, "18:00", "19:45", "6:00 PM - 7:45 PM"),
            BloqueHorario(7, "20:00", "21:45", "8:00 PM - 9:45 PM")
        )
    }

    /**
     * Mostrar selector de bloques horarios - versión simplificada
     */
    private fun mostrarSelectorBloqueHorario(onBloqueSeleccionado: (BloqueHorario) -> Unit) {
        android.util.Log.d("ReservaFragment", "Iniciando mostrarSelectorBloqueHorario")
        
        val bloques = obtenerBloquesHorarios()
        val opciones = bloques.map { "${it.descripcion}" }.toTypedArray()
        
        android.util.Log.d("ReservaFragment", "Opciones: ${opciones.joinToString()}")
        
        try {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("⏰ Selecciona tu bloque horario")
            builder.setItems(opciones) { dialog, which ->
                android.util.Log.d("ReservaFragment", "Seleccionado índice: $which")
                if (which >= 0 && which < bloques.size) {
                    val bloqueSeleccionado = bloques[which]
                    onBloqueSeleccionado(bloqueSeleccionado)
                    Toast.makeText(
                        requireContext(), 
                        "✅ ${bloqueSeleccionado.descripcion}", 
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dialog.dismiss()
            }
            builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            
            val dialog = builder.create()
            dialog.show()
            
            android.util.Log.d("ReservaFragment", "Dialog creado y mostrado")
        } catch (e: Exception) {
            android.util.Log.e("ReservaFragment", "Error: ${e.message}", e)
            // Fallback: mostrar Toast con las opciones
            mostrarFallbackSelector(bloques, onBloqueSeleccionado)
        }
    }
    
    /**
     * Selector de fallback usando Toast
     */
    private fun mostrarFallbackSelector(bloques: List<BloqueHorario>, onBloqueSeleccionado: (BloqueHorario) -> Unit) {
        // Por ahora, seleccionar automáticamente el primer bloque como prueba
        Toast.makeText(requireContext(), "Problema con el selector. Usando Bloque 1 por defecto.", Toast.LENGTH_LONG).show()
        onBloqueSeleccionado(bloques[0])
    }

    /**
     * Validar que las horas estén en el rango permitido (8:00 AM - 10:00 PM)
     */
    private fun validarRangoHorario(horaInicio: String, horaFin: String): Boolean {
        try {
            val horaInicioInt = horaInicio.split(":")[0].toInt()
            val horaFinInt = horaFin.split(":")[0].toInt()
            
            when {
                horaInicioInt < 8 -> {
                    Toast.makeText(requireContext(), "⏰ La hora de inicio debe ser desde las 8:00 AM", Toast.LENGTH_LONG).show()
                    return false
                }
                horaFinInt > 22 -> {
                    Toast.makeText(requireContext(), "⏰ La hora de fin debe ser hasta las 10:00 PM", Toast.LENGTH_LONG).show()
                    return false
                }
                horaInicioInt > 22 -> {
                    Toast.makeText(requireContext(), "⏰ La hora de inicio debe ser hasta las 10:00 PM", Toast.LENGTH_LONG).show()
                    return false
                }
                else -> return true
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "⏰ Error al validar horarios", Toast.LENGTH_SHORT).show()
            return false
        }
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

    /**
     * Validar que la fecha seleccionada no sea una fecha del pasado
     */
    private fun validarFechaNoEsPasada(fechaSeleccionada: String): Boolean {
        try {
            val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaElegida = formatoFecha.parse(fechaSeleccionada)
            val fechaActual = Calendar.getInstance()
            
            // Normalizar fechas para comparar solo día, mes y año (ignorar horas)
            val fechaElegidaCalendar = Calendar.getInstance()
            fechaElegidaCalendar.time = fechaElegida
            fechaElegidaCalendar.set(Calendar.HOUR_OF_DAY, 0)
            fechaElegidaCalendar.set(Calendar.MINUTE, 0)
            fechaElegidaCalendar.set(Calendar.SECOND, 0)
            fechaElegidaCalendar.set(Calendar.MILLISECOND, 0)
            
            fechaActual.set(Calendar.HOUR_OF_DAY, 0)
            fechaActual.set(Calendar.MINUTE, 0)
            fechaActual.set(Calendar.SECOND, 0)
            fechaActual.set(Calendar.MILLISECOND, 0)
            
            if (fechaElegidaCalendar.before(fechaActual)) {
                Toast.makeText(requireContext(), "📅 No puedes reservar en fechas pasadas. Por favor selecciona una fecha actual o futura.", Toast.LENGTH_LONG).show()
                return false
            }
            
            return true
            
        } catch (e: Exception) {
            Log.e("ReservaFragment", "Error al validar fecha: ${e.message}")
            Toast.makeText(requireContext(), "📅 Error al validar la fecha seleccionada", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    /**
     * Validar que la fecha y hora seleccionadas no sean del pasado
     */
    private fun validarFechaYHoraNoEsPasada(fechaSeleccionada: String, horaInicioSeleccionada: String): Boolean {
        android.util.Log.d("ReservaFragment", "=== INICIO VALIDACIÓN ===")
        android.util.Log.d("ReservaFragment", "Validando fecha: '$fechaSeleccionada', hora: '$horaInicioSeleccionada'")
        
        try {
            
            // Crear Calendar para fecha y hora seleccionadas
            val fechaHoraSeleccionada = Calendar.getInstance()
            
            // Parsear fecha - puede venir en formato DD/MM/YYYY o YYYY-MM-DD
            val dia: Int
            val mes: Int
            val año: Int
            
            if (fechaSeleccionada.contains("/")) {
                // Formato DD/MM/YYYY
                val partesFecha = fechaSeleccionada.split("/")
                if (partesFecha.size != 3) {
                    Log.e("ReservaFragment", "Formato de fecha inválido: $fechaSeleccionada")
                    return false
                }
                dia = partesFecha[0].toInt()
                mes = partesFecha[1].toInt() - 1 // Calendar usa meses 0-11
                año = partesFecha[2].toInt()
            } else if (fechaSeleccionada.contains("-")) {
                // Formato YYYY-MM-DD
                val partesFecha = fechaSeleccionada.split("-")
                if (partesFecha.size != 3) {
                    Log.e("ReservaFragment", "Formato de fecha inválido: $fechaSeleccionada")
                    return false
                }
                año = partesFecha[0].toInt()
                mes = partesFecha[1].toInt() - 1 // Calendar usa meses 0-11
                dia = partesFecha[2].toInt()
            } else {
                Log.e("ReservaFragment", "Formato de fecha no reconocido: $fechaSeleccionada")
                return false
            }
            
            // Parsear hora
            val partesHora = horaInicioSeleccionada.split(":")
            if (partesHora.size != 2) {
                Log.e("ReservaFragment", "Formato de hora inválido: $horaInicioSeleccionada")
                return false
            }
            
            val hora = partesHora[0].toInt()
            val minuto = partesHora[1].toInt()
            
            // Establecer fecha y hora seleccionadas
            fechaHoraSeleccionada.set(año, mes, dia, hora, minuto, 0)
            fechaHoraSeleccionada.set(Calendar.MILLISECOND, 0)
            
            // Obtener fecha y hora actuales en zona horaria local
            val fechaHoraActual = Calendar.getInstance()
            Log.d("ReservaFragment", "Zona horaria del dispositivo: ${fechaHoraActual.timeZone.displayName}")
            Log.d("ReservaFragment", "Offset de zona horaria: ${fechaHoraActual.timeZone.rawOffset / (1000 * 60 * 60)} horas")
            
            Log.d("ReservaFragment", "=== COMPARACIÓN DE TIEMPOS ===")
            Log.d("ReservaFragment", "Fecha/hora seleccionada: ${fechaHoraSeleccionada.time}")
            Log.d("ReservaFragment", "Fecha/hora actual: ${fechaHoraActual.time}")
            Log.d("ReservaFragment", "Hora actual del sistema: ${fechaHoraActual.get(Calendar.HOUR_OF_DAY)}:${fechaHoraActual.get(Calendar.MINUTE)}")
            Log.d("ReservaFragment", "Hora seleccionada: $hora:$minuto")
            Log.d("ReservaFragment", "Comparación (seleccionada < actual): ${fechaHoraSeleccionada.before(fechaHoraActual)}")
            
            // Comparar fechas y horas
            if (fechaHoraSeleccionada.before(fechaHoraActual)) {
                // Verificar si es el mismo día para dar mensaje específico
                val fechaActualSinHora = Calendar.getInstance()
                fechaActualSinHora.set(Calendar.HOUR_OF_DAY, 0)
                fechaActualSinHora.set(Calendar.MINUTE, 0)
                fechaActualSinHora.set(Calendar.SECOND, 0)
                fechaActualSinHora.set(Calendar.MILLISECOND, 0)
                
                val fechaSeleccionadaSinHora = Calendar.getInstance()
                fechaSeleccionadaSinHora.set(año, mes, dia, 0, 0, 0)
                fechaSeleccionadaSinHora.set(Calendar.MILLISECOND, 0)
                
                if (fechaSeleccionadaSinHora.equals(fechaActualSinHora)) {
                    Toast.makeText(requireContext(), "⏰ No puedes reservar en horas que ya pasaron. Selecciona una hora futura.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), "📅 No puedes reservar en fechas pasadas. Selecciona una fecha futura.", Toast.LENGTH_LONG).show()
                }
                return false
            }
            
            Log.d("ReservaFragment", "Validación exitosa: fecha y hora son válidas")
            android.util.Log.d("ReservaFragment", "=== FIN VALIDACIÓN: ÉXITO ===")
            return true
            
        } catch (e: Exception) {
            Log.e("ReservaFragment", "Error al validar fecha y hora: ${e.message}", e)
            Toast.makeText(requireContext(), "📅 Error al validar la fecha y hora. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
            return false
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
