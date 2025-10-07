package com.example.spacius

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun muestraErrorSiCamposVacios() {
        onView(withId(R.id.btnLogin)).perform(click())
    }

    @Test
    fun muestraErrorSiCredencialesInvalidas() {
        onView(withId(R.id.etEmail)).perform(typeText("usuario@falso.com"), closeSoftKeyboard())
        onView(withId(R.id.etPassword)).perform(typeText("123456"), closeSoftKeyboard())
        onView(withId(R.id.btnLogin)).perform(click())
    }
}
