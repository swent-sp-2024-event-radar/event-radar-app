package com.github.se.eventradar.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.message.Message
import com.github.se.eventradar.model.message.MessageHistory
import com.github.se.eventradar.model.repository.message.IMessageRepository
import com.github.se.eventradar.model.repository.user.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel
@Inject
constructor(
    private val messagesRepository: IMessageRepository,
    private val userRepository: IUserRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    fun onMessageBarInputChange(newInput: String) {
        _uiState.update { currentState -> currentState.copy(messageBarInput = newInput) }
    }

    // TO DO: Complete VM functions for chat screen
/**
    // TO DO: Copied from MessagesViewModel.kt
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

            if (_uiState.value.userId != null) {
                getMessages()
                getFriends()
            }
        }
    }

    fun onChatInputFocusChanged(state: MutableStateFlow<ChatUiState> = _uiState) {
        state.value = state.value.copy(isChatInputFocus = !state.value.isChatInputFocus)
    }



    fun onMessageSend() {
        val message = _uiState.value.messageBarInput
        if (message.isNotBlank()) {
            sendMessage(message)
        }
        insertMessageToFirebase(
            senderId,
            messageContent
        )
    }

    fun insertMessageToFirebase(
        chatRoomUUID: String,
        messageContent: String,
        registerUUID: String,
        oneSignalUserId: String
    ) {
        viewModelScope.launch {
            chatScreenUseCases.insertMessageToFirebase(
                chatRoomUUID,
                messageContent,
                registerUUID,
                oneSignalUserId
            ).collect { response ->
                when (response) {
                    is Response.Loading -> {
                        messageInserted.value = false
                    }
                    is Response.Success -> {
                        messageInserted.value = true
                    }
                    is Response.Error -> {}
                }
            }
        }
    }

    fun loadMessagesFromFirebase(chatRoomUUID: String, opponentUUID: String, registerUUID: String) {
        viewModelScope.launch {
            chatScreenUseCases.loadMessageFromFirebase(chatRoomUUID, opponentUUID, registerUUID)
                .collect { response ->
                    when (response) {
                        is Response.Loading -> {}
                        is Response.Success -> {
                            messages = listOf()
                            for (i in response.data) {
                                if (i.profileUUID == opponentUUID) {
                                    messages =
                                        messages + MessageRegister(i, true) //Opponent Message
                                } else {
                                    messages = messages + MessageRegister(i, false) //User Message
                                }

                            }
                            messagesLoadedFirstTime.value = true
                        }
                        is Response.Error -> {}
                    }
                }
        }
    }

    fun loadOpponentProfileFromFirebase(opponentUUID: String) {
        viewModelScope.launch {
            chatScreenUseCases.opponentProfileFromFirebase(opponentUUID).collect { response ->
                when (response) {
                    is Response.Loading -> {}
                    is Response.Success -> {
                        opponentProfileFromFirebase.value = response.data
                    }
                    is Response.Error -> {}
                }
            }
        }
    }
**/
}

data class ChatUiState(
    val userId: String? = null,
    val opponentId: String? = null,
    val messageHistory: MessageHistory = MessageHistory(
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
