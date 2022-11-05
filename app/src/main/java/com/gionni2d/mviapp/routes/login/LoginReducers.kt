package com.gionni2d.mviapp.routes.login

import com.gionni2d.mvi.Action
import com.gionni2d.mvi.Reducer
import com.gionni2d.mviapp.domain.presentation.LoadingResult
import com.gionni2d.mviapp.domain.presentation.toLoadingResult

interface LoginReducers {
    val typeUsername: Reducer<LoginState, LoginIntent.TypeUsername>

    val typePassword: Reducer<LoginState, LoginIntent.TypePassword>

    val startLoading: Reducer<LoginState, Action>

    val updateAuthentication: Reducer<LoginState, AuthenticationResponse>
}

object LoginReducersImpl : LoginReducers {
    override val typeUsername = Reducer<LoginState, LoginIntent.TypeUsername> { s, i ->
        s.copy(username = i.value)
    }

    override val typePassword = Reducer<LoginState, LoginIntent.TypePassword> { s, i ->
        s.copy(password = i.value)
    }

    override val startLoading = Reducer<LoginState, Action> { s, _ ->
        s.copy(authStatus = LoadingResult.Loading)
    }

    override val updateAuthentication = Reducer<LoginState, AuthenticationResponse> { s, i ->
        s.copy(authStatus = i.value.toLoadingResult())
    }
}