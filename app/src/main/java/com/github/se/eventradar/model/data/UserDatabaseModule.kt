package com.github.se.eventradar.model.data

import com.github.se.eventradar.model.repository.user.FirebaseUserRepository
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UserDatabaseModule {
  @Binds
  abstract fun bindFirebaseUserRepository(userRepository: FirebaseUserRepository): IUserRepository

  @Binds abstract fun bindMockUserRepository(userRepository: MockUserRepository): IUserRepository
}
