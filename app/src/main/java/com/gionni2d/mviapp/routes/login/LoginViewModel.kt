package com.gionni2d.mviapp.routes.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gionni2d.mvi.*
import com.gionni2d.mviapp.data.UserRepository
import com.gionni2d.mviapp.domain.AuthenticationStatus
import com.gionni2d.mviapp.domain.presentation.LoadingResult.Loading
import com.gionni2d.mviapp.domain.presentation.LoadingResult.Success
import com.gionni2d.mviapp.domain.presentation.isLoading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class)
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel(), Model<LoginViewState, LoginIntent> {

    override fun subscribeTo(
        intents: Flow<LoginIntent>
    ): StateFlow<LoginViewState> {
        val store = store(LoginState())
        val state by store::currentState

        val typeUsernameFlow = intents
            .filterIsInstance<LoginIntent.TypeUsername>()
            .applyReducer(store) { s, i -> s.copy(username = i.value) }

        val typePasswordFlow = intents
            .filterIsInstance<LoginIntent.TypePassword>()
            .applyReducer(store) { s, i -> s.copy(password = i.value) }

        val notLoadingIntentFlow = intents
            .filterNot { state.authStatus.isLoading() }

        val clickLoginButtonFlow = notLoadingIntentFlow
            .filterIsInstance<LoginIntent.Login>()
            .applyReducer(store, LoginReducer.StartLoading)
            .flatMapConcat { userRepository.login(state.username, state.password) }
            .map(::AuthenticationResponse)
            .applyReducer(store, LoginReducer.UpdateAuthentication)

        val clickLogoutButtonFlow = notLoadingIntentFlow
            .filterIsInstance<LoginIntent.Logout>()
            .applyReducer(store, LoginReducer.StartLoading)
            .flatMapConcat { userRepository.logout() }
            .map { AuthenticationResponse(AuthenticationStatus.ANONYMOUS) }
            .applyReducer(store, LoginReducer.UpdateAuthentication)

        merge(
            typeUsernameFlow,
            typePasswordFlow,
            clickLoginButtonFlow,
            clickLogoutButtonFlow,
        ).launchIn(viewModelScope)

        return store.stateFlow
    }
}

object LoginReducer {
    val StartLoading = Reducer<LoginState, Action> { s, _ ->
        s.copy(authStatus = Loading)
    }

    val UpdateAuthentication = Reducer<LoginState, AuthenticationResponse> { s, i ->
        s.copy(authStatus = Success(i.value))
    }
}