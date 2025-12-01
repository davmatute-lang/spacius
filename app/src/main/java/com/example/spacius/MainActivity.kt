package com.example.spacius

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.spacius.utils.NotificationUtils
import com.example.spacius.viewmodels.NotificationViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private val calendarFragment by lazy { CalendarFragment() } // Instancia única del calendario

    // --- ViewModel ---
    private val notificationViewModel: NotificationViewModel by viewModels()

    // --- 🎨 UI para el contador de notificaciones ---
    private var notificationBadge: TextView? = null

    // --- 🔔 Lanuncher para el permiso de notificaciones ---
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permiso concedido. No se necesita hacer nada extra aquí.
        } else {
            // Permiso denegado. Las notificaciones no se mostrarán.
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Crear canal de notificaciones
        NotificationUtils.createNotificationChannel(this)

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

        // Pedir permiso al iniciar
        askNotificationPermission()
        
        // Observar el contador de notificaciones
        notificationViewModel.unreadNotificationsCount.observe(this) {
            count -> updateNotificationBadge(count)
        }
    }

    private fun askNotificationPermission() {
        // Solo es necesario para Android 13 (API 33) en adelante
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Si el permiso no está concedido, se solicita
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        // --- 🎨 Configurar el contador de notificaciones ---
        val notificationItem = menu?.findItem(R.id.action_notifications)
        val actionView = notificationItem?.actionView
        notificationBadge = actionView?.findViewById(R.id.notification_badge)

        actionView?.setOnClickListener {
            onOptionsItemSelected(notificationItem)
        }

        return true
    }

    // --- 🎨 Funciones para el contador de notificaciones ---
    fun updateNotificationBadge(count: Int) {
        if (notificationBadge == null) return

        runOnUiThread {
            if (count > 0) {
                notificationBadge?.text = count.toString()
                notificationBadge?.visibility = View.VISIBLE
            } else {
                notificationBadge?.visibility = View.GONE
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notifications -> {
                updateNotificationBadge(0) // Limpiar contador al abrir
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, NotificationHistoryFragment()) // Volver a usar el fragmento de historial
                    .addToBackStack(null)
                    .commit()
                true
            }
            else -> super.onOptionsItemSelected(item)
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

    fun setSelectedBottomNav(itemId: Int) {
        bottomNavigation.selectedItemId = itemId
    }

    fun marcarFechaEnCalendario(fecha: String) {
        setSelectedBottomNav(R.id.nav_calendario)
        supportActionBar?.title = "Calendario"
    }

    fun navegarACalendario() {
        setSelectedBottomNav(R.id.nav_calendario)
        supportActionBar?.title = "Calendario"
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, calendarFragment, "CALENDAR")
            .commit()
    }

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

    fun actualizarCalendarioDesdeDetalle() {
        if (calendarFragment.isAdded) {
            calendarFragment.actualizarDespuesDeCancelacion()
        }
        actualizarMapsFragment()
        actualizarHomeFragment()
    }

    fun actualizarHomeFragment() {
        val homeFragment = supportFragmentManager.findFragmentByTag("HOME") as? HomeFragment
        homeFragment?.let { }
    }

    fun actualizarMapsFragment() {
        val mapsFragment = supportFragmentManager.findFragmentByTag("MAPS") as? MapsFragment
        mapsFragment?.let { }
    }
}
