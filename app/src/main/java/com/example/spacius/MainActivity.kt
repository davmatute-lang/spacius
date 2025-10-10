package com.example.spacius

import android.os.Bundle
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
            loadFragment(HomeFragment())
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    loadFragment(HomeFragment())
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
                    loadFragment(MapsFragment())
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

    // ✅ Función segura para marcar fecha desde ReservaFragment
    fun marcarFechaEnCalendario(fecha: String) {
        // Comprobar si el fragmento está agregado antes de llamar a la función
        if (calendarFragment.isAdded) {
            calendarFragment.marcarFechaDesdeReserva(fecha)
        } else {
            // Si aún no está agregado, lo agregamos primero
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, calendarFragment, "CALENDAR")
                .commitNow()
            calendarFragment.marcarFechaDesdeReserva(fecha)
        }

        // Cambiar pestaña a calendario
        setSelectedBottomNav(R.id.nav_calendario)
    }
}

