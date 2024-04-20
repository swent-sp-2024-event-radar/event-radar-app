package com.github.se.eventradar.model.data

import com.github.se.eventradar.model.repository.event.FirebaseEventRepository
import com.github.se.eventradar.model.repository.event.IEventRepository
import com.github.se.eventradar.model.repository.event.MockEventRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class EventDatabaseModule {
  @Binds
  abstract fun bindFirebaseEventRepository(
      userRepository: FirebaseEventRepository
  ): IEventRepository

  @Binds abstract fun bindMockEventRepository(userRepository: MockEventRepository): IEventRepository
}
