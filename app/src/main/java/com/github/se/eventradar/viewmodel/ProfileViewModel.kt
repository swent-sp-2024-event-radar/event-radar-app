package com.github.se.eventradar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.repository.user.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(private val userRepository: IUserRepository) :
    ViewModel() {

  private val _uiState = MutableStateFlow(ProfileUiState())
  val uiState: StateFlow<ProfileUiState> = _uiState

  private val userId = "placeholderUserId" // replace this with the actual user ID

  fun updateUserData(field: String, newValue: String) {
    viewModelScope.launch {
      val result = userRepository.updateUserField(userId, field, newValue)
      if (result is Resource.Success) {
        _uiState.value = _uiState.value.updateField(field, newValue)
      }
    }
  }
}

data class ProfileUiState(
    val userId: String = "",
    val birthDate: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val accountStatus: String = "",
    val eventsAttendeeList: MutableList<String> = mutableListOf(),
    val eventsHostList: MutableList<String> = mutableListOf(),
    val friendsList: MutableList<String> = mutableListOf(),
    val profilePicUrl: String = "",
    val qrCodeUrl: String = "",
    val username: String = ""
) {
  fun updateField(field: String, newValue: Any): ProfileUiState {
    return when (field) {
      "userId" -> copy(userId = newValue as String)
      "birthDate" -> copy(birthDate = newValue as String)
      "email" -> copy(email = newValue as String)
      "firstName" -> copy(firstName = newValue as String)
      "lastName" -> copy(lastName = newValue as String)
      "phoneNumber" -> copy(phoneNumber = newValue as String)
      "accountStatus" -> copy(accountStatus = newValue as String)
      "eventsAttendeeList" -> copy(eventsAttendeeList = newValue as MutableList<String>)
      "eventsHostList" -> copy(eventsHostList = newValue as MutableList<String>)
      "friendsList" -> copy(friendsList = newValue as MutableList<String>)
      "profilePicUrl" -> copy(profilePicUrl = newValue as String)
      "qrCodeUrl" -> copy(qrCodeUrl = newValue as String)
      "username" -> copy(username = newValue as String)
      else -> this
    }
  }
}
