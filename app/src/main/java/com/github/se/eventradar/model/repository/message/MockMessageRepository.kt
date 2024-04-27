package com.github.se.eventradar.model.repository.message

import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.message.Message
import com.github.se.eventradar.model.message.MessageHistory

class MockMessageRepository : IMessageRepository {
  private val mockMessageHistory = mutableListOf<MessageHistory>()
  private var ticker = 0

  override suspend fun getMessages(uid: String): Resource<List<MessageHistory>> {
    val messageHistories = mockMessageHistory.filter { it.user1 == uid || it.user2 == uid }
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
      createNewMessageHistory(user1, user2)
    }
  }

  override suspend fun addMessage(
      message: Message,
      messageHistory: MessageHistory
  ): Resource<Unit> {
    val addMessage = mockMessageHistory.find { it.id == messageHistory.id }?.messages?.add(message)

    return if (addMessage != null && addMessage) {
      mockMessageHistory.find { it.id == messageHistory.id }?.latestMessageId = message.id
      Resource.Success(Unit)
    } else {
      Resource.Failure(Exception("MessageHistory with id ${messageHistory.id} not found"))
    }
  }

  override suspend fun updateMessageToReadState(
      message: Message,
      messageHistory: MessageHistory
  ): Resource<Unit> {
    val updateMessage =
        mockMessageHistory
            .find { it.id == messageHistory.id }
            ?.messages
            ?.find { it.id == message.id }
            ?.let { it.isRead = true }

    return if (updateMessage != null) {
      Resource.Success(Unit)
    } else {
      Resource.Failure(
          Exception(
              "Message with id ${message.id} not found in MessageHistory with id ${messageHistory.id}"))
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
            id = "${ticker++}")
    mockMessageHistory.add(messageHistory)

    return Resource.Success(messageHistory)
  }
}
