package com.gionni2d.mviapp.data

import com.gionni2d.mviapp.domain.AuthenticationStatus
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun login(
        username: String,
        password: String
    ) : Flow<AuthenticationStatus>

    fun logout() : Flow<Unit>
}