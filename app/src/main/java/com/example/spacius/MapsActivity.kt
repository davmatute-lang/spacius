package com.example.spacius

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import com.example.spacius.data.FirestoreRepository

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var firestoreRepository: FirestoreRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Inicializamos el repositorio de Firestore
        firestoreRepository = FirestoreRepository()

        // Inicialización del mapa
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

        // Marcador inicial en Guayaquil
        val guayaquil = LatLng(-2.1709, -79.9224)
        mMap.addMarker(MarkerOptions().position(guayaquil).title("Guayaquil, Ecuador"))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(guayaquil, 12f))

        // � Cargar todos los lugares desde Firestore y agregarlos al mapa
        lifecycleScope.launch {
            val lugares = firestoreRepository.obtenerLugares()
            for (lugar in lugares) {
                val coordenada = LatLng(lugar.latitud, lugar.longitud)
                mMap.addMarker(
                    MarkerOptions()
                        .position(coordenada)
                        .title(lugar.nombre)
                        .snippet(lugar.descripcion)
                )
            }

            // Centrar la cámara en el primer lugar si existe
            if (lugares.isNotEmpty()) {
                val primerLugar = lugares.first()
                val centro = LatLng(primerLugar.latitud, primerLugar.longitud)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centro, 13f))
            }
        }
    }
}
