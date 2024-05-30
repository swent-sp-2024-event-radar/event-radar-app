package com.github.se.eventradar.model.repository.user

import android.net.Uri
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User

class MockUserRepository : IUserRepository {
  private val mockUsers = mutableListOf<User>()
  /*
  Map of FolderNames, to their corresponding photoNames
   */
  // associate folderName to
  private val mockImagesDatabase =
      mutableMapOf(
          "QR_Codes" to mutableListOf<String>(),
          "Profile_Pictures" to mutableListOf<String>(),
          "Event_Pictures" to mutableListOf<String>())

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
    return Resource.Success(Unit)
  }

  override suspend fun addUser(map: Map<String, Any?>, documentId: String): Resource<Unit> {
    val user = User(map, documentId)
    return addUser(user)
  }

  override suspend fun updateUser(user: User): Resource<Unit> {
    val index = mockUsers.indexOfFirst { it.userId == user.userId }

    return if (index != -1) {
      // Update the user in mockUsers
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

  override suspend fun uploadImage(
      selectedImageUri: Uri,
      imageId: String,
      folderName: String
  ): Resource<Unit> {
    return if (currentUserId == null ||
        mockUsers.indexOfFirst { it.userId == currentUserId } == -1) {
      Resource.Failure(Exception("No user has been found"))
    } else if (folderName != "QR_Codes" &&
        folderName != "Profile_Pictures" &&
        folderName != "Event_Pictures") {
      Resource.Failure(Exception("Folder $folderName does not exist"))
    } else {
      mockImagesDatabase.getValue(folderName).add(imageId)
      Resource.Success(Unit)
    }
  }

  override suspend fun getImage(imageId: String, folderName: String): Resource<String> {
    return if (currentUserId == null ||
        mockUsers.indexOfFirst { it.userId == currentUserId } == -1) {
      Resource.Failure(Exception("No user has been found"))
    } else if (folderName != "QR_Codes" &&
        folderName != "Profile_Pictures" &&
        folderName != "Event_Pictures") {
      Resource.Failure(Exception("Folder $folderName does not exist"))
    } else {
      val databaseFolderList =
          mockImagesDatabase.getValue(folderName).filter { id -> id == imageId }
      if (databaseFolderList.isEmpty() || databaseFolderList[0] != imageId) {
        Resource.Failure(
            Exception("Image from folder $folderName not found for user $currentUserId"))
      } else {
        val databaseImageId = databaseFolderList[0]
        val imageUrl = "http://example.com/$folderName/$databaseImageId"
        Resource.Success(imageUrl)
      }
    }
  }

  override suspend fun uploadQRCode(data: ByteArray, userId: String): Resource<Unit> {
    val index = mockUsers.indexOfFirst { it.userId == userId }
    return if (index != -1) {
      mockImagesDatabase.getValue("QR_Codes").add(userId)
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

  override suspend fun addEventToAttendeeList(
      userId: String,
      attendingEventId: String
  ): Resource<Unit> {
    return when (val res = getUser(userId)) {
      is Resource.Success -> {
        res.data?.eventsAttendeeList?.add(attendingEventId)
        Resource.Success(Unit)
      }
      is Resource.Failure -> {
        Resource.Failure(res.throwable)
      }
    }
  }

  override suspend fun removeEventFromAttendeeList(
      userId: String,
      attendingEventId: String
  ): Resource<Unit> {
    return when (val res = getUser(userId)) {
      is Resource.Success -> {
        res.data?.eventsAttendeeList?.remove(attendingEventId)
        Resource.Success(Unit)
      }
      is Resource.Failure -> {
        Resource.Failure(res.throwable)
      }
    }
  }

  override suspend fun updateUserField(userId: String, field: String, value: Any): Resource<Unit> {
    val index = mockUsers.indexOfFirst { it.userId == userId }
    return if (index != -1) {
      val user = mockUsers[index]
      val updatedUser =
          when (field) {
            "username" -> user.copy(username = value as? String ?: user.username)
            "firstName" -> user.copy(firstName = value as? String ?: user.firstName)
            "lastName" -> user.copy(lastName = value as? String ?: user.lastName)
            "phoneNumber" -> user.copy(phoneNumber = value as? String ?: user.phoneNumber)
            "birthDate" -> user.copy(birthDate = value as? String ?: user.birthDate)
            "accountStatus" -> user.copy(accountStatus = value as? String ?: user.accountStatus)
            "eventsAttendeeList" ->
                user.copy(
                    eventsAttendeeList = value as? MutableList<String> ?: user.eventsAttendeeList)
            "eventsHostList" ->
                user.copy(eventsHostList = value as? MutableList<String> ?: user.eventsHostList)
            "friendsList" ->
                user.copy(friendsList = value as? MutableList<String> ?: user.friendsList)
            "profilePicUrl" -> user.copy(profilePicUrl = value as? String ?: user.profilePicUrl)
            "qrCodeUrl" -> user.copy(qrCodeUrl = value as? String ?: user.qrCodeUrl)
            else -> user
          }
      updateUser(updatedUser)
    } else {
      Resource.Failure(Exception("User with id $userId not found"))
    }
  }

    override suspend fun getUserField(userId: String, field: String): Resource<Any> {
        val index = mockUsers.indexOfFirst { it.userId == userId }
        return if (index != -1) {
        val user = mockUsers[index]
        val value =
            when (field) {
                "username" -> user.username
                "firstName" -> user.firstName
                "lastName" -> user.lastName
                "phoneNumber" -> user.phoneNumber
                "birthDate" -> user.birthDate
                "accountStatus" -> user.accountStatus
                "eventsAttendeeList" -> user.eventsAttendeeList
                "eventsHostList" -> user.eventsHostList
                "friendsList" -> user.friendsList
                "profilePicUrl" -> user.profilePicUrl
                "qrCodeUrl" -> user.qrCodeUrl
                else -> null
            }
        if (value != null) {
            Resource.Success(value)
        } else {
            Resource.Failure(Exception("Field $field not found for user $userId"))
        }
        } else {
        Resource.Failure(Exception("User with id $userId not found"))
        }
    }

  // Helper method to set the current user ID for testing
  fun updateCurrentUserId(userId: String?) {
    currentUserId = userId
  }
}
