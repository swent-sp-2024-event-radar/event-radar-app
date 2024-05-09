package com.github.se.eventradar.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.event.EventList
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepository: IUserRepository) :
    ViewModel() {
  private val _uiState = MutableStateFlow(LoginUiState())
  val uiState: StateFlow<LoginUiState> = _uiState

  fun addUser(
      state: MutableStateFlow<LoginUiState> = _uiState
  ) {
    viewModelScope.launch{
        when (val userIdResource = userRepository.getCurrentUserId()){
            is Resource.Success -> {
                val uid = userIdResource.data
                // Upload Profile Pic Image to Firestore
                val imageURI =
                    state.value.selectedImageUri
                        ?: Uri.parse("android.resource://com.github.se.eventradar/drawable/placeholder")
                userRepository.uploadImage(imageURI, uid, "Profile_Pictures")
                //Generate QR Code Image and Upload it to Firestore
                userRepository.generateQRCode(uid)
                val qrCodeUrl =
                    when (val result = userRepository.getImage(uid, "QR_Codes")) {
                        is Resource.Success -> {
                            result.data
                        }
                        is Resource.Failure -> {
                            Log.d("LoginScreenViewModel","Fetching QR Code Error")
                            ""
                        }
                    }
                val profilePicUrl =
                    when (val result = userRepository.getImage(uid, "Profile_Pictures")) {
                        is Resource.Success -> {
                            result.data
                        }
                        is Resource.Failure -> {
                            Log.d("LoginScreenViewModel","Fetching Profile Picture Error")
                            ""
                        }
                    }
                val userEmail =
                    when (val result = userRepository.getUser(uid)) {
                        is Resource.Success -> {
                            result.data!!.email
                        }
                        is Resource.Failure -> {
                            Log.d("LoginScreenViewModel","Fetching Profile Picture Error")
                            ""
                        }
                    }
                val userValues =
                    hashMapOf(
                        "private/firstName" to state.value.firstName,
                        "private/lastName" to state.value.lastName,
                        "private/phoneNumber" to state.value.phoneNumber,
                        "private/birthDate" to state.value.birthDate,
                        "private/email" to userEmail,
                        "profilePicUrl" to profilePicUrl,
                        "qrCodeUrl" to qrCodeUrl,
                        "username" to state.value.username,
                        "accountStatus" to "active",
                        "eventsAttendeeList" to emptyList<String>(),
                        "eventsHostList" to emptyList<String>())
                addUserAsync(userValues, uid, state)
            }
            is Resource.Failure -> {
                Log.d("LoginScreenViewModel", "User not logged in")
            }
        }
    }
  }

  private suspend fun addUserAsync(userValues: Map<String, Any?>, userId: String, state: MutableStateFlow<LoginUiState>) {
    when (val result = userRepository.addUser(userValues, userId)) {
      is Resource.Success -> {
        state.value = state.value.copy(isSignUpCompleted = true, isSignUpSuccessful = true)
      }
      is Resource.Failure -> {
        Log.d("LoginScreenViewModel", "Error adding user: ${result.throwable.message}")
          state.value = state.value.copy(isSignUpCompleted = true, isSignUpSuccessful = false)
      }
    }
  }

  fun doesUserExist(userId: String): Boolean {
    var userExists: Boolean
    runBlocking { userExists = doesUserExistAsync(userId) }
    return userExists
  }

  private suspend fun doesUserExistAsync(userId: String): Boolean {
    return when (val result = userRepository.doesUserExist(userId)) {
      is Resource.Success -> {
        true
      }
      is Resource.Failure -> {
        Log.d("LoginScreenViewModel", "User not logged in: ${result.throwable.message}")
        false
      }
    }
  }
    fun onSignUpStarted(state: MutableStateFlow<LoginUiState> = _uiState) {
        state.value = state.value.copy(isSignUpStarted = true)
    }

  fun onSelectedImageUriChanged(uri: Uri?, state: MutableStateFlow<LoginUiState> = _uiState) {
    state.value = state.value.copy(selectedImageUri = uri)
  }

  fun onUsernameChanged(username: String, state: MutableStateFlow<LoginUiState> = _uiState) {
    state.value = state.value.copy(username = username)
  }

  fun onFirstNameChanged(firstName: String, state: MutableStateFlow<LoginUiState> = _uiState) {
    state.value = state.value.copy(firstName = firstName)
  }

  fun onLastNameChanged(lastName: String, state: MutableStateFlow<LoginUiState> = _uiState) {
    state.value = state.value.copy(lastName = lastName)
  }

  fun onPhoneNumberChanged(phoneNumber: String, state: MutableStateFlow<LoginUiState> = _uiState) {
    state.value = state.value.copy(phoneNumber = phoneNumber)
  }

  fun onBirthDateChanged(birthDate: String, state: MutableStateFlow<LoginUiState> = _uiState) {
    state.value = state.value.copy(birthDate = birthDate)
  }

  fun onCountryCodeChanged(
      countryCode: CountryCode,
      state: MutableStateFlow<LoginUiState> = _uiState
  ) {
    state.value = state.value.copy(selectedCountryCode = countryCode)
  }

  fun validateFields(state: MutableStateFlow<LoginUiState> = _uiState): Boolean {
    state.value =
        state.value.copy(
            userNameIsError = state.value.username.isEmpty(),
            firstNameIsError = state.value.firstName.isEmpty(),
            lastNameIsError = state.value.lastName.isEmpty(),
            phoneNumberIsError =
                !isValidPhoneNumber(state.value.phoneNumber, state.value.selectedCountryCode),
            birthDateIsError = !isValidDate(state.value.birthDate))

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
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    format.isLenient = false
    return try {
      format.parse(date)
      true
    } catch (e: Exception) {
      false
    }
  }
}

data class LoginUiState(
    val selectedImageUri: Uri? = null,
    val username: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val birthDate: String = "",
    val selectedCountryCode: CountryCode = CountryCode.CH,
    val userNameIsError: Boolean = false,
    val firstNameIsError: Boolean = false,
    val lastNameIsError: Boolean = false,
    val phoneNumberIsError: Boolean = false,
    val birthDateIsError: Boolean = false,
    val isSignUpStarted : Boolean = false,
    val isSignUpCompleted : Boolean = false,
    val isSignUpSuccessful : Boolean = false,
)

enum class CountryCode(val ext: String, val country: String, val numberLength: Int) {
  US("+1", "United States", 10),
  FR("+33", "France", 9),
  CH("+41", "Switzerland", 9),
  UK("+44", "United Kingdom", 10),
  AU("+61", "Australia", 9),
  JP("+81", "Japan", 10),
  IN("+91", "India", 10),
}
