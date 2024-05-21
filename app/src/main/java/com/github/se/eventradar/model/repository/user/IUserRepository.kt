package com.github.se.eventradar.model.repository.user

import android.net.Uri
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User

interface IUserRepository {

  suspend fun getUsers(): Resource<List<User>>

  suspend fun getUser(uid: String): Resource<User?>

  suspend fun addUser(user: User): Resource<Unit>

  // add user using a map
  suspend fun addUser(map: Map<String, Any?>, documentId: String): Resource<Unit>

  suspend fun updateUser(user: User): Resource<Unit>

  suspend fun deleteUser(user: User): Resource<Unit>

  suspend fun doesUserExist(userId: String): Resource<Unit>

  suspend fun uploadImage(selectedImageUri: Uri, imageId: String, folderName: String): Resource<Unit>

  suspend fun getImage(uid: String, folderName: String): Resource<String>

  suspend fun uploadQRCode(data: ByteArray, userId: String): Resource<Unit>

  suspend fun getCurrentUserId(): Resource<String>
}
