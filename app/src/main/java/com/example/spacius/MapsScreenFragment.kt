package com.example.spacius

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsScreenFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps_screen, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
            ?: SupportMapFragment.newInstance().also {
                childFragmentManager.beginTransaction()
                    .replace(R.id.map, it)
                    .commit()
            }

        mapFragment.getMapAsync(this)
        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Marcador ejemplo en Guayaquil
        val guayaquil = LatLng(-2.170998, -79.922359)
        map.addMarker(MarkerOptions().position(guayaquil).title("Guayaquil"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(guayaquil, 13f))

        // Habilitar controles de zoom
        map.uiSettings.isZoomControlsEnabled = true
    }
}
