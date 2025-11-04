package com.example.spacius

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private val calendarFragment by lazy { CalendarFragment() } // Instancia única del calendario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Configurar la Toolbar
        val topAppBar: Toolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(topAppBar)

        // Manejo de EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar BottomNavigationView
        setupBottomNavigation()

        // Cargar fragment inicial y establecer título
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment(), "HOME")
                .commit()
            supportActionBar?.title = "Inicio"
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
                    supportActionBar?.title = "Inicio"
                    true
                }
                R.id.nav_calendario -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, calendarFragment, "CALENDAR")
                        .commit()
                    supportActionBar?.title = "Calendario"
                    true
                }
                R.id.nav_mapa -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, MapsFragment(), "MAPS")
                        .commit()
                    supportActionBar?.title = "Mapa"
                    true
                }
                R.id.nav_perfil -> {
                    loadFragment(SettingsFragment())
                    supportActionBar?.title = "Ajustes"
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
        setSelectedBottomNav(R.id.nav_calendario)
        supportActionBar?.title = "Calendario"
    }

    // ✅ Función para navegar al calendario después de una reserva exitosa
    fun navegarACalendario() {
        setSelectedBottomNav(R.id.nav_calendario)
        supportActionBar?.title = "Calendario"
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, calendarFragment, "CALENDAR")
            .commit()
    }

    // 🔹 Función simplificada para procesar reservas completas
    fun procesarReservaCompleta(
        idLugar: Int,
        nombreLugar: String,
        fecha: String,
        horaInicio: String,
        horaFin: String
    ) {
        setSelectedBottomNav(R.id.nav_calendario)
        supportActionBar?.title = "Calendario"
        actualizarHomeFragment()
        actualizarMapsFragment()
    }

    // 🔹 Nueva función para actualizar calendario desde DetalleReservaFragment
    fun actualizarCalendarioDesdeDetalle() {
        if (calendarFragment.isAdded) {
            calendarFragment.actualizarDespuesDeCancelacion()
        }
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
