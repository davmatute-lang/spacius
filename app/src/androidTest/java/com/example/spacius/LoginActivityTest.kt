package com.example.spacius

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    private var idlingResource: IdlingResource? = null

    @Before
    fun setUp() {
        FirebaseAuth.getInstance().signOut()
        Intents.init()
        activityRule.scenario.onActivity { activity ->
            idlingResource = LoginActivityIdlingResource(activity.isIdle)
            IdlingRegistry.getInstance().register(idlingResource)
        }
    }

    @After
    fun tearDown() {
        Intents.release()
        idlingResource?.let {
            IdlingRegistry.getInstance().unregister(it)
        }
    }

    @Test
    fun muestraErrorSiCamposVacios() {
        onView(withId(R.id.btnLogin)).perform(click())
    }

    @Test
    fun muestraErrorSiCredencialesInvalidas() {
        onView(withId(R.id.etCorreo)).perform(typeText("diegodr05@falso.com"), closeSoftKeyboard())
        onView(withId(R.id.etContrasena)).perform(typeText("12345678"), closeSoftKeyboard())
        onView(withId(R.id.btnLogin)).perform(click())
    }

    @Test(timeout = 60000) // 60 seconds timeout
    fun loginExitosoNavegaAMainActivity() {
        onView(withId(R.id.etCorreo)).perform(typeText("test@example.com"), closeSoftKeyboard())
        onView(withId(R.id.etContrasena)).perform(typeText("password123"), closeSoftKeyboard())

        onView(withId(R.id.btnLogin)).perform(click())

        // Wait for the login to complete
        Thread.sleep(2000)

        Intents.intended(hasComponent(MainActivity::class.java.name))
    }
}
