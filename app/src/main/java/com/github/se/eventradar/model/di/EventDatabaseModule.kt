package com.github.se.eventradar.model.di

import com.github.se.eventradar.model.repository.event.FirebaseEventRepository
import com.github.se.eventradar.model.repository.event.IEventRepository
import com.github.se.eventradar.model.repository.event.MockEventRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier annotation class FirebaseEventModule

@Qualifier annotation class MockEventModule

@Module
@InstallIn(SingletonComponent::class)
class FirebaseEventDatabaseModule {
  @Provides
  @FirebaseEventModule
  @Singleton
  fun provideFirebaseEventRepository(): IEventRepository {
    return FirebaseEventRepository()
  }
}

@Module
@InstallIn(SingletonComponent::class)
class MockEventDatabaseModule {
  @Provides
  @MockEventModule
  @Singleton
  fun provideMockEventRepository(): IEventRepository {
    return MockEventRepository()
  }
}
