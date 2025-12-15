package com.example.spacius.fragments

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.spacius.HomeFragment
import com.example.spacius.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas de Integración para HomeFragment
 * 
 * Tipo de Prueba: Integración (UI + Lógica)
 * Objetivo: Validar la correcta visualización y carga de elementos del Home
 */
@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {

    private lateinit var scenario: FragmentScenario<HomeFragment>

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer<HomeFragment>(
            themeResId = R.style.Theme_Spacius
        )
    }

    @Test
    fun homeFragment_seVisualizaCorrectamente() {
        // Assert - Verificar que el fragment se carga
        onView(withId(R.id.txtMisReservas))
            .check(matches(isDisplayed()))
    }
    
    @Test
    fun recyclerViewLugares_estaVisible() {
        // Assert - Verificar que el RecyclerView está visible
        onView(withId(R.id.recyclerLugares))
            .check(matches(isDisplayed()))
    }
}
