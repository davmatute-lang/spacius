package com.example.spacius

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.spacius.data.AppDatabase
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        db = AppDatabase.getDatabase(requireContext())

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragmentContainer) as? SupportMapFragment
            ?: SupportMapFragment.newInstance().also {
                childFragmentManager.beginTransaction()
                    .replace(R.id.mapFragmentContainer, it)
                    .commit()
            }

        mapFragment.getMapAsync(this)
        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

        val guayaquil = LatLng(-2.170998, -79.922359)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(guayaquil, 12f))

        lifecycleScope.launch {
            val lugares = db.lugarDao().getAllLugares()
            for (lugar in lugares) {
                val coordenada = LatLng(lugar.latitud, lugar.longitud)
                mMap.addMarker(
                    MarkerOptions()
                        .position(coordenada)
                        .title(lugar.nombre)
                        .snippet(lugar.descripcion)
                )
            }

            if (lugares.isNotEmpty()) {
                val primerLugar = lugares.first()
                val centro = LatLng(primerLugar.latitud, primerLugar.longitud)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centro, 13f))
            }
        }
    }
}
