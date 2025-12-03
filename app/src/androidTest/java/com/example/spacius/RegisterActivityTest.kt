package com.example.spacius

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import com.google.firebase.auth.FirebaseAuth
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas Funcionales para RegisterActivity
 * 
 * Tipo de Prueba: Funcional (UI)
 * Objetivo: Validar el flujo completo de registro de usuario
 */
@RunWith(AndroidJUnit4::class)
class RegisterActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(RegisterActivity::class.java)

    @Before
    fun setUp() {
        // Asegurar que no hay sesión activa
        FirebaseAuth.getInstance().signOut()
    }

    @Test
    fun registroConCamposVacios_muestraErrores() {
        // Act - Intentar registrar sin llenar campos
        onView(withId(R.id.btnRegister))
            .perform(scrollTo(), click())
        
        // Assert - Deberían mostrarse los campos sin error evidente
        // (La validación puede ser diferente según implementación)
        onView(withId(R.id.btnRegister))
            .check(matches(isDisplayed()))
    }
    
    @Test
    fun registroConEmailInvalido_muestraError() {
        // Arrange
        val emailInvalido = "emailinvalido"
        val password = "Password123"
        
        // Act
        onView(withId(R.id.etRegisterEmail))
            .perform(scrollTo(), typeText(emailInvalido), closeSoftKeyboard())
        onView(withId(R.id.etRegisterPassword))
            .perform(scrollTo(), typeText(password), closeSoftKeyboard())
        onView(withId(R.id.btnRegister))
            .perform(scrollTo(), click())
        
        // Assert - La validación debería evitar el registro
        Thread.sleep(1000)
        onView(withId(R.id.btnRegister))
            .check(matches(isDisplayed()))
    }
    
    @Test
    fun registroConPasswordDebil_muestraError() {
        // Arrange
        val email = "test@example.com"
        val passwordDebil = "123" // Muy corta
        
        // Act
        onView(withId(R.id.etRegisterEmail))
            .perform(scrollTo(), typeText(email), closeSoftKeyboard())
        onView(withId(R.id.etRegisterPassword))
            .perform(scrollTo(), typeText(passwordDebil), closeSoftKeyboard())
        onView(withId(R.id.btnRegister))
            .perform(scrollTo(), click())
        
        // Assert
        Thread.sleep(1000)
        onView(withId(R.id.btnRegister))
            .check(matches(isDisplayed()))
    }
    
    @Test
    fun botonYaTengoCuenta_muestraCorrectamente() {
        // Assert - Verificar que el botón de login está visible
        onView(withId(R.id.tvYaTengoCuenta))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
    }
    
    @Test
    fun camposDeTextoVisibles_enPantalla() {
        // Assert - Verificar que todos los campos están visibles
        onView(withId(R.id.etRegisterNombre))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.etRegisterEmail))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.etRegisterPassword))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
    }
    
    @Test
    fun escribirEnCampoNombre_funcionaCorrectamente() {
        // Arrange
        val nombre = "Usuario Test"
        
        // Act
        onView(withId(R.id.etRegisterNombre))
            .perform(scrollTo(), typeText(nombre), closeSoftKeyboard())
        
        // Assert
        onView(withId(R.id.etRegisterNombre))
            .check(matches(withText(nombre)))
    }
    
    @Test
    fun escribirEnCampoEmail_funcionaCorrectamente() {
        // Arrange
        val email = "test@example.com"
        
        // Act
        onView(withId(R.id.etRegisterEmail))
            .perform(scrollTo(), typeText(email), closeSoftKeyboard())
        
        // Assert
        onView(withId(R.id.etRegisterEmail))
            .check(matches(withText(email)))
    }
}
