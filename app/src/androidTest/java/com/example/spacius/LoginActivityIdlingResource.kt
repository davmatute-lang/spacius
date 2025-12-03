package com.example.spacius

import androidx.test.espresso.IdlingResource
import java.util.concurrent.atomic.AtomicBoolean

class LoginActivityIdlingResource(private val isIdle: AtomicBoolean) : IdlingResource {

    private var resourceCallback: IdlingResource.ResourceCallback? = null

    override fun getName(): String {
        return LoginActivityIdlingResource::class.java.name
    }

    override fun isIdleNow(): Boolean {
        val idle = isIdle.get()
        if (idle) {
            resourceCallback?.onTransitionToIdle()
        }
        return idle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.resourceCallback = callback
    }
}