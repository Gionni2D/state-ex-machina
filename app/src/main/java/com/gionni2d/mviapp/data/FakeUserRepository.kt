package com.gionni2d.mviapp.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeUserRepository @Inject constructor() : UserRepository {

    private val users = mapOf(
        "mario.rossi" to "password1"
    )

    override fun login(username: String, password: String): Flow<Boolean> = flow {
        delay(1000)
        emit(users.containsKey(username) && users[username] == password)
    }
}