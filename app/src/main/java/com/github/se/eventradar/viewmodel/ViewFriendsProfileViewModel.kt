package com.github.se.eventradar.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.repository.user.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewFriendsProfileViewModel
@Inject constructor(
    private val userRepository: IUserRepository
) : ViewModel() {

    private lateinit var friendUserId: String //To be injected.

    private val _uiState = MutableStateFlow(ViewFriendsProfileUiState())
    val uiState: StateFlow<ViewFriendsProfileUiState> = _uiState.asStateFlow()

    fun getFriendProfileDetails(){
        viewModelScope.launch {
            when (val friendUserObj = userRepository.getUser(friendUserId)) {
                is Resource.Success -> {
                    _uiState.value =
                        _uiState.value.copy(
                            friendProfilePicLink = friendUserObj.data!!.profilePicUrl,
                            friendName = friendUserObj.data.firstName,
                            friendUserName = friendUserObj.data.username,
                            bio = friendUserObj.data.accountStatus
                        )
                }
                is Resource.Failure ->
                    Log.d("ViewFriendsProfileViewModel", "Error getting friend details: ${friendUserObj.throwable.message}")
            }
        }
    }
}

data class ViewFriendsProfileUiState(
    val friendProfilePicLink : String = "",
    val friendName : String = "",
    val friendUserName : String = "",
    val bio : String = "",
)