package com.github.se.eventradar.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.message.MessageHistory
import com.github.se.eventradar.model.repository.message.IMessageRepository
import com.github.se.eventradar.model.repository.user.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ChatViewModel
@Inject
constructor(
    private val messagesRepository: IMessageRepository,
    private val userRepository: IUserRepository,
) : ViewModel() {
  private val _uiState = MutableStateFlow(ChatUiState())
  val uiState: StateFlow<ChatUiState> = _uiState

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
            username = "Default")
  fun onMessageBarInputChange(newInput: String) {
    _uiState.update { currentState -> currentState.copy(messageBarInput = newInput) }
  }

  // TO DO: Complete VM functions for chat screen
  init {
      viewModelScope.launch {
          _uiState.update {
              val userId = userRepository.getCurrentUserId()

              if (userId is Resource.Success) {
                  it.copy(userId = userId.data)
              } else {
                  Log.d(
                      "MessagesViewModel",
                      "Error getting user ID: ${(userId as Resource.Failure).throwable.message}")
                  it.copy(userId = null)
              }
          }

      }
  }
    fun initOpponent(opponentId: String) {
        viewModelScope.launch {
            _uiState.update {
                when (val opponentResource = userRepository.getUser(opponentId)) {
                    is Resource.Success -> {
                        it.copy(opponentProfile = opponentResource.data!!, opponentId = opponentId)
                    }
                    is Resource.Failure -> {
                        Log.d("ChatViewModel", "Error getting opponent details: ${opponentResource.throwable.message}")
                        it.copy(opponentProfile = nullUser, opponentId = null)
                    }
                }
            }
        }
    }
    fun initAndGetMessages(opponentId: String) {
        viewModelScope.launch {
            initOpponent(opponentId)

            // Fetch messages
            val userId = _uiState.value.userId
            if (userId != null && _uiState.value.opponentId!= null) {
                val messagesResource = messagesRepository.getMessages(userId, opponentId)
                _uiState.update { currentState ->
                    when (messagesResource) {
                        is Resource.Success -> {
                            currentState.copy(
                                messageHistory = messagesResource.data,
                                messagesLoadedFirstTime = true
                            )
                        }
                        is Resource.Failure -> {
                            Log.d("ChatViewModel", "Error fetching messages: ${messagesResource.throwable.message}")
                            currentState
                        }
                    }
                }
            } else {
                Log.d("ChatViewModel", "Invalid state: User ID or Opponent ID is null.")
            }
        }
    }

}

data class ChatUiState(
    val userId: String? = null,
    val opponentId: String? = null,
    val messageHistory: MessageHistory =
        MessageHistory(
            user1 = "Default sender",
            user2 = "Default recipient",
            latestMessageId = "DefaultId",
            user1ReadMostRecentMessage = false,
            user2ReadMostRecentMessage = false,
            messages = mutableListOf()),
    val opponentProfile: User = User(
        userId = "Default",
        birthDate = "Default",
        email = "Default",
        firstName = "Default",
        lastName = "Default",
        phoneNumber = "Default",
        accountStatus = "Default",
        eventsAttendeeSet = mutableSetOf(),
        eventsHostSet = mutableSetOf(),
        friendsSet = mutableSetOf(),
        profilePicUrl = "Default",
        qrCodeUrl = "Default",
        username = "Default"),
    val messageInserted: Boolean = false,
    val messagesLoadedFirstTime: Boolean = false,
    val messageBarInput: String = "",
)
