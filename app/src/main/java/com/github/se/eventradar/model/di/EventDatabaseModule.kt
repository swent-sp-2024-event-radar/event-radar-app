package com.github.se.eventradar.model.di

import com.github.se.eventradar.model.repository.event.FirebaseEventRepository
import com.github.se.eventradar.model.repository.event.IEventRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FirebaseEventDatabaseModule {
  @Provides
  @Singleton
  fun provideFirebaseEventRepository(): IEventRepository {
    return FirebaseEventRepository()
  }
}
