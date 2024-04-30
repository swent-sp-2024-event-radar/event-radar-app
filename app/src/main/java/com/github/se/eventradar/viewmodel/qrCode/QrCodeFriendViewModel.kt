package com.github.se.eventradar.viewmodel.qrCode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

enum class NavigationEvent {
  None,
  NavigateToNextScreen
}

enum class TAB {
  MyQR,
  ScanQR
}
// ViewModel & UI can be improved on by having a state where the UI reflects a loading icon if
// firebase operations take a long time
// would also need to handle the case where the updates really do fail within 1 minute's time.
// personally gonna make this extra.

@HiltViewModel
class QrCodeFriendViewModel
@Inject
constructor(
  private val userRepository: IUserRepository, // Dependency injection
  val qrCodeAnalyser: QrCodeAnalyser // Dependency injection
) : ViewModel() {

  private val _decodedResult = MutableStateFlow<String?>(null)

  private val _navigationEvent = MutableStateFlow(NavigationEvent.None)
  val navigationEvent: StateFlow<NavigationEvent> = _navigationEvent.asStateFlow()

  private val _tabState = MutableStateFlow(TAB.MyQR.ordinal)
  val tabState: StateFlow<Int> = _tabState.asStateFlow()

  init {
    qrCodeAnalyser.onDecoded = { decodedString ->
      _decodedResult.value = decodedString ?: "Failed to decode QR Code"
    }

    viewModelScope.launch {
      _decodedResult
          .filterNotNull() // Only continue if the value is not null
          .collect { result ->
            if (result != "Failed to decode QR Code") {
              updateFriendList(result)
            }
          }
    }
  }

  private fun updateFriendList(friendID: String) {
    viewModelScope.launch {
      val myUID = FirebaseAuth.getInstance().currentUser!!.uid

      val friendUserDeferred = async { userRepository.getUser(friendID) }
      val currentUserDeferred = async { userRepository.getUser(myUID) }

      val friendUser = friendUserDeferred.await()
      val currentUser = currentUserDeferred.await()

      if (friendUser is Resource.Success && currentUser is Resource.Success) {
        val friendUpdatesDeferred = async { retryUpdate(friendUser.data!!, myUID) }
        val userUpdatesDeferred = async { retryUpdate(currentUser.data!!, friendID) }

        // Await both updates to complete successfully
        val friendUpdateResult = friendUpdatesDeferred.await()
        val userUpdateResult = userUpdatesDeferred.await()

        // After successful updates, navigate to the next screen
        if (friendUpdateResult && userUpdateResult) {
          _navigationEvent.value = NavigationEvent.NavigateToNextScreen
        } else {
          println("Failed to update user data to Firebase")
        }
      } else {
        println("Failed to fetch user data from Firebase")
      }
    }
  }

  // Utility function to retry updates until successful
  private suspend fun retryUpdate(user: User, friendIDToAdd: String): Boolean {
    var updateResult: Resource<Any>?
    val timeoutDuration = 10000L // Overall timeout duration in milliseconds for all retries

    val result =
        withTimeoutOrNull(timeoutDuration) {
          do {
            updateResult =
                if (!user.friendsSet.contains(friendIDToAdd)) {
                  userRepository.updateUser(user)
                } else {
                  Resource.Success(Unit) // No update needed, considers as success
                }
            if (updateResult is Resource.Failure) {
              delay(1000) // Wait for a second before retrying to avoid overloading the server
            }
          } while (updateResult !is Resource.Success)

          updateResult
        }

    return result is Resource.Success
  }

  fun resetNavigationEvent() {
    _navigationEvent.value = NavigationEvent.None
  }
  fun changeTabState(tab: Int) {
    _tabState.value = tab
  }

}
