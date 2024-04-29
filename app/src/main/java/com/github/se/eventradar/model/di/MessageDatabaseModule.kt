package com.github.se.eventradar.model.di

import com.github.se.eventradar.model.repository.message.FirebaseMessageRepository
import com.github.se.eventradar.model.repository.message.IMessageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FirebaseMessageDatabaseModule {
  @Provides
  @Singleton
  fun provideFirebaseMessageRepository(): IMessageRepository {
    return FirebaseMessageRepository()
  }
}
