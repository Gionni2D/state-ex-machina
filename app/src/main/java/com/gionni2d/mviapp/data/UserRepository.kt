package com.gionni2d.mviapp.data

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun login(
        username: String,
        password: String
    ) : Flow<Boolean>
}