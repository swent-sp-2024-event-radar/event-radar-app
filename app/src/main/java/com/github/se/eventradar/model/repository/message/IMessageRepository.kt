package com.github.se.eventradar.model.repository.message

import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.message.Message
import com.github.se.eventradar.model.message.MessageHistory

interface IMessageRepository {
  suspend fun getMessages(user1: String, user2: String): Resource<MessageHistory>
  
  suspend fun addMessage(message: Message, messageHistory: MessageHistory): Resource<Unit>
  
  suspend fun updateMessageToReadState(message: Message, messageHistory: MessageHistory): Resource<Unit>
  
  suspend fun createNewMessageHistory(user1: String, user2: String): Resource<MessageHistory>
}