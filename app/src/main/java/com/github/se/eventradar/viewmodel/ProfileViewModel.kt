package com.github.se.eventradar.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.repository.user.IUserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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
                    profilePicUrl = user.profilePicUrl,
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


    // Method to update UI state
    fun updateUiState(newUiState: ProfileUiState) {
        _uiState.value = newUiState
    }

    // Method to update user info in the database
    fun updateUserInfo(profilePicUrl: String, firstName: String, lastName: String, username: String, bio: String, phoneNumber: String, birthDate: String) {
        viewModelScope.launch {
            when (val result = userRepository.getUser(userId!!)) {
                is Resource.Success -> {
                    val user = result.data
                    val updatedUser = user!!.copy(
                        userId!!, birthDate, user.email, firstName, lastName, phoneNumber, user.accountStatus, user.eventsAttendeeList, user.eventsHostList, user.friendsList, profilePicUrl)

                    // Update the user in the database
                    when (val result = userRepository.updateUser(updatedUser)) {
                        is Resource.Success -> {
                            // Update was successful, update the UI state
                            _uiState.update {
                                it.copy(
                                    profilePicUrl = profilePicUrl,
                                    firstName = firstName,
                                    lastName = lastName,
                                    username = username,
                                    bio = bio,
                                    phoneNumber = phoneNumber,
                                    birthDate = birthDate
                                )
                            }
                        }
                        is Resource.Failure -> {
                            Log.d("ProfileViewModel", "Error updating user info")
                        }
                    }
                }
                is Resource.Failure -> {
                    Log.d("ProfileViewModel", "Error updating user info")
                }
            }

        }
    }

    fun onSelectedImageUriChanged(uri: Uri?, state: MutableStateFlow<ProfileUiState> = _uiState) {
        //state.update { currentState -> currentState.copy(selectedImageUri = uri) }
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

    fun onPhoneNumberChanged(phoneNumber: String, state: MutableStateFlow<ProfileUiState> = _uiState) {
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

    fun validateFields() {
        // Validate fields
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
    val profilePicUrl: String = "",
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
)

/*
fun validateFields(state: MutableStateFlow<ProfileUiState> = _uiState): Boolean {
    state.update { currentState ->
        currentState.copy(
            userNameIsError = state.value.username.isEmpty(),
            firstNameIsError = state.value.firstName.isEmpty(),
            lastNameIsError = state.value.lastName.isEmpty(),
            phoneNumberIsError =
            !isValidPhoneNumber(state.value.phoneNumber, state.value.selectedCountryCode),
            birthDateIsError = !isValidDate(state.value.birthDate))
    }

    return !state.value.userNameIsError &&
            !state.value.firstNameIsError &&
            !state.value.lastNameIsError &&
            !state.value.phoneNumberIsError &&
            !state.value.birthDateIsError
}

private fun isValidPhoneNumber(phoneNumber: String, countryCode: CountryCode): Boolean {
    return phoneNumber.length == countryCode.numberLength
}

private fun isValidDate(date: String): Boolean {
    val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    format.isLenient = false
    return try {
        val parsedDate = format.parse(date)

        val currentDate = Date()
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.YEAR, -130) // set limit to 130 years ago
        val pastDateLimit = calendar.time

        // return true if date not null and date is between today and past limit
        (parsedDate != null && parsedDate.before(currentDate) && parsedDate.after(pastDateLimit))
    } catch (e: Exception) {
        false
    }
}

enum class CountryCode(val ext: String, val country: String, val numberLength: Int) {
    US("+1", "United States", 10),
    FR("+33", "France", 9),
    CH("+41", "Switzerland", 9),
    UK("+44", "United Kingdom", 10),
    AU("+61", "Australia", 9),
    JP("+81", "Japan", 10),
    IN("+91", "India", 10),
}

 */
