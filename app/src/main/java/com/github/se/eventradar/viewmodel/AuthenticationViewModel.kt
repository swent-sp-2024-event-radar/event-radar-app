package com.github.se.eventradar.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.eventradar.model.Resource
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
import kotlinx.coroutines.runBlocking

@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepository: IUserRepository) :
    ViewModel() {
  private val _uiState = MutableStateFlow(LoginUiState())
  val uiState: StateFlow<LoginUiState> = _uiState

  fun addUser(
      state: MutableStateFlow<LoginUiState> = _uiState,
      user: FirebaseUser? = Firebase.auth.currentUser
  ): Boolean {
    if (user == null) {
      Log.d("LoginScreenViewModel", "User not logged in")
      return false
    }

    val profilePicFolder = Folders.PROFILE_PICTURES.folderName
    var uploadedPictureSuccessfully: Boolean
    var profilePicUrl = ""

    // runBlocking { uploadImageAsync(state.value.selectedImageUri, user.uid, profilePicFolder) }
    // runBlocking { profilePicUrl = getImageAsync(user.uid, profilePicFolder) }
    /*
    runBlocking {
      uploadedPictureSuccessfully =
          uploadImageAsync(state.value.selectedImageUri, user.uid, profilePicFolder)
      if (uploadedPictureSuccessfully) {
        profilePicUrl = getImageAsync(user.uid, profilePicFolder)
      }
    }


    if (!uploadedPictureSuccessfully) {
      return false
    }

       */

    val userValues =
        hashMapOf(
            "private/firstName" to state.value.firstName,
            "private/lastName" to state.value.lastName,
            "private/phoneNumber" to
                state.value.selectedCountryCode.ext.plus(state.value.phoneNumber),
            "private/birthDate" to state.value.birthDate,
            "private/email" to user.email,
            "profilePicUrl" to profilePicUrl,
            "qrCodeUrl" to "", // TODO: generate QR code here
            "username" to state.value.username,
            "accountStatus" to "active",
            "eventsAttendeeList" to mutableListOf<String>(),
            "eventsHostList" to mutableListOf<String>(),
        )

    // Add a new document with a generated ID into collection "users"
    val success: Boolean
    runBlocking { success = addUserAsync(userValues, user.uid) }

    return success
  }

  private suspend fun addUserAsync(userValues: Map<String, Any?>, userId: String): Boolean {
    return when (val result = userRepository.addUser(userValues, userId)) {
      is Resource.Success -> {
        true
      }
      is Resource.Failure -> {
        Log.d("LoginScreenViewModel", "Error adding user: ${result.throwable.message}")
        false
      }
    }
  }

  private suspend fun uploadImageAsync(
      selectedImageUri: Uri?,
      uid: String,
      folderName: String
  ): Boolean {
    if (selectedImageUri == null) {
      return false
    }
    return when (val result = userRepository.uploadImage(selectedImageUri, uid, folderName)) {
      is Resource.Success -> {
        true
      }
      is Resource.Failure -> {
        Log.d("LoginScreenViewModel", "Error uploading image: ${result.throwable.message}")
        false
      }
    }
  }

  private suspend fun getImageAsync(uid: String, folderName: String): String {
    return when (val result = userRepository.getImage(uid, folderName)) {
      is Resource.Success -> {
        Log.d("LoginScreenViewModel", "Image URL: ${result.data}")
        result.data
      }
      is Resource.Failure -> {
        Log.d("LoginScreenViewModel", "Error getting image: ${result.throwable.message}")
        ""
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
