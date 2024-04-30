package com.github.se.eventradar.ui.messages

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.message.MessageHistory
import com.github.se.eventradar.model.repository.message.IMessageRepository
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

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
    getMessages(_uiState.value.userId)
  }

  private fun getMessages(uid: String) {
    viewModelScope.launch {
      when (val response = messagesRepository.getMessages(uid)) {
        is Resource.Success -> {
          _uiState.value = _uiState.value.copy(messageList = response.data)
        }
        is Resource.Failure -> {
          Log.d("MessagesViewModel", "Error getting messages: ${response.throwable.message}")
        }
      }
    }
  }

  fun onSearchQueryChange(query: String) {
    _uiState.value = _uiState.value.copy(searchQuery = query)
  }

  fun onSelectedTabIndexChange(index: Int) {
    _uiState.value = _uiState.value.copy(selectedTabIndex = index)
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
    val userId: String = FirebaseAuth.getInstance().currentUser!!.uid,
    val messageList: List<MessageHistory> = emptyList(),
    val searchQuery: String = "",
    val selectedTabIndex: Int = 0,
)
