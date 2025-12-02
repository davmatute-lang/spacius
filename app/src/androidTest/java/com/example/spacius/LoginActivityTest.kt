
package com.example.spacius

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeoutException


@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    private var idlingResource: LoginIdlingResource? = null

    @Before
    fun setUp() {
        // Antes de cada test, nos aseguramos de cerrar la sesión del usuario.
        // Esto garantiza que cada prueba empieza en un estado limpio y predecible.
        FirebaseAuth.getInstance().signOut()
        activityRule.scenario.onActivity { activity ->
            idlingResource = LoginIdlingResource(activity)
            IdlingRegistry.getInstance().register(idlingResource)
        }
    }

    @After
    fun tearDown() {
        if (idlingResource != null) {
            IdlingRegistry.getInstance().unregister(idlingResource)
        }
    }

    @Test
    fun muestraErrorSiCamposVacios() {
        // Se hace clic en el botón de login sin rellenar los campos
        onView(withId(R.id.btnLogin)).perform(click())
        // Aquí faltaría una aserción para verificar que se muestra un error
    }

    @Test
    fun muestraErrorSiCredencialesInvalidas() {
        // Se introducen credenciales incorrectas
        onView(withId(R.id.etCorreo)).perform(typeText("diegodr05@falso.com"), closeSoftKeyboard())
        onView(withId(R.id.etContrasena)).perform(typeText("12345678"), closeSoftKeyboard())
        onView(withId(R.id.btnLogin)).perform(click())
        // Aquí también faltaría una aserción para verificar el error
    }

    @Test(timeout = 60000) // 60 seconds timeout
    fun loginExitosoNavegaAMainActivity() {
        // Asegúrate de que este usuario de prueba exista en Firebase Authentication
        onView(withId(R.id.etCorreo)).perform(typeText("test@example.com"), closeSoftKeyboard())
        onView(withId(R.id.etContrasena)).perform(typeText("password123"), closeSoftKeyboard())

        onView(withId(R.id.btnLogin)).perform(click())

        // Espresso will now wait for the idling resource to become idle before proceeding

        // Verificamos que, tras un login exitoso, aparece un elemento de la pantalla principal,
        // en este caso, la barra de navegación inferior.
        onView(withId(R.id.bottomNavigation)).check(matches(isDisplayed()))
    }
}
