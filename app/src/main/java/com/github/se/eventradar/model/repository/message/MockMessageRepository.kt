package com.github.se.eventradar.model.repository.message

import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.message.Message
import com.github.se.eventradar.model.message.MessageHistory

class MockMessageRepository : IMessageRepository {
  private val mockMessageHistory = mutableListOf<MessageHistory>()
  private var ticker = 0

  override suspend fun getMessages(uid: String): Resource<List<MessageHistory>> {
    val messageHistories =
        mockMessageHistory
            .filter { it.user1 == uid || it.user2 == uid }
            .sortedBy {
              it.messages.last { message -> message.id == it.latestMessageId }.dateTimeSent
            }
    return Resource.Success(messageHistories)
  }

  override suspend fun getMessages(user1: String, user2: String): Resource<MessageHistory> {
    val messageHistory =
        mockMessageHistory.find {
          (it.user1 == user1 && it.user2 == user2) || (it.user1 == user2 && it.user2 == user1)
        }

    return if (messageHistory != null) {
      Resource.Success(messageHistory)
    } else {
      Resource.Failure(Exception("No message history found between users"))
    }
  }

  override suspend fun addMessage(
      message: Message,
      messageHistory: MessageHistory
  ): Resource<Unit> {
    val messageHistoryId: String
    if (messageHistory.messages.isEmpty()) {
      val newHistoryResource = createNewMessageHistory(messageHistory.user1, messageHistory.user2)
      if (newHistoryResource is Resource.Failure) {
        return Resource.Failure(newHistoryResource.throwable)
      }

      val newHistory = (newHistoryResource as Resource.Success).data
      messageHistoryId = newHistory.id
    } else {
      // Use the existing message history ID
      messageHistoryId = messageHistory.id
    }
    val addMessage = mockMessageHistory.find { it.id == messageHistoryId }?.messages?.add(message)

    return if (addMessage != null && addMessage) {
      mockMessageHistory
          .find { it.id == messageHistoryId }
          ?.let {
            it.latestMessageId = message.id
            it.user1ReadMostRecentMessage = message.sender == it.user1
            it.user2ReadMostRecentMessage = message.sender == it.user2
          }
      Resource.Success(Unit)
    } else {
      Resource.Failure(Exception("MessageHistory with id $messageHistoryId not found"))
    }
  }

  override suspend fun updateReadStateForUser(
      userId: String,
      messageHistory: MessageHistory
  ): Resource<Unit> {
    val updateMessage =
        mockMessageHistory
            .find { it.id == messageHistory.id }
            ?.let {
              if (userId == it.user1) {
                it.user1ReadMostRecentMessage = true
              } else {
                it.user2ReadMostRecentMessage = true
              }
            }

    return if (updateMessage != null) {
      Resource.Success(Unit)
    } else {
      Resource.Failure(
          Exception(
              "Message history with id ${messageHistory.id} not found or user with id $userId not found"))
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
            user1ReadMostRecentMessage = false,
            user2ReadMostRecentMessage = false,
            latestMessageId = "",
            messages = mutableListOf(),
            id = "${ticker++}")
    mockMessageHistory.add(messageHistory)

    return Resource.Success(messageHistory)
  }
}
