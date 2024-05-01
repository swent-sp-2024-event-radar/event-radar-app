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
          _action.value = Action.CantGetMyUID
        }
      }
    }
    qrCodeAnalyser.onDecoded = { decodedString ->
      val result = decodedString ?: "Failed to decode QR Code"
      _decodedResult.value = result // Update state flow
      println("I have been invoked with $decodedString")
      if (result != "Failed to decode QR Code") {
        println("entered the if block")
        updateFriendList(result) // Directly call updateFriendList
      } else {
        _action.value = Action.AnalyserError
      }
    }
    println("ViewModel init block executed")
  }

  fun updateFriendList(friendID: String) { // private
    println("Entered updateFriendList with: $friendID")
    viewModelScope.launch {
      val friendUserDeferred = async { userRepository.getUser(friendID) }
      val currentUserDeferred = async { userRepository.getUser(myUID) }

      val friendUser = friendUserDeferred.await()
      val currentUser = currentUserDeferred.await()

      println("Fetched user data from Firebase")

      // TODO what should be done if firebase is down? notify UI to display a message, retry after
      // some time, or just return?

      if (friendUser is Resource.Success && currentUser is Resource.Success) {
        val friendUpdatesDeferred = async { retryUpdate(friendUser.data!!, myUID) }
        val userUpdatesDeferred = async { retryUpdate(currentUser.data!!, friendID) }

        // Await both updates to complete successfully
        val friendUpdateResult = friendUpdatesDeferred.await()
        val userUpdateResult = userUpdatesDeferred.await()

        println("I have called retryUpdate with $friendID and $myUID")

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

  // need to change this based on coaches feedback, if i do while loop, should just return unit.
  // Utility function to retry updates until successful
  private suspend fun retryUpdate(user: User, friendIDToAdd: String): Boolean {
    println("entered retryUpdate with $friendIDToAdd")

    var updateResult: Resource<Any>?
    do {
      updateResult =
          if (!user.friendsSet.contains(friendIDToAdd)) {
            user.friendsSet.add(friendIDToAdd)
            when (userRepository.updateUser(user)) {
              is Resource.Success -> {
                println("updated user")
                Resource.Success(Unit)
              }
              is Resource.Failure -> {
                println("failed to update user")
                Resource.Failure(Exception("Failed to update user"))
              }
            }
          } else {
            println("already friends")
            Resource.Success(Unit) // No update needed, considers as success
          }
    } while (updateResult !is Resource.Success)
    return true
  }

  fun resetNavigationEvent() {
    _action.value = Action.None
  }

  fun changeTabState(tab: TAB) {
    _tabState.value = tab
  }
}
