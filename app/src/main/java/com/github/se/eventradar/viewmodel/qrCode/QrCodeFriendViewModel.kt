package com.github.se.eventradar.viewmodel.qrCode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.repository.user.IUserRepository
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

// ViewModel & UI can be improved on by having a state where the UI reflects a loading icon if
// firebase operations take a long time
// would also need to handle the case where the updates really do fail within 1 minute's time.
// personally gonna make this extra.

// TODO what should be done if analyser returns a null ... how do we make it trey again

@HiltViewModel
class QrCodeFriendViewModel
@Inject
constructor(
    private val userRepository: IUserRepository, // Dependency injection
    val qrCodeAnalyser: QrCodeAnalyser, // Dependency injection
    private val myUID: String
) : ViewModel() {

  enum class Action {
    None,
    NavigateToNextScreen,
    FirebaseFetchError,
    FirebaseUpdateError,
    AnalyserError
  }

  enum class TAB {
    MyQR,
    ScanQR // TODO
  }

  private val _decodedResult = MutableStateFlow<String?>(null)
  val decodedResult: StateFlow<String?> = _decodedResult.asStateFlow()

  private val _action = MutableStateFlow(Action.None)
  val action: StateFlow<Action> = _action.asStateFlow()

  private val _tabState = MutableStateFlow(TAB.MyQR)
  val tabState: StateFlow<TAB> = _tabState.asStateFlow()

  //  val myUID = FirebaseAuth.getInstance().currentUser!!.uid

  init {
    qrCodeAnalyser.onDecoded = { decodedString ->
      _decodedResult.value = decodedString ?: "Failed to decode QR Code"
    }

    viewModelScope.launch {
      _decodedResult
          .filterNotNull() // Only continue if the value is not null
          .collect { result ->
            if (result == "Failed to decode QR Code") {
              _action.value = Action.AnalyserError
            } else {
              updateFriendList(result)
            }
          }
    }
  }

  fun updateFriendList(friendID: String) { // private
    viewModelScope.launch {
      val friendUserDeferred = async { userRepository.getUser(friendID) }
      val currentUserDeferred = async { userRepository.getUser(myUID) }

      val friendUser = friendUserDeferred.await()
      val currentUser = currentUserDeferred.await()

      // TODO what should be done if firebase is down? notify UI to display a message, retry after
      // some time, or just return?

      if (friendUser is Resource.Success && currentUser is Resource.Success) {
        val friendUpdatesDeferred = async { retryUpdate(friendUser.data!!, myUID) }
        val userUpdatesDeferred = async { retryUpdate(currentUser.data!!, friendID) }

        // Await both updates to complete successfully
        val friendUpdateResult = friendUpdatesDeferred.await()
        val userUpdateResult = userUpdatesDeferred.await()

        // After successful updates, navigate to the next screen
        if (friendUpdateResult && userUpdateResult) {
          _action.value = Action.NavigateToNextScreen
        } else {

          _action.value = Action.FirebaseUpdateError
        }
      } else {
        _action.value = Action.FirebaseFetchError
        println("Failed to fetch user data from Firebase")
        return@launch
      }
    }
  }

  // Utility function to retry updates until successful
  private suspend fun retryUpdate(user: User, friendIDToAdd: String): Boolean {
    var updateResult: Resource<Any>?
    val timeoutDuration =
        10000L // Overall timeout duration in milliseconds for all retries (10 seconds)

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

    return result is Resource.Success // returns TRUE if the update was successful else FALSE
  }

  fun resetNavigationEvent() {
    _action.value = Action.None
  }

  fun changeTabState(tab: TAB) {
    _tabState.value = tab
  }
}
