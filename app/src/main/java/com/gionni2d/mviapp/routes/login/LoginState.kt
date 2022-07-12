package com.gionni2d.mviapp.routes.login

import com.gionni2d.mvi.State
import com.gionni2d.mviapp.R
import com.gionni2d.mviapp.domain.AuthenticationStatus
import com.gionni2d.mviapp.domain.AuthenticationStatus.ANONYMOUS
import com.gionni2d.mviapp.domain.presentation.LoadingResult
import com.gionni2d.mviapp.domain.presentation.LoadingResult.Success
import com.gionni2d.mviapp.domain.presentation.compose.StringResource
import com.gionni2d.mviapp.domain.presentation.isLoading
import com.gionni2d.mviapp.domain.presentation.isSuccess


interface LoginViewState : State {
    val username: String
    val password: String
    val buttonText: StringResource
    val buttonLoading: Boolean
    val showFields: Boolean
    val isLogged: Boolean
}

data class LoginState(
    override val username: String = "",
    override val password: String = "",
    val authStatus: LoadingResult<AuthenticationStatus, Exception> = Success(ANONYMOUS)
) : State, LoginViewState {

    private val isLoading = authStatus.isLoading()

    override val buttonLoading: Boolean = isLoading

    override val buttonText: StringResource = when (authStatus) {
        is LoadingResult.Loading -> StringResource.Empty
        is LoadingResult.Failure -> StringResource.from("Error")
        is Success -> StringResource(
            if (authStatus.value == ANONYMOUS) R.string.login_cta_login
            else R.string.login_cta_logout
        )
    }

    override val isLogged: Boolean =
        authStatus.isSuccess() && authStatus.value == AuthenticationStatus.LOGGED

    override val showFields: Boolean = !isLogged && !isLoading
}
