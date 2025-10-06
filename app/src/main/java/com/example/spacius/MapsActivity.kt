package com.example.spacius

import android.content.Intent // ¡Necesaria si quieres que el botón 'Inicio' vuelva a MainActivity!
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView // ¡NUEVA IMPORTACIÓN!
import kotlin.jvm.java

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    // Define la Activity a la que se debe volver (la principal)
    private val MAIN_ACTIVITY_CLASS = MainActivity::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map) // Carga el layout que contiene el mapa y el BottomNav

        // 1. Inicialización y carga del mapa (Tu código original)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // 🚀 2. INICIALIZACIÓN DE LA BARRA DE NAVEGACIÓN INFERIOR 🚀
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // ESTABLECER EL BOTÓN DE MAPA COMO SELECCIONADO POR DEFECTO
        bottomNav.selectedItemId = R.id.nav_mapa // <-- ¡ESTO SELECCIONA EL MAPA VISUALMENTE!

        // Manejar el clic del botón 'Inicio' para volver a la pantalla principal
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    // Crea un Intent para volver a la MainActivity
                    val intent = Intent(this, MAIN_ACTIVITY_CLASS)
                    startActivity(intent)
                    finish() // Cierra MapsActivity para que no quede en la pila (mejor práctica)
                    true
                }
                R.id.nav_mapa -> true // Ya estás aquí, no hagas nada
                // Los otros botones deberían navegar a sus respectivas Activities/Fragments
                R.id.nav_calendario -> true
                R.id.nav_perfil -> true
                else -> false
            }
        }
    }

    /**
     * El resto de tu lógica de mapa permanece igual.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val guayaquil = LatLng(-2.1709, -79.9224)
        val zoomLevel = 12.0f
        mMap.addMarker(MarkerOptions().position(guayaquil).title("Guayaquil, Ecuador"))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(guayaquil, zoomLevel))
    }
}