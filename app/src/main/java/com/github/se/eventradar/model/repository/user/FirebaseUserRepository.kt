package com.github.se.eventradar.model.repository.user

import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseUserRepository : IUserRepository {
  private val db: FirebaseFirestore = Firebase.firestore
  private val userRef: CollectionReference = db.collection("users")

  override suspend fun getUsers(): Resource<List<User>> {
    val resultDocument = userRef.get().await()

    return try {
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
            userMap["private/birthDate"] = privateResult["birthDate"] as String
            userMap["private/email"] = privateResult["email"] as String
            userMap["private/firstName"] = privateResult["firstName"] as String
            userMap["private/lastName"] = privateResult["lastName"] as String
            userMap["private/phoneNumber"] = privateResult["phoneNumber"] as String
            User(userMap, document.id)
          }
      Resource.Success(users)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun getUser(uid: String): Resource<User?> {
    val resultDocument = userRef.document(uid).get().await()

    return try {
      val userMap = resultDocument.data!!
      val privateResult =
          userRef
              .document(resultDocument.id)
              .collection("private")
              .document("private")
              .get()
              .await()
      userMap["private/birthDate"] = privateResult["birthDate"] as String
      userMap["private/email"] = privateResult["email"] as String
      userMap["private/firstName"] = privateResult["firstName"] as String
      userMap["private/lastName"] = privateResult["lastName"] as String
      userMap["private/phoneNumber"] = privateResult["phoneNumber"] as String
      val user = User(userMap, resultDocument.id)
      Resource.Success(user)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun addUser(user: User): Resource<Unit> {
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
        )

    return try {
      val docId = userRef.document().id
      userRef.document(docId).set(publicMap).await()
      userRef.document(docId).collection("private").document("private").set(privateMap).await()
      Resource.Success(Unit)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun addUser(map: Map<String, Any?>, documentId: String): Resource<Unit> {
    val privateMap =
        mutableMapOf(
            "firstName" to map["private/firstName"],
            "lastName" to map["private/lastName"],
            "phoneNumber" to map["private/phoneNumber"],
            "birthDate" to map["private/birthDate"],
            "email" to map["private/email"],
        )

    val publicMap =
        mutableMapOf(
            "profilePicUrl" to map["profilePicUrl"],
            "qrCodeUrl" to map["qrCodeUrl"],
            "username" to map["username"],
            "accountStatus" to map["accountStatus"],
            "eventsAttendeeList" to map["eventsAttendeeList"],
            "eventsHostList" to map["eventsHostList"],
        )

    return try {
      userRef.document(documentId).set(publicMap).await()
      userRef.document(documentId).collection("private").document("private").set(privateMap).await()
      Resource.Success(Unit)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun updateUser(user: User): Resource<Unit> {
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
        )

    return try {
      userRef.document(user.userId).update(publicMap as Map<String, Any>).await()
      userRef
          .document(user.userId)
          .collection("private")
          .document("private")
          .update(privateMap as Map<String, Any>)
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

  override suspend fun isUserLoggedIn(userId: String): Resource<Unit> {
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
}
