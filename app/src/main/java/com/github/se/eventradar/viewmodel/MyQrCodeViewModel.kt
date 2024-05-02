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
import kotlinx.coroutines.runBlocking

@HiltViewModel
class MyQrCodeViewModel @Inject constructor(private val userRepository: IUserRepository) :
    ViewModel() {
  private val _uiState = MutableStateFlow(MyQrCodeUiState())
  val uiState: StateFlow<MyQrCodeUiState> = _uiState
  val qrCodesFolder = Folders.QR_Codes.folderName

  fun getUserDetails(uid: String? = Firebase.auth.currentUser?.uid){
    viewModelScope.launch {
      when (val result = userRepository.getUser(uid!!)){
        is Resource.Success -> {
          _uiState.value = _uiState.value.copy(username = result.data!!.username, qrCodeLink = getImageAsync(uid, qrCodesFolder))
        }
        is Resource.Failure -> {
          Log.d("LoginScreenViewModel", "Error adding user: ${result.throwable.message}")
          false
        }
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
    val username: String = "",
    val qrCodeLink: String = "",
)
