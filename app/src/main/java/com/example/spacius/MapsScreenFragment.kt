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
    private var lat: Double = -2.170998  // valor por defecto
    private var lng: Double = -79.922359 // valor por defecto
    private var nombreLugar: String = "Guayaquil" // valor por defecto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lat = it.getDouble("latitud", lat)
            lng = it.getDouble("longitud", lng)
            nombreLugar = it.getString("nombreLugar", nombreLugar)
        }
    }

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

        val ubicacion = LatLng(lat, lng)
        map.addMarker(MarkerOptions().position(ubicacion).title(nombreLugar))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 16f))

        map.uiSettings.isZoomControlsEnabled = true
    }
}

