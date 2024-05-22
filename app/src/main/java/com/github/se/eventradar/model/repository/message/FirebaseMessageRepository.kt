package com.github.se.eventradar.model.repository.message

import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.message.Message
import com.github.se.eventradar.model.message.MessageHistory
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseMessageRepository(db: FirebaseFirestore = Firebase.firestore) : IMessageRepository {
  private val messageRef: CollectionReference = db.collection("messages")

  override suspend fun getMessages(uid: String): Resource<List<MessageHistory>> {
    return try {
      val resultDocument =
          messageRef
              .where(Filter.or(Filter.equalTo("from_user", uid), Filter.equalTo("to_user", uid)))
              .get()
              .await()
      if (resultDocument == null || resultDocument.isEmpty) {
        return Resource.Success(emptyList())
      }
      val messageHistories =
          resultDocument.documents.map {
            val messageHistoryMap = it.data!!

            val messages = messageRef.document(it.id).collection("messages_list").get().await()

            messageHistoryMap["messages"] =
                messages.documents.map { message ->
                  Message(
                      sender = message["sender"] as String,
                      content = message["content"] as String,
                      dateTimeSent = LocalDateTime.parse(message["date_time_sent"] as String),
                      id = message.id,
                  )
                }

            MessageHistory(messageHistoryMap, it.id)
          }

      Resource.Success(messageHistories)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun getMessages(user1: String, user2: String): Resource<MessageHistory> {
    // From and to user are interchangeable
    return try {
      val resultDocument =
          messageRef
              .where(
                  Filter.or(
                      Filter.and(
                          Filter.equalTo("from_user", user1), Filter.equalTo("to_user", user2)),
                      Filter.and(
                          Filter.equalTo("from_user", user2), Filter.equalTo("to_user", user1)),
                  ))
              .limit(1)
              .get()
              .await()

      if (resultDocument == null || resultDocument.isEmpty) {
        return Resource.Failure(Exception("No message history found between users"))
      }

      val result = resultDocument.documents[0]
      val messageHistoryMap = result.data!!

      val messages = messageRef.document(result.id).collection("messages_list").get().await()

      messageHistoryMap["messages"] =
          messages.documents.map { message ->
            Message(
                sender = message["sender"] as String,
                content = message["content"] as String,
                dateTimeSent = LocalDateTime.parse(message["date_time_sent"] as String),
                id = message.id,
            )
          }

      val messageHistory = MessageHistory(messageHistoryMap, result.id)
      Resource.Success(messageHistory)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun addMessage(
      message: Message,
      messageHistory: MessageHistory
  ): Resource<Unit> {
    return try {
      // Check if the message history exists based on the `messages` field
      // If messages field is empty, then message history doesn't exist in Firestore
      val messageHistoryId =
          if (messageHistory.messages.isEmpty()) {
            val newHistoryResource =
                createNewMessageHistory(messageHistory.user1, messageHistory.user2)
            if (newHistoryResource is Resource.Failure) {
              return Resource.Failure(newHistoryResource.throwable)
            }

            val newHistory = (newHistoryResource as Resource.Success).data
            newHistory.id
          } else {
            // Use the existing message history ID
            messageHistory.id
          }

      val newMessage =
          messageRef
              .document(messageHistoryId)
              .collection("messages_list")
              .add(message.toMap())
              .await()

      val updatedValues =
          mapOf(
              "latest_message_id" to newMessage.id,
              "from_user_read" to (message.sender == messageHistory.user1),
              "to_user_read" to (message.sender == messageHistory.user2),
          )

      messageRef.document(messageHistoryId).update(updatedValues).await()
      Resource.Success(Unit)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun updateReadStateForUser(
      userId: String,
      messageHistory: MessageHistory
  ): Resource<Unit> {
    val updatedValue =
        mapOf(
            if (userId == messageHistory.user1) "from_user_read" to true
            else "to_user_read" to true)

    return try {
      messageRef.document(messageHistory.id).update(updatedValue).await()
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
            user1ReadMostRecentMessage = false,
            user2ReadMostRecentMessage = false,
            messages = mutableListOf(),
        )
    return try {
      // Add the MessageHistory to Firestore and get the generated DocumentReference
      val documentReference = messageRef.add(messageHistory.toMap()).await()

      // Update the messageHistory object with the generated ID
      val updatedMessageHistory = messageHistory.copy(id = documentReference.id)

      Resource.Success(updatedMessageHistory)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override fun observeMessages(user1: String, user2: String): Flow<Resource<MessageHistory>> =
      callbackFlow {
        val query =
            messageRef
                .where(
                    Filter.or(
                        Filter.and(
                            Filter.equalTo("from_user", user1), Filter.equalTo("to_user", user2)),
                        Filter.and(
                            Filter.equalTo("from_user", user2), Filter.equalTo("to_user", user1)),
                    ))
                .limit(1)

        val listener =
            query.addSnapshotListener { snapshot, error ->
              if (error != null) {
                trySend(
                    Resource.Failure(
                        Exception("Error listening to message updates: ${error.message}")))
                return@addSnapshotListener
              }

              if (snapshot != null && !snapshot.isEmpty) {
                val doc = snapshot.documents.first()

                val messageHistoryMap = doc.data!!

                launch {
                  val messages =
                      messageRef.document(doc.id).collection("messages_list").get().await()
                  messageHistoryMap["messages"] =
                      messages.documents.map { message ->
                        Message(
                            sender = message["sender"] as String,
                            content = message["content"] as String,
                            dateTimeSent = LocalDateTime.parse(message["date_time_sent"] as String),
                            id = message.id,
                        )
                      }

                  val messageHistory = MessageHistory(messageHistoryMap, doc.id)
                  trySend(Resource.Success(messageHistory))
                }
              } else {
                trySend(Resource.Failure(Exception("No message history found")))
              }
            }

        awaitClose { listener.remove() }
      }
}
