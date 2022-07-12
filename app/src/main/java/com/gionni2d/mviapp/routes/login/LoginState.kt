package com.gionni2d.mviapp.routes.login

import com.gionni2d.mvi.State

data class LoginState(
    val username: String = "",
    val password: String = "",
    val loginButtonLoading: Boolean = false
) : State