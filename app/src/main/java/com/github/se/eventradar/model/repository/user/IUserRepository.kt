package com.github.se.eventradar.model.repository.user

import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User

interface IUserRepository {
  suspend fun getUsers(): Resource<List<User>>

  suspend fun getUser(uid: String): Resource<User?>

  suspend fun addUser(user: User): Resource<Unit>

  suspend fun updateUser(user: User): Resource<Unit>

  suspend fun deleteUser(user: User): Resource<Unit>
}
