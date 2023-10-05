package com.gionni2d.mviapp.routes.login

import com.gionni2d.mvi.foundation.Reducer
import com.gionni2d.mviapp.domain.AuthenticationStatus
import com.gionni2d.mviapp.domain.presentation.LoadingResult
import com.gionni2d.mviapp.domain.presentation.toLoadingResult

interface LoginReducerFactory {
    fun typeUsername(i: LoginIntent.TypeUsername): Reducer<LoginState>

    fun typePassword(i: LoginIntent.TypePassword): Reducer<LoginState>

    val startLoading: Reducer<LoginState>

    fun updateAuthentication(d: Result<AuthenticationStatus>): Reducer<LoginState>
}

object LoginReducerFactoryImpl : LoginReducerFactory {
    override fun typeUsername(i: LoginIntent.TypeUsername) = Reducer<LoginState> { s ->
        s.copy(username = i.value)
    }

    override fun typePassword(i: LoginIntent.TypePassword) = Reducer<LoginState> { s ->
        s.copy(password = i.value)
    }

    override val startLoading = Reducer<LoginState> { s ->
        s.copy(authStatus = LoadingResult.Loading)
    }

    override fun updateAuthentication(d: Result<AuthenticationStatus>) = Reducer<LoginState> { s ->
        s.copy(authStatus = d.toLoadingResult())
    }
}