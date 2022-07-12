package com.gionni2d.mviapp.di

import com.gionni2d.mviapp.data.FakeUserRepository
import com.gionni2d.mviapp.data.UserRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    fun provideUserRepository(impl: FakeUserRepository) : UserRepository = impl
}