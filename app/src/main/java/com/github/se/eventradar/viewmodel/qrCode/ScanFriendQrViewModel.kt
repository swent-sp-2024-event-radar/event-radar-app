package com.github.se.eventradar.viewmodel.qrCode

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.repository.user.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// TODO ViewModel & UI can be improved on by having a state where the UI reflects a loading icon if
// firebase operations take a long time
// TODO ViewModel & UI can be improved to error message for each different type of Error ?

@HiltViewModel
class ScanFriendQrViewModel
@Inject
constructor(
    private val userRepository: IUserRepository, // Dependency injection
    val qrCodeAnalyser: QrCodeAnalyser, // Dependency injection
) : ViewModel() {
  private val _uiState = MutableStateFlow(QrCodeScanFriendState())
  val uiState: StateFlow<QrCodeScanFriendState> = _uiState

  init {
    viewModelScope.launch { setEnvironment() }
  }

  suspend fun setEnvironment() {
    val userId = userRepository.getCurrentUserId()
    if (userId is Resource.Success) {
      _uiState.update { it.copy(userId = userId.data) }
    } else {
      Log.d("currentIdNull", "Error getting user ID: Failure, is Null")
    }
  }

  fun setDecodedResultCallback(callback: ((String?) -> Unit)? = null) {
    qrCodeAnalyser.onDecoded = callback
  }

  fun updateFriendList(decodedString: String = _uiState.value.decodedResult): Boolean {
    val uiLength = 28
    val friendID = decodedString.take(uiLength)
    var successfulUpdate = true
    Log.d("QrCodeFriendViewModel", "Friend ID: $friendID")

    viewModelScope.launch {
      val friendUserDeferred = async { userRepository.getUser(friendID) }
      val currentUserDeferred = async { userRepository.getUser(_uiState.value.userId!!) }

      val friendUser = friendUserDeferred.await()
      val currentUser = currentUserDeferred.await()

      if (friendUser is Resource.Success && currentUser is Resource.Success) {
        val friendUpdatesDeferred = async {
          retryUpdate(friendUser.data!!, _uiState.value.userId!!)
        }
        val userUpdatesDeferred = async { retryUpdate(currentUser.data!!, friendID) }

        // Await both updates to complete successfully
        val friendUpdateResult = friendUpdatesDeferred.await()
        val userUpdateResult = userUpdatesDeferred.await()

        // After successful updates, navigate to the next screen
        if (!friendUpdateResult || !userUpdateResult) {
          //          Log.d("ScanFriendQrViewModel", "Failed to update user details")
          successfulUpdate = false
          return@launch
        }
      } else {
        val exception =
            (if (friendUser is Resource.Failure) friendUser.throwable
            else (currentUser as Resource.Failure).throwable)
        Log.d("ScanFriendQrViewModel", "Error fetching user details: ${exception.message}")
        successfulUpdate = false
        return@launch
      }
    }

    return successfulUpdate
  }

  suspend fun retryUpdate(user: User, friendIDToAdd: String): Boolean {
    var maxNumberOfRetries = 3
    var updateResult: Resource<Any>?
    do {
      updateResult =
          if (user.friendsList.contains(friendIDToAdd)) {
            Resource.Success(Unit)
          } else {
            user.friendsList.add(friendIDToAdd)
            when (userRepository.updateUser(user)) {
              is Resource.Success -> Resource.Success(Unit)
              is Resource.Failure -> Resource.Failure(Exception("Failed to update user"))
            }
          }
    } while ((updateResult !is Resource.Success) && (maxNumberOfRetries-- > 0))

    return updateResult is Resource.Success
  }

  fun onDecodedResultChanged(decodedResult: String) {
    _uiState.value = _uiState.value.copy(decodedResult = decodedResult)
  }

  fun changeTabState(tab: Tab) {
    _uiState.value = uiState.value.copy(tabState = tab)
  }

  fun getUserDetails() {
    viewModelScope.launch {
      when (val userIdResource = userRepository.getCurrentUserId()) {
        is Resource.Success -> {
          val userId = userIdResource.data
          // Now fetch the user data using the fetched user ID
          when (val userResult = userRepository.getUser(userId)) {
            is Resource.Success -> {
              _uiState.value =
                  _uiState.value.copy(
                      username = userResult.data!!.username, qrCodeLink = userResult.data.qrCodeUrl)
            }
            is Resource.Failure -> {
              Log.d(
                  "ScanFriendQrViewModel",
                  "Error fetching user details: ${userResult.throwable.message}")
            }
          }
        }
        is Resource.Failure -> {
          Log.d(
              "ScanFriendQrViewModel",
              "Error fetching user ID: ${userIdResource.throwable.message}")
        }
      }
    }
  }
}

enum class Tab {
  MyQR,
  ScanQR
}

data class QrCodeScanFriendState(
    val userId: String? = null,
    val decodedResult: String = "",
    val tabState: Tab = Tab.MyQR,
    val username: String = "",
    val qrCodeLink: String = "",
    val isLoading: Boolean = true, // Indicates loading state
)
