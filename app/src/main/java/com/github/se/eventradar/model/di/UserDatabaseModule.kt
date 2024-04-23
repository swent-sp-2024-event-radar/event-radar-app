package com.github.se.eventradar.model.di

import com.github.se.eventradar.model.repository.user.FirebaseUserRepository
import com.github.se.eventradar.model.repository.user.IUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FirebaseUserDatabaseModule {
  @Provides
  @Singleton
  fun provideFirebaseUserRepository(): IUserRepository {
    return FirebaseUserRepository()
  }
}
