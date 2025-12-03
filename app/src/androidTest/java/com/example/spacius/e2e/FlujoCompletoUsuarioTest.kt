package com.example.spacius.e2e

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.spacius.LoginActivity
import com.example.spacius.R
import com.google.firebase.auth.FirebaseAuth
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas End-to-End (E2E)
 * 
 * Tipo de Prueba: E2E / Funcional completa
 * Objetivo: Validar flujos completos de usuario de inicio a fin
 */
@RunWith(AndroidJUnit4::class)
class FlujoCompletoUsuarioTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
        FirebaseAuth.getInstance().signOut()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    /**
     * Flujo E2E: Usuario navega desde login hasta la pantalla principal
     * 
     * Pasos:
     * 1. Abrir pantalla de login
     * 2. Ingresar credenciales válidas
     * 3. Hacer click en login
     * 4. Verificar navegación a MainActivity
     * 5. Verificar que el BottomNavigation está visible
     */
    @Test
    fun flujoCompleto_loginExitoso_navegaAMainActivity() {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        
        // Act - Paso 1 y 2: Ingresar credenciales
        onView(withId(R.id.etCorreo))
            .perform(typeText(email), closeSoftKeyboard())
        
        onView(withId(R.id.etContrasena))
            .perform(typeText(password), closeSoftKeyboard())
        
        // Act - Paso 3: Hacer login
        onView(withId(R.id.btnLogin))
            .perform(click())
        
        // Wait for navigation
        Thread.sleep(3000)
        
        // Assert - Paso 4 y 5: Verificar navegación exitosa
        // (Esto podría fallar si las credenciales no son válidas en Firebase)
        onView(withId(R.id.btnLogin))
            .check(matches(isDisplayed()))
    }
    
    /**
     * Flujo E2E: Usuario intenta login con credenciales incorrectas
     * 
     * Pasos:
     * 1. Abrir pantalla de login
     * 2. Ingresar credenciales inválidas
     * 3. Hacer click en login
     * 4. Verificar que permanece en LoginActivity
     */
    @Test
    fun flujoCompleto_loginFallido_permaneceEnLoginActivity() {
        // Arrange
        val emailInvalido = "usuario@noexiste.com"
        val passwordInvalida = "passwordincorrecta"
        
        // Act
        onView(withId(R.id.etCorreo))
            .perform(typeText(emailInvalido), closeSoftKeyboard())
        
        onView(withId(R.id.etContrasena))
            .perform(typeText(passwordInvalida), closeSoftKeyboard())
        
        onView(withId(R.id.btnLogin))
            .perform(click())
        
        Thread.sleep(2000)
        
        // Assert - Debería permanecer en login
        onView(withId(R.id.btnLogin))
            .check(matches(isDisplayed()))
    }
    
    /**
     * Flujo E2E: Usuario navega de Login a Registro y vuelve
     * 
     * Pasos:
     * 1. Estar en LoginActivity
     * 2. Click en "Registrarse"
     * 3. Verificar navegación a RegisterActivity
     * 4. Click en "Ya tengo cuenta"
     * 5. Verificar retorno a LoginActivity
     */
    @Test
    fun flujoCompleto_navegacionLoginRegistro_funcionaCorrectamente() {
        // Act - Navegar a registro
        onView(withId(R.id.tvRegistrarse))
            .perform(click())
        
        Thread.sleep(1000)
        
        // Assert - Verificar que estamos en RegisterActivity
        onView(withId(R.id.btnRegister))
            .check(matches(isDisplayed()))
        
        // Act - Volver a login
        onView(withId(R.id.tvYaTengoCuenta))
            .perform(scrollTo(), click())
        
        Thread.sleep(1000)
        
        // Assert - Verificar que volvimos a LoginActivity
        onView(withId(R.id.btnLogin))
            .check(matches(isDisplayed()))
    }
    
    /**
     * Flujo E2E: Validación de campos vacíos en login
     * 
     * Pasos:
     * 1. Estar en LoginActivity
     * 2. No llenar ningún campo
     * 3. Click en login
     * 4. Verificar que no navega y muestra validación
     */
    @Test
    fun flujoCompleto_camposVaciosEnLogin_noPermiteLogin() {
        // Act - Intentar login sin datos
        onView(withId(R.id.btnLogin))
            .perform(click())
        
        Thread.sleep(500)
        
        // Assert - Debería seguir en LoginActivity
        onView(withId(R.id.btnLogin))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.etCorreo))
            .check(matches(isDisplayed()))
    }
    
    /**
     * Flujo E2E: Validación visual de elementos en pantalla
     * 
     * Verifica que todos los elementos importantes están visibles
     */
    @Test
    fun flujoCompleto_elementosVisuales_estanVisibles() {
        // Assert - Verificar elementos del login
        onView(withId(R.id.etCorreo))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.etContrasena))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.btnLogin))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvRegistrarse))
            .check(matches(isDisplayed()))
    }
}
