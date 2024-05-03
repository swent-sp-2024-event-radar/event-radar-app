package com.github.se.eventradar.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MyQrCodeViewModel @Inject constructor(private val userRepository: IUserRepository) :
    ViewModel() {
  private val _uiState = MutableStateFlow(MyQrCodeUiState())
  val uiState: StateFlow<MyQrCodeUiState> = _uiState
  val qrCodesFolder = "QR_Codes"

  fun getUsername(uid: String? = Firebase.auth.currentUser?.uid) {
    viewModelScope.launch {
      when (val result = userRepository.getUser(uid!!)) {
        is Resource.Success -> {
          _uiState.value = _uiState.value.copy(username = result.data!!.username)
        }
        is Resource.Failure -> {
          Log.d("LoginScreenViewModel", "Error adding user: ${result.throwable.message}")
          false
        }
      }
    }
  }

  fun getQRCodeLink(uid: String? = Firebase.auth.currentUser?.uid) {
    viewModelScope.launch {
      when (val result = userRepository.getImage(uid!!, qrCodesFolder)) {
        is Resource.Success -> {
          _uiState.value = _uiState.value.copy(qrCodeLink = result.data)
          Log.d("MyQrCodeViewModel", "Image URL: ${result.data}")
        }
        is Resource.Failure -> {
          Log.d("MyQrCodeViewModel", "Error getting image: ${result.throwable.message}")
        }
      }
    }
  }
}

data class MyQrCodeUiState(
    val username: String = "",
    val qrCodeLink: String = "",
)
