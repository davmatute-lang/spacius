package com.example.spacius

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map) // Asegúrate de que este XML sea el correcto (el que dejaste con solo el mapa)

        // Inicialización del mapa
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Marcador en Guayaquil
        val guayaquil = LatLng(-2.1709, -79.9224)
        val zoomLevel = 12.0f
        mMap.addMarker(MarkerOptions().position(guayaquil).title("Guayaquil, Ecuador"))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(guayaquil, zoomLevel))
    }
}
