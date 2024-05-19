package com.github.se.eventradar.viewmodel.qrCode

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// TODO ViewModel & UI can be improved on by having a state where the UI reflects a loading icon if
// firebase operations take a long time
// TODO ViewModel & UI can be improved to error message for each different type of Error ?

@HiltViewModel(assistedFactory = ScanFriendQrViewModel.Factory::class)
class ScanFriendQrViewModel
@AssistedInject
constructor(
    private val userRepository: IUserRepository, // Dependency injection
    val qrCodeAnalyser: QrCodeAnalyser, // Dependency injection
    @Assisted private val navigationActions: NavigationActions
) : ViewModel() {

  @AssistedFactory
  interface Factory {
    fun create(navigationActions: NavigationActions): ScanFriendQrViewModel
  }

  companion object {
    @Composable
    fun create(navigationActions: NavigationActions): ScanFriendQrViewModel {
      return hiltViewModel<ScanFriendQrViewModel, Factory>(
          creationCallback = { factory -> factory.create(navigationActions = navigationActions) })
    }
  }

  enum class Action {
    None,
    NavigateToNextScreen,
    FirebaseFetchError,
    FirebaseUpdateError,
    AnalyserError,
    CantGetMyUID
  }

  enum class Tab {
    MyQR,
    ScanQR
  }

  data class QrCodeScanFriendState(
      val decodedResult: String = "",
      val action: Action = Action.None,
      val tabState: Tab = Tab.MyQR,
      val username: String = "",
      val qrCodeLink: String = "",
      val isLoading: Boolean = true, // Indicates loading state
  )

  var myUID: String = ""

  private val _uiState = MutableStateFlow(QrCodeScanFriendState())
  val uiState: StateFlow<QrCodeScanFriendState> = _uiState

  //  private val _uiState = MutableStateFlow(QrCodeScanFriendState())
  //  val uiState: StateFlow<QrCodeScanFriendState> = _uiState

  val initialUiState: StateFlow<QrCodeScanFriendState> =
      flow {
            emit(QrCodeScanFriendState(isLoading = true))

            when (val userIdResult = userRepository.getCurrentUserId()) {
              is Resource.Success -> {
                val getMyUID = userIdResult.data
                emit(QrCodeScanFriendState(isLoading = true))
                myUID = getMyUID
              }
              is Resource.Failure -> {
                emit(QrCodeScanFriendState(isLoading = false, action = Action.CantGetMyUID))
              }
            }
            val decodedResult =
                callbackFlow {
                      qrCodeAnalyser.onDecoded = { decodedString ->
                        trySend(decodedString ?: "Failed to decode QR Code")
                        close()
                      }
                      awaitClose { qrCodeAnalyser.onDecoded = null }
                    }
                    .first()

            if (decodedResult == "Failed to decode QR Code") {
              emit(
                  QrCodeScanFriendState(
                      isLoading = false,
                      action = Action.AnalyserError,
                      decodedResult = decodedResult))
            } else {
              emit(QrCodeScanFriendState(isLoading = false, decodedResult = decodedResult))
              updateFriendList(decodedResult)
            }
          }
          .stateIn(
              viewModelScope,
              SharingStarted.WhileSubscribed(5000),
              QrCodeScanFriendState(isLoading = true))

  init {
    viewModelScope.launch { initialUiState.collect { newState -> _uiState.value = newState } }
  }

  //    qrCodeAnalyser.onDecoded = { decodedString ->
  //      Log.d("QrCodeFriendViewModel", "Decoded QR Code: $decodedString")
  //      if(decodedString != null) {
  //        emit(QrCodeScanFriendState(isLoading = false))
  //        updateFriendList(decodedString)
  //      } else {
  //        emit(QrCodeScanFriendState(isLoading = false, action = Action.AnalyserError))
  //      }
  //    }
  //  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),
  // QrCodeScanFriendState(isLoading = true))

  //  init {
  //    viewModelScope.launch {
  //      when (userRepository.getCurrentUserId()) {
  //        is Resource.Success -> {
  //          myUID = (userRepository.getCurrentUserId() as Resource.Success<String>).data
  //        }
  //        is Resource.Failure -> {
  //          changeAction(Action.CantGetMyUID)
  //        }
  //      }
  //    }
  //    qrCodeAnalyser.onDecoded = { decodedString ->
  //      Log.d("QrCodeFriendViewModel", "Decoded QR Code: $decodedString")
  //      val result = decodedString ?: "Failed to decode QR Code"
  //      _uiState.value = _uiState.value.copy(decodedResult = result) // Update state flow
  //      //      _decodedResult.value = result // Update state flow
  //      if (result != "Failed to decode QR Code") {
  //        updateFriendList(result) // Directly call updateFriendList
  //      } else {
  //        changeAction(Action.AnalyserError)
  //      }
  //    }
  //  }

  private fun updateFriendList(decodedString: String) { // private
    val uiLength = 28
    val friendID = decodedString.take(uiLength)
    Log.d("QrCodeFriendViewModel", "Friend ID: $friendID")

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

  fun changeAction(action: Action) {
    _uiState.value = uiState.value.copy(action = action)
    if (action == Action.NavigateToNextScreen) {
      navigationActions.navController.navigate(
          "${Route.PRIVATE_CHAT}/${_uiState.value.decodedResult}") // TODO check correct
      _uiState.value = _uiState.value.copy(action = Action.None)
      changeTabState(Tab.MyQR) // TODO add test for this
    }
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
