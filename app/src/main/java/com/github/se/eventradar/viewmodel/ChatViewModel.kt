package com.github.se.eventradar.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.message.Message
import com.github.se.eventradar.model.message.MessageHistory
import com.github.se.eventradar.model.repository.message.IMessageRepository
import com.github.se.eventradar.model.repository.user.IUserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ChatViewModel.Factory::class)
class ChatViewModel
@AssistedInject
constructor(
    private val messagesRepository: IMessageRepository,
    private val userRepository: IUserRepository,
    @Assisted val opponentId: String,
) : ViewModel() {
  private val _uiState = MutableStateFlow(ChatUiState())
  val uiState: StateFlow<ChatUiState> = _uiState

  @AssistedFactory
  interface Factory {
    fun create(opponentId: String): ChatViewModel
  }

  companion object {
    @Composable
    fun create(opponentId: String): ChatViewModel {
      return hiltViewModel<ChatViewModel, Factory>(
          creationCallback = { factory -> factory.create(opponentId = opponentId) })
    }
  }

  private val nullUser =
      User(
          userId = "Default",
          birthDate = "Default",
          email = "Default",
          firstName = "Default",
          lastName = "Default",
          phoneNumber = "Default",
          accountStatus = "Default",
          eventsAttendeeList = mutableListOf(),
          eventsHostList = mutableListOf(),
          friendsList = mutableListOf(),
          profilePicUrl = "Default",
          qrCodeUrl = "Default",
          bio = "",
          username = "Default")

  init {

    viewModelScope.launch {
      _uiState.update {
        val userId = userRepository.getCurrentUserId()

        if (userId is Resource.Success) {
          it.copy(userId = userId.data)
        } else {
          Log.d(
              "ChatViewModel",
              "Error getting user ID: ${(userId as Resource.Failure).throwable.message}")
          it.copy(userId = null)
        }
      }
    }
    initOpponent()
    getMessages()
  }

  private fun initOpponent() {
    viewModelScope.launch {
      _uiState.update {
        when (val opponentResource = userRepository.getUser(opponentId)) {
          is Resource.Success -> {
            it.copy(opponentProfile = opponentResource.data!!)
          }
          is Resource.Failure -> {
            Log.d(
                "ChatViewModel",
                "Error getting opponent details: ${opponentResource.throwable.message}")
            it.copy(opponentProfile = nullUser)
          }
        }
      }
    }
  }

  fun getMessages() {
    viewModelScope.launch {
      // Fetch messages
      val userId = _uiState.value.userId
      if (userId != null) {
        val messagesResource = messagesRepository.getMessages(userId, opponentId)
        _uiState.update { currentState ->
          when (messagesResource) {
            is Resource.Success -> {
              currentState.copy(
                  messageHistory = messagesResource.data, messagesLoadedFirstTime = true)
            }
            is Resource.Failure -> {
              Log.d(
                  "ChatViewModel", "Error fetching messages: ${messagesResource.throwable.message}")
              currentState
            }
          }
        }
      } else {
        Log.d("ChatViewModel", "Invalid state: User ID is null.")
      }
    }
  }

  fun onMessageBarInputChange(newInput: String) {
    _uiState.update { currentState ->
      currentState.copy(messageBarInput = newInput, messageInserted = false)
    }
  }

  fun onMessageSend() {
    val message = _uiState.value.messageBarInput
    if (message.isNotBlank()) {
      sendMessage(message)
      getMessages()
      _uiState.update { currentState ->
        currentState.copy(messageBarInput = "", messageInserted = true)
      }
    }
  }

  private fun sendMessage(message: String) {
    viewModelScope.launch {
      val userId = _uiState.value.userId
      if (userId != null) {

        val newMessage =
            Message(
                sender = userId,
                content = message,
                dateTimeSent = LocalDateTime.now(),
                id = "") // Temporarily empty until Firebase assigns an ID

        messagesRepository.addMessage(newMessage, _uiState.value.messageHistory)
      } else {
        Log.d("ChatViewModel", "Invalid state: User ID")
      }
    }
  }
}

data class ChatUiState(
    val userId: String? = null,
    val messageHistory: MessageHistory =
        MessageHistory(
            user1 = "Default sender",
            user2 = "Default recipient",
            latestMessageId = "DefaultId",
            user1ReadMostRecentMessage = false,
            user2ReadMostRecentMessage = false,
            messages = mutableListOf()),
    val opponentId: String? = null, // To be deleted once VM is integrated with UI
    val opponentProfile: User =
        User(
            userId = "Default",
            birthDate = "Default",
            email = "Default",
            firstName = "Default",
            lastName = "Default",
            phoneNumber = "Default",
            accountStatus = "Default",
            eventsAttendeeList = mutableListOf(),
            eventsHostList = mutableListOf(),
            friendsList = mutableListOf(),
            profilePicUrl = "Default",
            qrCodeUrl = "Default",
            bio = "",
            username = "Default"),
    val messageInserted: Boolean = false,
    val messagesLoadedFirstTime: Boolean = false,
    val messageBarInput: String = "",
)
