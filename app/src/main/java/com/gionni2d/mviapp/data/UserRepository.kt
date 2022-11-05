package com.gionni2d.mviapp.data

import com.gionni2d.mviapp.domain.AuthenticationStatus
import com.gionni2d.mviapp.domain.ResultFlow
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun login(
        username: String,
        password: String
    ) : ResultFlow<AuthenticationStatus>

    fun logout() : ResultFlow<Unit>
}