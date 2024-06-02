package com.github.se.eventradar.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.github.se.eventradar.viewmodel.user.isValidDate
import com.github.se.eventradar.viewmodel.user.isValidPhoneNumber
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltViewModel(assistedFactory = ProfileViewModel.Factory::class)
class ProfileViewModel
@AssistedInject
constructor(private val userRepository: IUserRepository, @Assisted var userId: String?) :
    ViewModel() {

  private val _uiState = MutableStateFlow(ProfileUiState())
  val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

  init {
    getProfileDetails()
  }

  fun getProfileDetails() {
    viewModelScope.launch {
      if (userId == null) {
        when (val currentUid = userRepository.getCurrentUserId()) {
          is Resource.Success -> {
            userId = currentUid.data
          }
          is Resource.Failure -> Log.d("ProfileViewModel", "Error getting current user id")
        }
      } else {
        when (val userResource = userRepository.getUser(userId!!)) {
          is Resource.Success -> {
            val user = userResource.data
            if (user != null) {
              _uiState.update {
                it.copy(
                    profilePicUri = user.profilePicUrl.toUri(),
                    firstName = user.firstName,
                    lastName = user.lastName,
                    username = user.username,
                    bio = user.bio,
                    phoneNumber = user.phoneNumber,
                    birthDate = user.birthDate)
              }
            }
          }
          is Resource.Failure ->
              Log.d("ProfileViewModel", "Error getting user details for user $userId")
        }
      }
    }
  }

  // Method to update user info in the database
  fun updateUserInfo() {
    viewModelScope.launch {
      when (val result = userRepository.getUser(userId!!)) {
        is Resource.Success -> {
          val user = result.data
          uiState.value.profilePicUri?.let {
            userRepository.uploadImage(it, userId!!, "Profile_Pictures")
          }
          val profilePicUrl: String =
              when (val imageUrl = userRepository.getImage(userId!!, "Profile_Pictures")) {
                is Resource.Success -> imageUrl.data
                is Resource.Failure -> {
                  Log.d("ProfileViewModel", "Error getting profile picture")
                  ""
                }
              }
          val updatedUser =
              user!!.copy(
                  profilePicUrl = profilePicUrl,
                  firstName = uiState.value.firstName,
                  lastName = uiState.value.lastName,
                  username = uiState.value.username,
                  bio = uiState.value.bio,
                  phoneNumber = uiState.value.phoneNumber,
                  birthDate = uiState.value.birthDate)

          // Update the user in the database
          when (userRepository.updateUser(updatedUser)) {
            is Resource.Success -> {
              Log.d("ProfileViewModel", "User info updated successfully")
            }
            is Resource.Failure -> {
              Log.d("ProfileViewModel", "Error updating user info")
            }
          }
        }
        is Resource.Failure -> {
          Log.d("ProfileViewModel", "Error getting User $userId")
        }
      }
    }
  }

  fun onSelectedImageUriChanged(uri: Uri?, state: MutableStateFlow<ProfileUiState> = _uiState) {
    state.update { currentState -> currentState.copy(profilePicUri = uri) }
  }

  fun onUsernameChanged(username: String, state: MutableStateFlow<ProfileUiState> = _uiState) {
    state.update { currentState -> currentState.copy(username = username) }
  }

  fun onFirstNameChanged(firstName: String, state: MutableStateFlow<ProfileUiState> = _uiState) {
    state.update { currentState -> currentState.copy(firstName = firstName) }
  }

  fun onLastNameChanged(lastName: String, state: MutableStateFlow<ProfileUiState> = _uiState) {
    state.update { currentState -> currentState.copy(lastName = lastName) }
  }

  fun onPhoneNumberChanged(
      phoneNumber: String,
      state: MutableStateFlow<ProfileUiState> = _uiState
  ) {
    state.update { currentState -> currentState.copy(phoneNumber = phoneNumber) }
  }

  fun onBirthDateChanged(birthDate: String, state: MutableStateFlow<ProfileUiState> = _uiState) {
    state.update { currentState -> currentState.copy(birthDate = birthDate) }
  }

  fun onCountryCodeChanged(
      countryCode: CountryCode,
      state: MutableStateFlow<ProfileUiState> = _uiState
  ) {
    state.update { currentState -> currentState.copy(selectedCountryCode = countryCode) }
  }

  fun onBioChanged(bio: String, state: MutableStateFlow<ProfileUiState> = _uiState) {
    state.update { currentState -> currentState.copy(bio = bio) }
  }

  fun logOut(context: Context) {
    // Clear user data from local storage
    runBlocking {
      when (userRepository.signOut()) {
        is Resource.Success -> {
          Log.d("ProfileViewModel", "User logged out successfully")
        }
        is Resource.Failure -> {
          Log.d("ProfileViewModel", "Error logging out user")
        }
      }
    }

    // Cancel the viewModelScope
    viewModelScope.cancel()

    // Clear the userId
    userId = null

    // Clear the uiState
    _uiState.value = ProfileUiState()

    // Sign out from Google
    val googleSignInClient: GoogleSignInClient =
        GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN)
    googleSignInClient.signOut()
  }

  fun validateFields(state: MutableStateFlow<ProfileUiState> = _uiState): Boolean {
    state.update { currentState ->
      currentState.copy(
          userNameIsError = state.value.username.isEmpty(),
          firstNameIsError = state.value.firstName.isEmpty(),
          lastNameIsError = state.value.lastName.isEmpty(),
          phoneNumberIsError =
              !isValidPhoneNumber(state.value.phoneNumber, state.value.selectedCountryCode),
          birthDateIsError = !isValidDate(state.value.birthDate),
          bioIsError = state.value.bio.isEmpty())
    }

    return !state.value.userNameIsError &&
        !state.value.firstNameIsError &&
        !state.value.lastNameIsError &&
        !state.value.phoneNumberIsError &&
        !state.value.birthDateIsError &&
        !state.value.bioIsError
  }

  fun getUiState(): ProfileUiState {
    return _uiState.value
  }

  @AssistedFactory
  interface Factory {
    fun create(userId: String?): ProfileViewModel
  }

  companion object {
    @Composable
    fun create(userId: String? = null): ProfileViewModel {
      return hiltViewModel<ProfileViewModel, Factory>(
          creationCallback = { factory -> factory.create(userId = userId) })
    }
  }
}

data class ProfileUiState(
    val profilePicUri: Uri? = null,
    val username: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val bio: String = "",
    val phoneNumber: String = "",
    val birthDate: String = "",
    val selectedCountryCode: CountryCode = CountryCode.CH,
    val userNameIsError: Boolean = false,
    val firstNameIsError: Boolean = false,
    val lastNameIsError: Boolean = false,
    val phoneNumberIsError: Boolean = false,
    val birthDateIsError: Boolean = false,
    val bioIsError: Boolean = false
)
