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

            // Establecer valores en las vistas
            txtNombreLugar.text = nombreLugar
            txtDescripcion.text = descripcion
            txtFechaReserva.text = "📅 $fecha"
            txtHorario.text = "🕐 $horaInicio - $horaFin"
            txtUsuario.text = "👤 Reservado por: $usuario"

            // Cargar imagen del lugar
            if (imagenUrl.isNotEmpty()) {
                Glide.with(this)
                    .load(imagenUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imgLugar)
            }
        }

        // Configurar botón de cancelar reserva
        btnCancelarReserva.setOnClickListener {
            mostrarDialogoCancelacion()
        }

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

    private fun mostrarDialogoCancelacion() {
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