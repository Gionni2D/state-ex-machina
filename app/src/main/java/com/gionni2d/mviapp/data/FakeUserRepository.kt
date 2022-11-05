package com.gionni2d.mviapp.data

import com.gionni2d.mviapp.domain.AuthenticationStatus
import com.gionni2d.mviapp.domain.ResultFlow
import com.gionni2d.mviapp.domain.failure
import com.gionni2d.mviapp.domain.success
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeUserRepository @Inject constructor() : UserRepository {

    private val users = mapOf(
        "mario.rossi" to "password1"
    )

    override fun login(
        username: String,
        password: String
    ): ResultFlow<AuthenticationStatus> = flow {
        delay(1000)
        val result: Result<AuthenticationStatus> = when {
            username.isEmpty() || password.isEmpty() -> IllegalArgumentException().failure()
            users[username] == password -> AuthenticationStatus.LOGGED.success()
            else -> AuthenticationStatus.ANONYMOUS.success()
        }
        emit(result)
    }

    override fun logout(): ResultFlow<Unit> = flow {
        delay(2000)
        emit(Unit.success())
    }
}