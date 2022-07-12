package com.gionni2d.mviapp.data

import com.gionni2d.mviapp.domain.AuthenticationStatus
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
    ): Flow<AuthenticationStatus> = flow {
        delay(1000)
        emit(
            if (users[username] == password) AuthenticationStatus.LOGGED
            else AuthenticationStatus.ANONYMOUS
        )
    }

    override fun logout(): Flow<Unit> = flow {
        delay(2000)
        emit(Unit)
    }
}