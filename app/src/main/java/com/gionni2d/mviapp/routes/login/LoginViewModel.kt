package com.gionni2d.mviapp.routes.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gionni2d.mvi.Model
import com.gionni2d.mvi.applyReducer
import com.gionni2d.mvi.store
import com.gionni2d.mviapp.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class)
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel(), Model<LoginState, LoginIntent> {

    override fun subscribeTo(
        intents: Flow<LoginIntent>
    ): StateFlow<LoginState> {
        val store = store(LoginState())

        val typeUsernameFlow = intents
            .filterIsInstance<LoginIntent.TypeUsername>()
            .applyReducer(store) { s, i -> s.copy(username = i.value) }

        val typePasswordFlow = intents
            .filterIsInstance<LoginIntent.TypePassword>()
            .applyReducer(store) { s, i -> s.copy(password = i.value) }

        val clickLoginButtonFlow = intents
            .filterIsInstance<LoginIntent.ClickLoginButton>()
            .filterNot { store.value.loginButtonLoading }
            .applyReducer(store) { s, _ -> s.copy(loginButtonLoading = true) }
            .flatMapConcat { userRepository.login(store.value.username, store.value.password) }
            .map { loginResponse -> LoginIntent.LoginResponse(loginResponse) }
            .applyReducer(store) { s, _ -> s.copy(loginButtonLoading = false) }

        merge(
            typeUsernameFlow,
            typePasswordFlow,
            clickLoginButtonFlow,
        ).launchIn(viewModelScope)

        return store.stateFlow
    }
}
