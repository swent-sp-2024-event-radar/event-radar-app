package com.github.se.eventradar.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.repository.user.IUserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ViewFriendsProfileViewModel.Factory::class)
class ViewFriendsProfileViewModel
@AssistedInject
constructor(
    private val userRepository: IUserRepository,
    @Assisted val friendUserId: String,
) : ViewModel() {

  private val _uiState = MutableStateFlow(ViewFriendsProfileUiState())
  val uiState: StateFlow<ViewFriendsProfileUiState> = _uiState.asStateFlow()

  init {
    getFriendProfileDetails()
  }

  fun getFriendProfileDetails() {
    viewModelScope.launch {
      when (val friendUserObj = userRepository.getUser(friendUserId)) {
        is Resource.Success -> {
          _uiState.value =
              _uiState.value.copy(
                  friendProfilePicLink = friendUserObj.data!!.profilePicUrl,
                  friendName = friendUserObj.data.firstName,
                  friendUserName = friendUserObj.data.username,
                  bio = friendUserObj.data.bio)
        }
        is Resource.Failure ->
            Log.d(
                "ViewFriendsProfileViewModel",
                "Error getting friend's user details for friendUserId ${friendUserId}")
      }
    }
  }

  @AssistedFactory
  interface Factory {
    fun create(friendUserId: String): ViewFriendsProfileViewModel
  }

  companion object {
    @Composable
    fun create(friendUserId: String): ViewFriendsProfileViewModel {
      return hiltViewModel<ViewFriendsProfileViewModel, Factory>(
          creationCallback = { factory -> factory.create(friendUserId = friendUserId) })
    }
  }
}

data class ViewFriendsProfileUiState(
    val friendProfilePicLink: String = "",
    val friendName: String = "",
    val friendUserName: String = "",
    val bio: String = "",
)
