package com.github.se.eventradar.viewmodel.qrCode

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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// TODO ViewModel & UI can be improved on by having a state where the UI reflects a loading icon if
// firebase operations take a long time
// TODO ViewModel & UI can be improved to error message for each different type of Error ?

@HiltViewModel
class QrCodeFriendViewModel
@Inject
constructor(
    private val userRepository: IUserRepository, // Dependency injection
    val qrCodeAnalyser: QrCodeAnalyser, // Dependency injection
) : ViewModel() {

  enum class Action {
    None,
    NavigateToNextScreen,
    FirebaseFetchError,
    FirebaseUpdateError,
    AnalyserError,
    CantGetMyUID
  }

  enum class TAB {
    MyQR,
    ScanQR
  }

  private var myUID = ""

  private val _decodedResult = MutableStateFlow<String?>(null)
  val decodedResult: StateFlow<String?> = _decodedResult.asStateFlow()

  private val _action = MutableStateFlow(Action.None)
  val action: StateFlow<Action> = _action.asStateFlow()

  private val _tabState = MutableStateFlow(TAB.MyQR)
  val tabState: StateFlow<TAB> = _tabState.asStateFlow()

  init {
    viewModelScope.launch {
      when (userRepository.getCurrentUserId()) {
        is Resource.Success -> {
          myUID = (userRepository.getCurrentUserId() as Resource.Success<String>).data
        }
        is Resource.Failure -> {
          changeAction(Action.CantGetMyUID)
        }
      }
    }
    qrCodeAnalyser.onDecoded = { decodedString ->
      val result = decodedString ?: "Failed to decode QR Code"
      _decodedResult.value = result // Update state flow
      if (result != "Failed to decode QR Code") {
        updateFriendList(result) // Directly call updateFriendList
      } else {
        changeAction(Action.AnalyserError)
      }
    }
  }

  private fun updateFriendList(friendID: String) { // private

    viewModelScope.launch {
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
          changeAction(Action.NavigateToNextScreen)
        } else {
          changeAction(Action.FirebaseUpdateError)
        }
      } else {
        changeAction(Action.FirebaseFetchError)
        return@launch
      }
    }
  }

  private suspend fun retryUpdate(user: User, friendIDToAdd: String): Boolean {
    var maxNumberOfRetries = 3
    var updateResult: Resource<Any>?
    do {
      updateResult =
          if (user.friendsSet.contains(friendIDToAdd)) {
            Resource.Success(Unit)
          } else {
            user.friendsSet.add(friendIDToAdd)
            when (userRepository.updateUser(user)) {
              is Resource.Success -> Resource.Success(Unit)
              is Resource.Failure -> Resource.Failure(Exception("Failed to update user"))
            }
          }
    } while ((updateResult !is Resource.Success) && (maxNumberOfRetries-- > 0))

    return updateResult is Resource.Success
  }

  fun resetNavigationEvent() {
    _action.value = Action.None
  }

  fun changeTabState(tab: TAB) {
    _tabState.value = tab
  }

  fun changeAction(action: Action) {
    _action.value = action
  }
}
