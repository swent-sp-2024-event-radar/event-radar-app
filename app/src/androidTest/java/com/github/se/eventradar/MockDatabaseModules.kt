package com.github.se.eventradar

import com.github.se.eventradar.model.di.FirebaseEventDatabaseModule
import com.github.se.eventradar.model.di.FirebaseUserDatabaseModule
import com.github.se.eventradar.model.repository.event.IEventRepository
import com.github.se.eventradar.model.repository.event.MockEventRepository
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class], replaces = [FirebaseEventDatabaseModule::class])
class MockEventDatabaseModule {
  @Provides
  @Singleton
  fun provideMockEventRepository(): IEventRepository {
    return MockEventRepository()
  }
}

@Module
@TestInstallIn(
    components = [SingletonComponent::class], replaces = [FirebaseUserDatabaseModule::class])
class MockUserDatabaseModule {
  @Provides
  @Singleton
  fun provideMockUserRepository(): IUserRepository {
    return MockUserRepository()
  }
}
