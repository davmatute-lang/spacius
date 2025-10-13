package com.example.spacius

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.spacius.data.AppDatabase
import com.example.spacius.data.Reserva
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var db: AppDatabase
    private var rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_maps, container, false)

        db = AppDatabase.getDatabase(requireContext())

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragmentContainer) as? SupportMapFragment
            ?: SupportMapFragment.newInstance().also {
                childFragmentManager.beginTransaction()
                    .replace(R.id.mapFragmentContainer, it)
                    .commit()
            }

        mapFragment.getMapAsync(this)
        return rootView
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = false
        
        // Habilitar ventana de información personalizada
        mMap.setInfoWindowAdapter(null) // Usar ventana por defecto con snippet multilínea

        val guayaquil = LatLng(-2.170998, -79.922359)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(guayaquil, 12f))

        cargarReservasEnMapa()
        
        // 🔹 Listener para clics en los marcadores
        mMap.setOnMarkerClickListener { marker ->
            marker.showInfoWindow()
            true
        }
    }
    
    // 🔹 Nueva función para cargar solo lugares con reservas activas
    private fun cargarReservasEnMapa() {
        lifecycleScope.launch {
            // Limpiar marcadores anteriores
            mMap.clear()
            
            // Obtener todas las reservas activas
            val reservas = db.reservaDao().getAllReservas()
            
            // Controlar visibilidad del mensaje y mapa
            val tvSinReservas = rootView?.findViewById<android.widget.TextView>(R.id.tvSinReservas)
            val mapContainer = rootView?.findViewById<View>(R.id.mapFragmentContainer)
            
            if (reservas.isEmpty()) {
                // Mostrar mensaje cuando no hay reservas
                tvSinReservas?.visibility = View.VISIBLE
                mapContainer?.visibility = View.GONE
                return@launch
            } else {
                // Mostrar mapa cuando hay reservas
                tvSinReservas?.visibility = View.GONE
                mapContainer?.visibility = View.VISIBLE
            }
            
            // Para cada reserva, obtener el lugar y crear marcador
            var primeraReserva = true
            for (reserva in reservas) {
                val lugar = db.lugarDao().getLugarById(reserva.idLugar)
                
                lugar?.let {
                    val coordenada = LatLng(it.latitud, it.longitud)
                    
                    // Crear snippet con información de la reserva
                    val snippetInfo = "📅 ${reserva.fecha}\n" +
                                     "🕐 ${reserva.horaInicio} - ${reserva.horaFin}\n" +
                                     "👤 ${reserva.nombreUsuario}"
                    
                    mMap.addMarker(
                        MarkerOptions()
                            .position(coordenada)
                            .title("📍 ${it.nombre}")
                            .snippet(snippetInfo)
                    )
                    
                    // Centrar la cámara en la primera reserva
                    if (primeraReserva) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordenada, 13f))
                        primeraReserva = false
                    }
                }
            }
        }
    }
    
    // 🔹 Actualizar el mapa cuando el fragment sea visible
    override fun onResume() {
        super.onResume()
        if (::mMap.isInitialized) {
            cargarReservasEnMapa()
        }
    }
}
