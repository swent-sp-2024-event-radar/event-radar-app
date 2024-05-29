package com.github.se.eventradar.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepository: IUserRepository) :
    ViewModel() {
  private val _uiState = MutableStateFlow(LoginUiState())
  val uiState: StateFlow<LoginUiState> = _uiState

  fun addUser(state: MutableStateFlow<LoginUiState> = _uiState) {
    viewModelScope.launch {
      when (val userIdResource = userRepository.getCurrentUserId()) {
        is Resource.Success -> {
          val uid = userIdResource.data
          // Upload Profile Pic Image to Firestore
          val imageURI =
              state.value.selectedImageUri
                  ?: Uri.parse("android.resource://com.github.se.eventradar/drawable/placeholder")
          userRepository.uploadImage(imageURI, uid, "Profile_Pictures")
          // Generate QR Code Image and Upload it to Firestore
          val qrCodeUrl =
              when (val qrCodeData = generateQRCodeData(uid)) {
                null -> {
                  Log.d(
                      "Exception",
                      "QR Code Generation failed, a null QR Code ByteArray is received")
                  ""
                }
                else -> {
                  userRepository.uploadQRCode(qrCodeData, uid)
                  when (val result = userRepository.getImage(uid, "QR_Codes")) {
                    is Resource.Success -> result.data
                    is Resource.Failure -> {
                      Log.d("LoginScreenViewModel", "Fetching QR Code Error")
                      ""
                    }
                  }
                }
              }
          val profilePicUrl =
              when (val result = userRepository.getImage(uid, "Profile_Pictures")) {
                is Resource.Success -> {
                  result.data
                }
                is Resource.Failure -> {
                  Log.d("LoginScreenViewModel", "Fetching Profile Picture Error")
                  ""
                }
              }
          val userEmail =
              when (val result = userRepository.getUser(uid)) {
                is Resource.Success -> {
                  result.data!!.email
                }
                is Resource.Failure -> {
                  Log.d("LoginScreenViewModel", "Fetching Profile Picture Error")
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
                  "bio" to "",
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

  private fun generateQRCodeData(userId: String): ByteArray? {
    return try {
      val qrCodeWriter = QRCodeWriter()
      val bitMatrix = qrCodeWriter.encode(userId, BarcodeFormat.QR_CODE, 200, 200)
      val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.RGB_565)
      for (x in 0 until 200) {
        for (y in 0 until 200) {
          bitmap.setPixel(
              x,
              y,
              if (bitMatrix[x, y]) Color(0xFF000000).hashCode() else Color(0xFFFFFFFF).hashCode())
        }
      }
      // Convert the bitmap to a byte array
      val baos = ByteArrayOutputStream()
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
      val data = baos.toByteArray()
      data
    } catch (e: Exception) {
      Log.d("Exception", "Error generating QR Code for User: ${e.message}")
      null
    }
  }

  private suspend fun addUserAsync(
      userValues: Map<String, Any?>,
      userId: String,
      state: MutableStateFlow<LoginUiState>
  ) {
    when (val result = userRepository.addUser(userValues, userId)) {
      is Resource.Success -> {
          state.update { currentState -> currentState.copy(isSignUpCompleted = true, isSignUpSuccessful = true) }
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
      state.update { currentState -> currentState.copy(isSignUpStarted = true) }
  }

  fun onSelectedImageUriChanged(uri: Uri?, state: MutableStateFlow<LoginUiState> = _uiState) {
      state.update { currentState -> currentState.copy(selectedImageUri = uri) }
  }

  fun onUsernameChanged(username: String, state: MutableStateFlow<LoginUiState> = _uiState) {
      state.update { currentState -> currentState.copy(username = username) }
  }

  fun onFirstNameChanged(firstName: String, state: MutableStateFlow<LoginUiState> = _uiState) {
      state.update { currentState -> currentState.copy(firstName = firstName) }
  }

  fun onLastNameChanged(lastName: String, state: MutableStateFlow<LoginUiState> = _uiState) {
      state.update { currentState -> currentState.copy(lastName = lastName) }
  }

  fun onPhoneNumberChanged(phoneNumber: String, state: MutableStateFlow<LoginUiState> = _uiState) {
      state.update { currentState -> currentState.copy(phoneNumber = phoneNumber) }
  }

  fun onBirthDateChanged(birthDate: String, state: MutableStateFlow<LoginUiState> = _uiState) {
      state.update { currentState -> currentState.copy(birthDate = birthDate) }
  }

  fun onCountryCodeChanged(
      countryCode: CountryCode,
      state: MutableStateFlow<LoginUiState> = _uiState
  ) {
      state.update { currentState -> currentState.copy(selectedCountryCode = countryCode) }
  }

  fun validateFields(state: MutableStateFlow<LoginUiState> = _uiState): Boolean {
      state.update { currentState -> currentState.copy(
          userNameIsError = state.value.username.isEmpty(),
          firstNameIsError = state.value.firstName.isEmpty(),
          lastNameIsError = state.value.lastName.isEmpty(),
          phoneNumberIsError =
          !isValidPhoneNumber(state.value.phoneNumber, state.value.selectedCountryCode),
          birthDateIsError = !isValidDate(state.value.birthDate))}

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
    val isSignUpStarted: Boolean = false,
    val isSignUpCompleted: Boolean = false,
    val isSignUpSuccessful: Boolean = false,
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
