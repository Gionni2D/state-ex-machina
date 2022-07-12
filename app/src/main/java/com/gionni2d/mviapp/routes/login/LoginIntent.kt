package com.gionni2d.mviapp.routes.login

import com.gionni2d.mvi.Intent
import com.gionni2d.mviapp.utils.debug.SerializableObject

sealed interface LoginIntent : Intent {

    // User intent

    data class TypeUsername(val value: String) : LoginIntent
    data class TypePassword(val value: String) : LoginIntent
    object ClickLoginButton : LoginIntent, SerializableObject("ClickLoginButton")

    // Event intent

    data class LoginResponse(val value: Boolean) : LoginIntent
}

