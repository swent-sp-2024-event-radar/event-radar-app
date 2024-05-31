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
import kotlinx.coroutines.runBlocking

@HiltViewModel
class MessagesViewModel
@Inject
constructor(
    private val messagesRepository: IMessageRepository,
    private val userRepository: IUserRepository,
) : ViewModel() {
  private val _uiState = MutableStateFlow(MessagesUiState())
  val uiState: StateFlow<MessagesUiState> = _uiState

  init {
    viewModelScope.launch {
      val userId = userRepository.getCurrentUserId()

      if (userId is Resource.Success) {
        _uiState.update { it.copy(userId = userId.data) }
        getMessages()
      } else {
        Log.d(
            "MessagesViewModel",
            "Error getting user ID: ${(userId as Resource.Failure).throwable.message}")
        _uiState.update { it.copy(userId = null) }
      }
    }
  }

  fun getMessages() {
    viewModelScope.launch {
      when (val response = messagesRepository.getMessages(_uiState.value.userId!!)) {
        is Resource.Success -> {
          // Filter the message histories to include only those with non-empty messages
          val filteredMessageList = response.data.filter { it.messages.isNotEmpty() }

          // Sort the filtered message list by timestamp of the latest message for each message
          // history
          val sortedMessageList =
              filteredMessageList.sortedByDescending {
                it.messages.find { message -> message.id == it.latestMessageId }?.dateTimeSent
              }

          // Update the UI state with the sorted and filtered message list

          _uiState.update { it.copy(messageList = sortedMessageList) }
        }
        is Resource.Failure -> {
          Log.d("MessagesViewModel", "Error getting messages: ${response.throwable.message}")
        }
      }
    }
  }

  fun getFriends() {
    viewModelScope.launch {
      when (val response = userRepository.getUser(_uiState.value.userId!!)) {
        is Resource.Success -> {
          val friendsList = mutableListOf<User>()
          for (friendId in response.data!!.friendsList) {
            val friend = userRepository.getUser(friendId)
            if (friend is Resource.Success) {
              friendsList.add(friend.data!!)
            } else {
              Log.d(
                  "MessagesViewModel",
                  "Error getting friend: ${(friend as Resource.Failure).throwable.message}")
            }
          }
          _uiState.update { it.copy(friendsList = friendsList) }
        }
        is Resource.Failure -> {
          Log.d("MessagesViewModel", "Error getting friends: ${response.throwable.message}")
        }
      }
    }
  }

  fun onSearchQueryChanged(query: String, state: MutableStateFlow<MessagesUiState> = _uiState) {
    state.update { it.copy(searchQuery = query) }
    filterMessagesLists()
  }

  fun onSearchActiveChanged(
      isActive: Boolean,
      state: MutableStateFlow<MessagesUiState> = _uiState
  ) {
    state.update { it.copy(isSearchActive = isActive) }
  }

  fun onSelectedTabIndexChanged(index: Int, state: MutableStateFlow<MessagesUiState> = _uiState) {
    state.update { it.copy(selectedTabIndex = index, isSearchActive = false, searchQuery = "") }
    if (index == 0) {
      getMessages()
    } else {
      getFriends()
    }
  }

  fun filterMessagesLists() {
    val query = _uiState.value.searchQuery
    val friendsList = _uiState.value.friendsList
    val messageList = _uiState.value.messageList

    if (_uiState.value.selectedTabIndex == 0) {
      val filteredMessageList =
          messageList.filter { messageHistory ->
            val friend =
                getUser(
                    if (_uiState.value.userId != messageHistory.user1) messageHistory.user1
                    else messageHistory.user2)
            friend.firstName.contains(query, ignoreCase = true) or
                friend.lastName.contains(query, ignoreCase = true)
          }

      _uiState.update { it.copy(filteredMessageList = filteredMessageList) }
    } else {
      val filteredFriendsList =
          friendsList.filter { user ->
            user.firstName.contains(query, ignoreCase = true) or
                user.lastName.contains(query, ignoreCase = true)
          }
      _uiState.update { it.copy(filteredFriendsList = filteredFriendsList) }
    }
  }

  fun getUser(userId: String): User {
    var user: User?

    runBlocking { user = getUserAsync(userId) }

    return user!!
  }

  private suspend fun getUserAsync(userId: String): User? {
    return when (val response = userRepository.getUser(userId)) {
      is Resource.Success -> {
        return response.data!!
      }
      is Resource.Failure -> {
        Log.d("MessagesViewModel", "Error getting user: ${response.throwable.message}")
        return null
      }
    }
  }
}

data class MessagesUiState(
    val userId: String? = null,
    val messageList: List<MessageHistory> = emptyList(),
    val filteredMessageList: List<MessageHistory> = messageList,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val selectedTabIndex: Int = 0,
    val friendsList: List<User> = emptyList(),
    val filteredFriendsList: List<User> = friendsList,
)
