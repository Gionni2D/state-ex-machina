package com.gionni2d.mviapp.routes.login

import com.gionni2d.mvi.foundation.State
import com.gionni2d.mviapp.R
import com.gionni2d.mviapp.domain.AuthenticationStatus
import com.gionni2d.mviapp.domain.AuthenticationStatus.ANONYMOUS
import com.gionni2d.mviapp.domain.AuthenticationStatus.LOGGED
import com.gionni2d.mviapp.domain.presentation.LoadingResult
import com.gionni2d.mviapp.domain.presentation.LoadingResult.Success
import com.gionni2d.mviapp.domain.presentation.compose.StringResource
import com.gionni2d.mviapp.domain.presentation.isFailure
import com.gionni2d.mviapp.domain.presentation.isLoading
import com.gionni2d.mviapp.domain.presentation.isSuccess


interface LoginViewState : State {
    val username: String
    val password: String
    val buttonText: StringResource
    val buttonLoading: Boolean
    val buttonIntent: LoginIntent
    val showFields: Boolean
    val errorMessage: StringResource?
}

data class LoginState(
    override val username: String = "",
    override val password: String = "",
    val authStatus: LoadingResult<AuthenticationStatus, Throwable> = Success(ANONYMOUS)
) : State, LoginViewState {

    val isLoading = authStatus.isLoading()

    private val isLogged: Boolean =
        authStatus.isSuccess() && authStatus.value == LOGGED

    override val buttonLoading: Boolean = isLoading

    override val buttonText: StringResource = when (authStatus) {
        is LoadingResult.Loading -> StringResource.Empty
        is LoadingResult.Failure -> ANONYMOUS.buttonText
        is Success -> authStatus.value.buttonText
    }

    override val buttonIntent: LoginIntent =
        if (isLogged) LoginIntent.Logout
        else LoginIntent.Login

    override val showFields: Boolean = !isLogged && !isLoading

    override val errorMessage: StringResource? =
        if (!authStatus.isFailure()) null
        else StringResource.from("Error: ${authStatus.error::class.simpleName}")
}

private val AuthenticationStatus.buttonText: StringResource
    get() = StringResource(
        id = when (this) {
            ANONYMOUS -> R.string.login_cta_login
            LOGGED -> R.string.login_cta_logout
        }
    )
