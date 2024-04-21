package com.github.se.eventradar.model.di

import com.github.se.eventradar.model.repository.user.FirebaseUserRepository
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier annotation class FirebaseUserModule

@Qualifier annotation class MockUserModule

@Module
@InstallIn(SingletonComponent::class)
class FirebaseUserDatabaseModule {
  @Provides
  @FirebaseUserModule
  @Singleton
  fun provideFirebaseUserRepository(): IUserRepository {
    return FirebaseUserRepository()
  }
}

@Module
@InstallIn(SingletonComponent::class)
class MockUserDatabaseModule {
  @Provides
  @MockUserModule
  @Singleton
  fun provideMockUserRepository(): IUserRepository {
    return MockUserRepository()
  }
}
