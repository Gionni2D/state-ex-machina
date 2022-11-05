package com.gionni2d.mviapp.routes.login

import com.gionni2d.mvi.Event
import com.gionni2d.mvi.Intent
import com.gionni2d.mviapp.domain.AuthenticationStatus
import com.gionni2d.mviapp.utils.debug.SerializableObject

sealed interface LoginIntent : Intent {

    // User intent

    data class TypeUsername(val value: String) : LoginIntent
    data class TypePassword(val value: String) : LoginIntent
    object Login : LoginIntent, SerializableObject("Login")
    object Logout : LoginIntent, SerializableObject("Logout")
}

data class AuthenticationResponse(val value: Result<AuthenticationStatus>) : Event
