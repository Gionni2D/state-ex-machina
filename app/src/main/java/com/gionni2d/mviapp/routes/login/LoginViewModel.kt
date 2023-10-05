package com.gionni2d.mviapp.routes.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gionni2d.mvi.*
import com.gionni2d.mvi.extension.applyReducer
import com.gionni2d.mvi.foundation.Model
import com.gionni2d.mvi.foundation.store
import com.gionni2d.mviapp.data.UserRepository
import com.gionni2d.mviapp.domain.AuthenticationStatus
import com.gionni2d.mviapp.domain.success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class)
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val loginReducerFactory: LoginReducerFactory
) : ViewModel(), Model<LoginViewState, LoginIntent> {

    override fun subscribeTo(
        intents: Flow<LoginIntent>
    ): StateFlow<LoginViewState> {
        val store = store(LoginState())
        val state by store::currentState

        val typeUsernameFlow = intents
            .filterIsInstance<LoginIntent.TypeUsername>()
            .applyReducer(store, loginReducerFactory::typeUsername)

        val typePasswordFlow = intents
            .filterIsInstance<LoginIntent.TypePassword>()
            .applyReducer(store, loginReducerFactory::typePassword)

        val notLoadingIntentFlow = intents
            .filterNot { state.isLoading }

        val clickLoginButtonFlow = notLoadingIntentFlow
            .filterIsInstance<LoginIntent.Login>()
            .applyReducer(store, loginReducerFactory.startLoading)
            .flatMapConcat { userRepository.login(state.username, state.password) }
            .applyReducer(store, loginReducerFactory::updateAuthentication)

        val clickLogoutButtonFlow = notLoadingIntentFlow
            .filterIsInstance<LoginIntent.Logout>()
            .applyReducer(store, loginReducerFactory.startLoading)
            .flatMapConcat { userRepository.logout() }
            .map { AuthenticationStatus.ANONYMOUS.success() }
            .applyReducer(store, loginReducerFactory::updateAuthentication)

        merge(
            typeUsernameFlow,
            typePasswordFlow,
            clickLoginButtonFlow,
            clickLogoutButtonFlow,
        ).launchIn(viewModelScope)

        return store.stateFlow
    }
}
