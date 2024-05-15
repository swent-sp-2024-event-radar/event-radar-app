package com.github.se.eventradar.di

import com.github.se.eventradar.model.di.FirebaseEventDatabaseModule
import com.github.se.eventradar.model.di.FirebaseMessageDatabaseModule
import com.github.se.eventradar.model.di.FirebaseUserDatabaseModule
import com.github.se.eventradar.model.repository.event.IEventRepository
import com.github.se.eventradar.model.repository.event.MockEventRepository
import com.github.se.eventradar.model.repository.message.IMessageRepository
import com.github.se.eventradar.model.repository.message.MockMessageRepository
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
  fun provideMockEventRepository(): IEventRepository {
    return MockEventRepository()
  }
}

@Module
@TestInstallIn(
    components = [SingletonComponent::class], replaces = [FirebaseUserDatabaseModule::class])
class MockUserDatabaseModule {
  @Provides
  fun provideMockUserRepository(): IUserRepository {
    return MockUserRepository()
  }
}

@Module
@TestInstallIn(
    components = [SingletonComponent::class], replaces = [FirebaseMessageDatabaseModule::class])
class MockMessageDatabaseModule {
  @Provides
  fun provideMockMessageRepository(): IMessageRepository {
    return MockMessageRepository()
  }
}
