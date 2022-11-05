package com.gionni2d.mviapp.di

import com.gionni2d.mviapp.data.FakeUserRepository
import com.gionni2d.mviapp.data.UserRepository
import com.gionni2d.mviapp.routes.login.LoginReducers
import com.gionni2d.mviapp.routes.login.LoginReducersImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    fun provideUserRepository(impl: FakeUserRepository): UserRepository = impl

    @Provides
    fun provideLoginReducers(): LoginReducers = LoginReducersImpl
}