package com.github.se.eventradar.model.data

import com.github.se.eventradar.model.repository.FirebaseUserRepository
import com.github.se.eventradar.model.repository.MockUserRepository
import com.github.se.eventradar.model.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UserDatabase {
  @Binds
  abstract fun bindFirebaseUserRepository(userRepository: FirebaseUserRepository): UserRepository

  @Binds abstract fun bindMockUserRepository(userRepository: MockUserRepository): UserRepository
}
