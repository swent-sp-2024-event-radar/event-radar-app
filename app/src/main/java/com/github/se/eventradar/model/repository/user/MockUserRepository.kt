package com.github.se.eventradar.model.repository.user

import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User

class MockUserRepository : IUserRepository {
  private val mockUsers = mutableListOf<User>()
  var currentUserId: String? = null // Simulate current user ID

  override suspend fun getUsers(): Resource<List<User>> {
    return Resource.Success(mockUsers)
  }

  override suspend fun getUser(uid: String): Resource<User?> {
    val user = mockUsers.find { it.userId == uid }

    return if (user != null) {
      Resource.Success(user)
    } else {
      Resource.Failure(Exception("User with id $uid not found"))
    }
  }

  override suspend fun addUser(user: User): Resource<Unit> {
    mockUsers.add(user)
    return Resource.Success(Unit)
  }

  override suspend fun addUser(map: Map<String, Any?>, documentId: String): Resource<Unit> {
    val user = User(map, documentId)
    return addUser(user)
  }

  override suspend fun updateUser(user: User): Resource<Unit> {
    val index = mockUsers.indexOfFirst { it.userId == user.userId }

    return if (index != -1) {
      mockUsers[index] = user
      Resource.Success(Unit)
    } else {
      Resource.Failure(Exception("User with id ${user.userId} not found"))
    }
  }

  override suspend fun deleteUser(user: User): Resource<Unit> {
    return if (mockUsers.remove(user)) {
      Resource.Success(Unit)
    } else {
      Resource.Failure(Exception("User with id ${user.userId} not found"))
    }
  }

  override suspend fun doesUserExist(userId: String): Resource<Unit> {
    return if (mockUsers.none { userId == it.userId })
        Resource.Failure(Exception("User not logged in"))
    else Resource.Success(Unit)
  }

  override suspend fun getCurrentUserId(): Resource<String> {
    return if (currentUserId != null) {
      Resource.Success(currentUserId!!)
    } else {
      Resource.Failure(Exception("No user currently signed in"))
    }
  }
}
