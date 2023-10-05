package com.gionni2d.mviapp.routes.login

import com.gionni2d.mvi.foundation.Intent
import com.gionni2d.mviapp.utils.debug.SerializableObject

sealed interface LoginIntent : Intent {

    // User intent

    data class TypeUsername(val value: String) : LoginIntent
    data class TypePassword(val value: String) : LoginIntent
    object Login : LoginIntent, SerializableObject("Login")
    object Logout : LoginIntent, SerializableObject("Logout")
}
