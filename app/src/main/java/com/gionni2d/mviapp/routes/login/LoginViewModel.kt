package com.gionni2d.mviapp.routes.login

import android.icu.text.DecimalFormatSymbols
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gionni2d.mvi.*
import com.gionni2d.mviapp.data.UserRepository
import com.gionni2d.mviapp.domain.AuthenticationStatus
import com.gionni2d.mviapp.domain.presentation.isLoading
import com.gionni2d.mviapp.domain.success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class)
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val loginReducers: LoginReducers
) : ViewModel(), Model<LoginViewState, LoginIntent> {

    override fun subscribeTo(
        intents: Flow<LoginIntent>
    ): StateFlow<LoginViewState> {
        val store = store(LoginState())
        val state by store::currentState

        val typeUsernameFlow = intents
            .filterIsInstance<LoginIntent.TypeUsername>()
            .applyReducer(store, loginReducers.typeUsername)

        val typePasswordFlow = intents
            .filterIsInstance<LoginIntent.TypePassword>()
            .applyReducer(store, loginReducers.typePassword)

        val notLoadingIntentFlow = intents
            .filterNot { state.isLoading }

        val clickLoginButtonFlow = notLoadingIntentFlow
            .filterIsInstance<LoginIntent.Login>()
            .applyReducer(store, loginReducers.startLoading)
            .flatMapConcat { userRepository.login(state.username, state.password) }
            .map(::AuthenticationResponse)
            .applyReducer(store, loginReducers.updateAuthentication)

        val clickLogoutButtonFlow = notLoadingIntentFlow
            .filterIsInstance<LoginIntent.Logout>()
            .applyReducer(store, loginReducers.startLoading)
            .flatMapConcat { userRepository.logout() }
            .map { AuthenticationResponse(AuthenticationStatus.ANONYMOUS.success()) }
            .applyReducer(store, loginReducers.updateAuthentication)

        merge(
            typeUsernameFlow,
            typePasswordFlow,
            clickLoginButtonFlow,
            clickLogoutButtonFlow,
        ).launchIn(viewModelScope)

        return store.stateFlow
    }
}
