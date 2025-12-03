package com.example.spacius

import androidx.test.espresso.IdlingResource

class LoginIdlingResource(private val loginActivity: LoginActivity) : IdlingResource {

    private var resourceCallback: IdlingResource.ResourceCallback? = null

    override fun getName(): String = LoginIdlingResource::class.java.name

    override fun isIdleNow(): Boolean {
        val idle = loginActivity.isIdle.get()
        if (idle) {
            resourceCallback?.onTransitionToIdle()
        }
        return idle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.resourceCallback = callback
    }
}
