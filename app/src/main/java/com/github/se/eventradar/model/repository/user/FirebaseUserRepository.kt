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
      val users = resultDocument.documents.map { document -> User(document.data!!, document.id) }
      Resource.Success(users)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun getUser(uid: String): Resource<User?> {
    val resultDocument = userRef.document(uid).get().await()

    return try {
      val user = User(resultDocument.data!!, resultDocument.id)
      Resource.Success(user)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun addUser(user: User): Resource<Unit> {

    return try {
      userRef.add(user).await()
      Resource.Success(Unit)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun updateUser(user: User): Resource<Unit> {
    val userMap = user.toMap()

    return try {
      userRef.document(user.userId).update(userMap as Map<String, Any>).await()
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
}
