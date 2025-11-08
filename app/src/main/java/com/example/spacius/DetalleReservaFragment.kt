package com.example.spacius

import android.app.AlertDialog
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
import com.example.spacius.data.FirestoreRepository
import com.example.spacius.utils.DateTimeUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class DetalleReservaFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var firestoreRepository: FirestoreRepository
    
    private var reservaId: String = ""
    private var latLugar: Double = -2.170998
    private var lngLugar: Double = -79.922359
    
    // Variables para validar si la reserva ya pasó
    private var fechaReserva: String = ""
    private var horaFinReserva: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detalle_reserva, container, false)

        firestoreRepository = FirestoreRepository()

        // Obtener referencias de las vistas
        val txtNombreLugar: TextView = view.findViewById(R.id.txtNombreLugarDetalle)
        val txtDescripcion: TextView = view.findViewById(R.id.txtDescripcionLugarDetalle)
        val txtFechaReserva: TextView = view.findViewById(R.id.txtFechaReservaDetalle)
        val txtHorario: TextView = view.findViewById(R.id.txtHorarioDetalle)
        val txtUsuario: TextView = view.findViewById(R.id.txtUsuarioDetalle)
        val imgLugar: ImageView = view.findViewById(R.id.imgLugarDetalle)
        
        val btnCancelarReserva: Button = view.findViewById(R.id.btnCancelarReserva)
        val btnVolver: Button = view.findViewById(R.id.btnVolverCalendario)

        // Obtener datos pasados desde el calendario
        arguments?.let { args ->
            reservaId = args.getString("reservaId", "")
            val nombreLugar = args.getString("nombreLugar", "")
            val descripcion = args.getString("descripcionLugar", "")
            val fecha = args.getString("fecha", "")
            val horaInicio = args.getString("horaInicio", "")
            val horaFin = args.getString("horaFin", "")
            val usuario = args.getString("usuario", "")
            val imagenUrl = args.getString("imagenUrl", "")
            latLugar = args.getDouble("latitud", -2.170998)
            lngLugar = args.getDouble("longitud", -79.922359)

            // Guardar fecha y hora para validación (convertir DD/MM/YYYY a YYYY-MM-DD)
            fechaReserva = convertirFechaAFormatoISO(fecha)
            horaFinReserva = horaFin
            
            // Establecer valores en las vistas
            txtNombreLugar.text = nombreLugar
            txtDescripcion.text = descripcion
            
            // 🆕 Verificar si la reserva ya pasó y mostrar indicador
            val yaOcurrio = validarSiReservaYaPaso(fechaReserva, horaFin)
            if (yaOcurrio) {
                txtFechaReserva.text = "📅 $fecha ⏱️ (Completada)"
                txtFechaReserva.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
            } else {
                txtFechaReserva.text = "📅 $fecha"
            }
            
            txtHorario.text = "🕐 $horaInicio - $horaFin"
            txtUsuario.text = "👤 Reservado por: $usuario"

            // Cargar imagen del lugar
            if (imagenUrl.isNotEmpty()) {
                Glide.with(this)
                    .load(imagenUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imgLugar)
            }
            
            // 🆕 Configurar botón de cancelar según si la reserva ya pasó
            configurarBotonCancelar(btnCancelarReserva, yaOcurrio)
        }

        // El listener del botón se configura en configurarBotonCancelar()
        
        // Configurar botón de volver
        btnVolver.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // Configurar mapa
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragmentDetalle) as? SupportMapFragment
            ?: SupportMapFragment.newInstance().also {
                childFragmentManager.beginTransaction()
                    .replace(R.id.mapFragmentDetalle, it)
                    .commit()
            }
        mapFragment.getMapAsync(this)

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val lugar = LatLng(latLugar, lngLugar)
        map.clear()
        map.addMarker(MarkerOptions().position(lugar).title("Ubicación del Lugar"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(lugar, 15f))
        map.uiSettings.isZoomControlsEnabled = true
    }
    
    /**
     * 🆕 Convierte fecha de DD/MM/YYYY a YYYY-MM-DD para validación
     */
    private fun convertirFechaAFormatoISO(fechaMostrar: String): String {
        val parts = fechaMostrar.split("/")
        return if (parts.size == 3) {
            "${parts[2]}-${parts[1]}-${parts[0]}" // YYYY-MM-DD
        } else {
            fechaMostrar
        }
    }
    
    /**
     * 🆕 Valida si la reserva ya pasó usando DateTimeUtils
     */
    private fun validarSiReservaYaPaso(fecha: String, horaFin: String): Boolean {
        return try {
            // Usar la función de DateTimeUtils que valida si es futura
            // Si NO es futura, entonces ya pasó
            !DateTimeUtils.esFechaHoraFutura(fecha, horaFin)
        } catch (e: Exception) {
            android.util.Log.e("DetalleReserva", "Error validando fecha: ${e.message}")
            false // En caso de error, asumir que no ha pasado para evitar bloquear funcionalidad
        }
    }
    
    /**
     * 🆕 Configura el botón de cancelar según si la reserva ya pasó
     */
    private fun configurarBotonCancelar(boton: Button, yaOcurrio: Boolean) {
        if (yaOcurrio) {
            // La reserva ya pasó - deshabilitar cancelación
            boton.isEnabled = false
            boton.alpha = 0.5f
            boton.text = "✓ Reserva Completada"
            boton.setBackgroundColor(resources.getColor(android.R.color.darker_gray, null))
            
            // Opcional: mostrar Toast informativo si intentan hacer clic
            boton.setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    "ℹ️ Esta reserva ya finalizó. No se puede cancelar.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            // La reserva es futura - permitir cancelación
            boton.isEnabled = true
            boton.alpha = 1.0f
            boton.text = "Cancelar Reserva"
            
            boton.setOnClickListener {
                mostrarDialogoCancelacion()
            }
        }
    }

    private fun mostrarDialogoCancelacion() {
        // 🆕 Doble validación antes de mostrar el diálogo
        if (validarSiReservaYaPaso(fechaReserva, horaFinReserva)) {
            Toast.makeText(
                requireContext(),
                "❌ No puedes cancelar una reserva que ya finalizó",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        
        AlertDialog.Builder(requireContext())
            .setTitle("⚠️ Cancelar Reserva")
            .setMessage("¿Estás seguro de que deseas cancelar esta reserva?\n\nEsta acción no se puede deshacer.")
            .setPositiveButton("Sí, cancelar") { _, _ ->
                cancelarReserva()
            }
            .setNegativeButton("No, mantener") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun cancelarReserva() {
        // 🆕 Triple validación antes de ejecutar la cancelación
        if (validarSiReservaYaPaso(fechaReserva, horaFinReserva)) {
            Toast.makeText(
                requireContext(),
                "❌ No puedes cancelar una reserva que ya finalizó",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        
        lifecycleScope.launch {
            try {
                // Eliminar de Firestore
                firestoreRepository.eliminarReserva(reservaId)
                
                Toast.makeText(
                    requireContext(), 
                    "✅ Reserva cancelada exitosamente", 
                    Toast.LENGTH_LONG
                ).show()

                // Volver al calendario (se actualizará automáticamente en onResume)
                requireActivity().supportFragmentManager.popBackStack()
                
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(), 
                    "❌ Error al cancelar la reserva: ${e.message}", 
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}