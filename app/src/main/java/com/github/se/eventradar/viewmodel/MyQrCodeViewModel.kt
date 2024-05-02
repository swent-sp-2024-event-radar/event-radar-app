package com.github.se.eventradar.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking

@HiltViewModel
class MyQrCodeViewModel @Inject constructor(private val userRepository: IUserRepository) :
    ViewModel() {
  private val _uiState = MutableStateFlow(MyQrCodeUiState())
  val uiState: StateFlow<MyQrCodeUiState> = _uiState
  val qrCodesFolder = Folders.QR_Codes.folderName

  init {
    getQRCode()
  }

  fun getQRCode(uid: String? = Firebase.auth.currentUser?.uid) {
    if (uid == null) {
      Log.d("MyQrCodeViewModel", "User not logged in")
    } else {
      runBlocking {
        _uiState.value = _uiState.value.copy(qrCodeLink = getImageAsync(uid, qrCodesFolder))
      }
    }
  }

  private suspend fun getImageAsync(uid: String, folderName: String): String {
    return when (val result = userRepository.getImage(uid, folderName)) {
      is Resource.Success -> {
        Log.d("MyQrCodeViewModel", "Image URL: ${result.data}")
        result.data
      }
      is Resource.Failure -> {
        Log.d("MyQrCodeViewModel", "Error getting image: ${result.throwable.message}")
        ""
      }
    }
  }
}

data class MyQrCodeUiState(
    val qrCodeLink: String = "",
)
