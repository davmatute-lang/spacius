package com.example.spacius

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.spacius.ui.HomeFragment
import com.example.spacius.CalendarFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private val calendarFragment by lazy { CalendarFragment() } // Instancia única del calendario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Manejo de EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar BottomNavigationView
        setupBottomNavigation()

        // Cargar fragment inicial
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment(), "HOME")
                .commit()
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment(), "HOME")
                        .commit()
                    true
                }
                R.id.nav_calendario -> {
                    // 🔹 Agregamos CalendarFragment con tag "CALENDAR"
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, calendarFragment, "CALENDAR")
                        .commit()
                    true
                }
                R.id.nav_mapa -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, MapsFragment(), "MAPS")
                        .commit()
                    true
                }

                R.id.nav_perfil -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Función para cambiar la pestaña del BottomNavigation
    fun setSelectedBottomNav(itemId: Int) {
        bottomNavigation.selectedItemId = itemId
    }

    // ✅ Función simplificada para cambiar a calendario (se actualiza automáticamente)
    fun marcarFechaEnCalendario(fecha: String) {
        // El calendario se actualiza automáticamente en onResume, 
        // solo necesitamos cambiar a la pestaña del calendario
        setSelectedBottomNav(R.id.nav_calendario)
        
        android.util.Log.d("MainActivity", "Navegando a calendario - se actualizará automáticamente")
    }

    // 🔹 Función simplificada para procesar reservas completas
    fun procesarReservaCompleta(
        idLugar: Int,
        nombreLugar: String,
        fecha: String,
        horaInicio: String,
        horaFin: String
    ) {
        // El calendario se actualiza automáticamente en onResume,
        // solo necesitamos cambiar a la pestaña del calendario
        setSelectedBottomNav(R.id.nav_calendario)
        
        // Actualizar HomeFragment y MapsFragment para reflejar que el lugar ya no está disponible
        actualizarHomeFragment()
        actualizarMapsFragment()
        
        android.util.Log.d("MainActivity", "Reserva procesada - calendario se actualizará automáticamente")
    }

    // 🔹 Nueva función para actualizar calendario desde DetalleReservaFragment
    fun actualizarCalendarioDesdeDetalle() {
        if (calendarFragment.isAdded) {
            calendarFragment.actualizarDespuesDeCancelacion()
        }
        // También actualizar el mapa y el home
        actualizarMapsFragment()
        actualizarHomeFragment()
    }
    
    // 🔹 Nueva función para actualizar HomeFragment después de una reserva o cancelación
    fun actualizarHomeFragment() {
        val homeFragment = supportFragmentManager.findFragmentByTag("HOME") as? HomeFragment
        homeFragment?.let {
            // El onResume del fragment se encargará de recargar los datos
        }
    }
    
    // 🔹 Nueva función para actualizar MapsFragment después de una reserva o cancelación
    fun actualizarMapsFragment() {
        val mapsFragment = supportFragmentManager.findFragmentByTag("MAPS") as? MapsFragment
        mapsFragment?.let {
            // El onResume del fragment se encargará de recargar los datos
        }
    }
}

