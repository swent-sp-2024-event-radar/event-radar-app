package com.github.se.eventradar.model.repository.user

import android.net.Uri
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User

class MockUserRepository : IUserRepository {
  private val mockUsers = mutableListOf<User>()
  /*
  Map that associates a user to:
   String 1. profilePictureLink
   String 2. qrCodePictureLink
   */
  // associate folderName to
  private val mockImagesDatabase = mutableMapOf<User, MutableMap<String, String>>()
  private var currentUserId: String? = null // Simulate current user ID

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
    mockImagesDatabase[user] =
        mutableMapOf(
            "Profile_Pictures" to user.profilePicUrl,
            "QR_Codes" to user.qrCodeUrl) // Add user to Database too
    return Resource.Success(Unit)
  }

  override suspend fun addUser(map: Map<String, Any?>, documentId: String): Resource<Unit> {
    val user = User(map, documentId)
    return addUser(user)
  }

  override suspend fun updateUser(user: User): Resource<Unit> {
    val index = mockUsers.indexOfFirst { it.userId == user.userId }

    return if (index != -1) {
      // Update the user in mockImagesDatabase
      val oldUser = mockUsers[index]
      mockImagesDatabase.remove(oldUser)
      mockImagesDatabase[user] =
          mutableMapOf("Profile_Pictures" to user.profilePicUrl, "QR_Codes" to user.qrCodeUrl)
      // Update the user in mockUsers
      mockUsers[index] = user
      Resource.Success(Unit)
    } else {
      Resource.Failure(Exception("User with id ${user.userId} not found"))
    }
  }

  override suspend fun deleteUser(user: User): Resource<Unit> {
    return if (mockUsers.remove(user)) {
      mockImagesDatabase.remove(user) // Remove from mockImagesDatabase
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

  override suspend fun uploadImage(
    selectedImageUri: Uri,
    imageId: String,
    folderName: String
  ): Resource<Unit> {
    println("Uploading Image")
    return if (currentUserId == null) {
      Resource.Failure(Exception("User with id $imageId not found"))
    } else if (folderName != "QR_Codes" && folderName != "Profile_Pictures" && folderName != "Event_Pictures") {
      Resource.Failure(Exception("Folder $folderName does not exist"))
    } else {
      val user = mockImagesDatabase.keys.filter { user -> user.userId == currentUserId }[0]
      mockImagesDatabase[user]?.replace(folderName, "http://example.com/$folderName/$imageId")
      Resource.Success(Unit)
    }
  }

  override suspend fun getImage(uid: String, folderName: String): Resource<String> {
    val userList = mockImagesDatabase.keys.filter { user -> user.userId == uid }
    return if (userList.isEmpty()) {
      Resource.Failure(Exception("User with id $uid not found"))
    } else if (folderName != "QR_Codes" && folderName != "Profile_Pictures" && folderName != "Event_Pictures") {
      Resource.Failure(Exception("Folder $folderName does not exist"))
    } else {
      val user = userList[0]
      val folderLinks = mockImagesDatabase[user]
      val imageUrl = folderLinks?.get(folderName)
      if (imageUrl != null && imageUrl != "") {
        Resource.Success(imageUrl)
      } else {
        Resource.Failure(Exception("Image from folder $folderName not found for user $uid"))
      }
    }
  }

  override suspend fun uploadQRCode(data: ByteArray, userId: String): Resource<Unit> {
    val index = mockUsers.indexOfFirst { it.userId == userId }
    return if (index != -1) {
      val userList = mockImagesDatabase.keys.filter { user -> user.userId == userId }
      val user = userList[0]
      mockImagesDatabase[user]?.replace("QR_Codes", "http://example.com/QR_Codes/$userId")
      Resource.Success(Unit)
    } else {
      Resource.Failure(Exception("User with id $userId not found"))
    }
  }

  override suspend fun getCurrentUserId(): Resource<String> {
    return if (currentUserId != null) {
      Resource.Success(currentUserId!!)
    } else {
      Resource.Failure(Exception("No user currently signed in"))
    }
  }

  // Helper method to set the current user ID for testing
  fun updateCurrentUserId(userId: String?) {
    currentUserId = userId
  }
}
