package com.example.spacius.fragments

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.spacius.NotificationsFragment
import com.example.spacius.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas de Integración para NotificationsFragment
 * 
 * Tipo de Prueba: Integración (UI + Preferencias)
 * Objetivo: Validar la gestión de preferencias de notificaciones
 */
@RunWith(AndroidJUnit4::class)
class NotificationsFragmentTest {

    private lateinit var scenario: FragmentScenario<NotificationsFragment>

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer<NotificationsFragment>(
            themeResId = R.style.Theme_Spacius
        )
    }

    @Test
    fun notificationsFragment_seVisualizaCorrectamente() {
        // Assert - Verificar que el fragment se carga
        onView(withId(R.id.switch_all_notifications))
            .check(matches(isDisplayed()))
    }
    
    @Test
    fun switchTodasNotificaciones_estaVisible() {
        // Assert
        onView(withId(R.id.switch_all_notifications))
            .check(matches(isDisplayed()))
    }
    
    @Test
    fun switchRecordatoriosReservas_estaVisible() {
        // Assert
        onView(withId(R.id.switch_booking_reminders))
            .check(matches(isDisplayed()))
    }
    
    @Test
    fun switchConfirmaciones_estaVisible() {
        // Assert
        onView(withId(R.id.switch_booking_confirmations))
            .check(matches(isDisplayed()))
    }
    
    @Test
    fun switchProximasReservas_estaVisible() {
        // Assert
        onView(withId(R.id.switch_new_spaces))
            .check(matches(isDisplayed()))
    }
}
