package com.github.se.eventradar.model.repository.message

import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.message.Message
import com.github.se.eventradar.model.message.MessageHistory
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseMessageRepository : IMessageRepository {
  private val db: FirebaseFirestore = Firebase.firestore
  private val messageRef: CollectionReference = db.collection("messages")

  override suspend fun getMessages(user1: String, user2: String): Resource<MessageHistory> {
    // From and to user are interchangeable
    val resultDocument =
        messageRef
            .where(
                Filter.or(
                    Filter.and(
                        Filter.arrayContains("from_user", user1),
                        Filter.arrayContains("to_user", user2)),
                    Filter.and(
                        Filter.arrayContains("from_user", user2),
                        Filter.arrayContains("to_user", user1)),
                ))
            .limit(1)
            .get()
            .await()

    return try {
      val result = resultDocument.documents[0]
      val messageHistory = MessageHistory(result.data!!, result.id)
      Resource.Success(messageHistory)
    } catch (e: Exception) {
      createNewMessageHistory(user1, user2)
    }
  }

  override suspend fun addMessage(
      message: Message,
      messageHistory: MessageHistory
  ): Resource<Unit> {
    return try {
      val newMessage =
          messageRef.document(messageHistory.id).collection("messages").add(message.toMap()).await()
      messageRef.document(messageHistory.id).update("latest_message_id", newMessage.id).await()
      Resource.Success(Unit)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun updateMessageToReadState(
      message: Message,
      messageHistory: MessageHistory
  ): Resource<Unit> {
    return try {
      messageRef
          .document(messageHistory.id)
          .collection("messages")
          .document(message.id)
          .update(mapOf("message_read" to message.isRead))
          .await()
      Resource.Success(Unit)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun createNewMessageHistory(
      user1: String,
      user2: String
  ): Resource<MessageHistory> {
    val messageHistory =
        MessageHistory(
            user1 = user1,
            user2 = user2,
            latestMessageId = "",
            messages = mutableListOf(),
        )
    return try {
      messageRef.add(messageHistory.toMap()).await()
      Resource.Success(messageHistory)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }
}
