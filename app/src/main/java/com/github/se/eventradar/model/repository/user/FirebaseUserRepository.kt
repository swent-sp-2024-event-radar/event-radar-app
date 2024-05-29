package com.github.se.eventradar.model.repository.user

import android.net.Uri
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

class FirebaseUserRepository(db: FirebaseFirestore = Firebase.firestore) : IUserRepository {
  private val userRef: CollectionReference = db.collection("users")

  private val birthdateString = "private/birthDate"
  private val emailString = "private/email"
  private val firstNameString = "private/firstName"
  private val lastNameString = "private/lastName"
  private val phoneNumberString = "private/phoneNumber"

  override suspend fun getUsers(): Resource<List<User>> {
    return try {
      val resultDocument = userRef.get().await()
      val users =
          resultDocument.documents.map { document ->
            val userMap = document.data!!
            val privateResult =
                userRef
                    .document(document.id)
                    .collection("private")
                    .document("private")
                    .get()
                    .await()
            userMap[birthdateString] = privateResult["birthDate"] as String
            userMap[emailString] = privateResult["email"] as String
            userMap[firstNameString] = privateResult["firstName"] as String
            userMap[lastNameString] = privateResult["lastName"] as String
            userMap[phoneNumberString] = privateResult["phoneNumber"] as String
            User(userMap, document.id)
          }
      Resource.Success(users)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun getUser(uid: String): Resource<User?> {
    return try {
      val resultDocument = userRef.document(uid).get().await()
      val userMap = resultDocument.data!!
      val privateResult =
          userRef
              .document(resultDocument.id)
              .collection("private")
              .document("private")
              .get()
              .await()
      userMap[birthdateString] = privateResult["birthDate"] as String
      userMap[emailString] = privateResult["email"] as String
      userMap[firstNameString] = privateResult["firstName"] as String
      userMap[lastNameString] = privateResult["lastName"] as String
      userMap[phoneNumberString] = privateResult["phoneNumber"] as String

      val user = User(userMap, resultDocument.id)
      Resource.Success(user)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun addUser(user: User): Resource<Unit> {
    val maps: Pair<Map<String, Any?>, Map<String, Any?>> = getMaps(user)

    return try {
      val docId = userRef.document().id
      userRef.document(docId).set(maps.first).await()
      userRef.document(docId).collection("private").document("private").set(maps.second).await()
      Resource.Success(Unit)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun addUser(map: Map<String, Any?>, documentId: String): Resource<Unit> {
    val user = User(map, documentId)
    val maps: Pair<Map<String, Any?>, Map<String, Any?>> = getMaps(user)

    return try {
      userRef.document(documentId).set(maps.first).await()
      userRef
          .document(documentId)
          .collection("private")
          .document("private")
          .set(maps.second)
          .await()
      Resource.Success(Unit)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun updateUser(user: User): Resource<Unit> {
    val maps: Pair<Map<String, Any?>, Map<String, Any?>> = getMaps(user)

    return try {
      userRef.document(user.userId).update(maps.first).await()
      userRef
          .document(user.userId)
          .collection("private")
          .document("private")
          .update(maps.second)
          .await()
      Resource.Success(Unit)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun deleteUser(user: User): Resource<Unit> {
    return try {
      userRef.document(user.userId).delete().await()
      Resource.Success(Unit)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun doesUserExist(userId: String): Resource<Unit> {
    return try {
      val user = userRef.document(userId).get().await()
      if (user.exists()) {
        Resource.Success(Unit)
      } else {
        Resource.Failure(Exception("User not found"))
      }
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun uploadImage(
      selectedImageUri: Uri,
      imageId: String,
      folderName: String
  ): Resource<Unit> {
    val storageRef = Firebase.storage.reference.child("$folderName/$imageId")
    return try {
      val result = storageRef.putFile(selectedImageUri).await()
      Resource.Success(Unit)
      if (result.task.isSuccessful) {
        Resource.Success(Unit)
      } else {
        val error = result.task.exception
        Resource.Failure(error ?: Exception("Upload failed without a specific error"))
      }
    } catch (e: Exception) {
      Resource.Failure(Exception("Error during upload image: ${e.localizedMessage}"))
    }
  }

  override suspend fun getImage(imageId: String, folderName: String): Resource<String> {

    val storageRef = Firebase.storage.reference.child("$folderName/$imageId")
    return try {
      val result = storageRef.downloadUrl.await()
      val url = result.toString()
      Resource.Success(url)
    } catch (e: Exception) {
      Resource.Failure(Exception("Error while getting image: ${e.localizedMessage}"))
    }
  }

  override suspend fun getCurrentUserId(): Resource<String> {
    val userId = Firebase.auth.currentUser?.uid
    return if (userId != null) {
      Resource.Success(userId)
    } else {
      Resource.Failure(Exception("No user currently signed in"))
    }
  }

  override suspend fun addEventToAttendeeList(
      userId: String,
      attendingEventId: String
  ): Resource<Unit> {
    return try {
      userRef.document(userId).update("eventsAttendeeList", FieldValue.arrayUnion(attendingEventId))
      Resource.Success(Unit)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun removeEventFromAttendeeList(
      userId: String,
      attendingEventId: String
  ): Resource<Unit> {
    return try {
      userRef
          .document(userId)
          .update("eventsAttendeeList", FieldValue.arrayRemove(attendingEventId))
      Resource.Success(Unit)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun uploadQRCode(
      data: ByteArray,
      userId: String
  ): Resource<Unit> { // upload this in firebase
    return try {
      // Create a reference to the file in Firebase Storage
      val storageRef = FirebaseStorage.getInstance().reference
      val qrCodesRef = storageRef.child("QR_Codes/$userId")
      // Upload the file to Firebase Storage
      val uploadTask = qrCodesRef.putBytes(data).await()
      // Get the download URL of the image
      if (uploadTask.task.isSuccessful) {
        Resource.Success(Unit) // Return the reference to the uploaded QR Code's path
      } else {
        val error = uploadTask.task.exception
        Resource.Failure(error ?: Exception("Upload QR Code failed without a specific error"))
      }
    } catch (e: Exception) {
      Resource.Failure(Exception("Error during QR code upload: ${e.localizedMessage}"))
    }
  }

  private fun getMaps(user: User): Pair<Map<String, Any?>, Map<String, Any?>> {
    val privateMap =
        mutableMapOf(
            "firstName" to user.firstName,
            "lastName" to user.lastName,
            "phoneNumber" to user.phoneNumber,
            "birthDate" to user.birthDate,
            "email" to user.email,
        )

    val publicMap =
        mutableMapOf(
            "profilePicUrl" to user.profilePicUrl,
            "qrCodeUrl" to user.qrCodeUrl,
            "username" to user.username,
            "accountStatus" to user.accountStatus,
            "eventsAttendeeList" to user.eventsAttendeeList,
            "eventsHostList" to user.eventsHostList,
            "bio" to user.bio,
            "friendsList" to user.friendsList,
        )

    return Pair(publicMap, privateMap)
  }
}
