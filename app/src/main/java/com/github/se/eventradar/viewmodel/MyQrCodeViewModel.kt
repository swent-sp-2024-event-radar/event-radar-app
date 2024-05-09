package com.github.se.eventradar.viewmodel

import android.util.Log
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
class MyQrCodeViewModel @Inject constructor(private val userRepository: IUserRepository) :
    ViewModel() {
  private val _uiState = MutableStateFlow(MyQrCodeUiState())
  val uiState: StateFlow<MyQrCodeUiState> = _uiState
  val qrCodesFolder = "QR_Codes"

  fun getUsername() {
    viewModelScope.launch {
      when (val userIdResource = userRepository.getCurrentUserId()) {
        is Resource.Success -> {
          val userId = userIdResource.data
          // Now fetch the user data using the fetched user ID
          when (val userResult = userRepository.getUser(userId)) {
            is Resource.Success -> {
              _uiState.value = _uiState.value.copy(username = userResult.data!!.username, qrCodeLink = userResult.data!!.qrCodeUrl)
            }
            is Resource.Failure -> {
              Log.d("MyQrCodeViewModel", "Error fetching username: ${userResult.throwable.message}?alt=media&token=bffb6ae0-45bd-455b-8e27-8920734779f3")
            }
          }
        }
        is Resource.Failure -> {
          Log.d("MyQrCodeViewModel", "Error fetching user ID: ${userIdResource.throwable.message}")
        }
      }
    }
  }
  /*
  fun getQRCodeLink() {
    viewModelScope.launch {
      when (val userIdResource = userRepository.getCurrentUserId()) {
        is Resource.Success -> {
          val userId = userIdResource.data
          // Now fetch the user data using the fetched user ID
          when (val result = userRepository.getImage(userId, qrCodesFolder)) {
            is Resource.Success -> {
              _uiState.value = _uiState.value.copy(qrCodeLink = result.data)
            }
            is Resource.Failure -> {
              Log.d("MyQrCodeViewModel", "Error fetching image: ${result.throwable.message}")
            }
          }
        }
        is Resource.Failure -> {
          Log.d("MyQrCodeViewModel", "Error fetching user ID: ${userIdResource.throwable.message}")
        }
      }
    }
  }
   */
}

data class MyQrCodeUiState(
    val username: String = "",
    val qrCodeLink: String = "",
)
