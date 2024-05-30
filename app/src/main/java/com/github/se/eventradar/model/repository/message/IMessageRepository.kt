package com.github.se.eventradar.model.repository.message

import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.message.Message
import com.github.se.eventradar.model.message.MessageHistory
import kotlinx.coroutines.flow.Flow

interface IMessageRepository {
  suspend fun getMessages(uid: String): Resource<List<MessageHistory>>

  suspend fun getMessages(user1: String, user2: String): Resource<MessageHistory>

  suspend fun addMessage(message: Message, messageHistory: MessageHistory): Resource<Unit>

  suspend fun updateReadStateForUser(userId: String, messageHistory: MessageHistory): Resource<Unit>

  suspend fun createNewMessageHistory(user1: String, user2: String): Resource<MessageHistory>

  fun observeMessages(user1: String, user2: String): Flow<Resource<MessageHistory>>
}
