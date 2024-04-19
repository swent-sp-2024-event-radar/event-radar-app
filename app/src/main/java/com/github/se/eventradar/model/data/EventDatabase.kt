package com.github.se.eventradar.model.data

import com.github.se.eventradar.model.repository.EventRepository
import com.github.se.eventradar.model.repository.FirebaseEventRepository
import com.github.se.eventradar.model.repository.MockEventRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class EventDatabase {
  @Binds
  abstract fun bindFirebaseEventRepository(userRepository: FirebaseEventRepository): EventRepository

  @Binds abstract fun bindMockUserRepository(userRepository: MockEventRepository): EventRepository
}
