package com.github.se.eventradar.viewmodel.qrCode

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.repository.event.IEventRepository
import com.github.se.eventradar.model.repository.user.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanTicketQrViewModel
@Inject// Dependency injection
constructor(
    private val userRepository: IUserRepository,
    private val eventRepository: IEventRepository,
    val qrCodeAnalyser: QrCodeAnalyser,
) : ViewModel() {

    private val _uiState = MutableStateFlow(QrCodeScanTicketState())
    val uiState: StateFlow<QrCodeScanTicketState> = _uiState


    enum class Action {
        None,
        NavigateToNextScreen,
        FirebaseFetchError,
        FirebaseUpdateError,
        AnalyserError,
        CantGetMyUID
    }

    enum class Tab {
        None,
        NavigateToNextScreen,
        FirebaseFetchError,
        FirebaseUpdateError,
        AnalyserError,
        CantGetMyUID
    }

    private var myEventID = ""
    fun saveEventID(eventID: String) {
        myEventID = eventID
    }

    init {
        qrCodeAnalyser.onTicketDecoded = { decodedString ->
            Log.d("QrCodeFriendViewModel", "Decoded QR Code: $decodedString")
            val result = decodedString ?: "Failed to decode QR Code (Ticket Use)"
            updateDecodedString(result) // Update state flow
            if (result != "Failed to decode QR Code") {
                updatePermissions(result) // Directly call updateFriendList
            } else {
                changeAction(ScanFriendQrViewModel.Action.AnalyserError)
            }
        }
    }

    private fun updatePermissions(decodedString: String) {
    val uiLength = 28
    val attendeeID = decodedString.take(uiLength)
    Log.d("QrCodeFriendViewModel", "Ticket User ID: $attendeeID")

    viewModelScope.launch {
        val attendeeUserDeferred = async { userRepository.getUser(attendeeID)}
        val currentEventDeferred = async { eventRepository.getEvent(myEventID)}

        val attendeeUser = attendeeUserDeferred.await()
        val currentEvent = currentEventDeferred.await()

        if (attendeeUser is Resource.Success && currentEvent is Resource.Success) {
            val PermissionUpdatesDeferred = async { retryUpdate(attendeeUser.data!!, currentEvent.data!!)}

            // Await both updates to complete successfully
            val PermissionUpdateResult = PermissionUpdatesDeferred.await()


            // After successful updates, navigate to the next screen
            if (PermissionUpdateResult && userUpdateResult) {
                changeAction(ScanFriendQrViewModel.Action.NavigateToNextScreen)
            } else {
                changeAction(ScanFriendQrViewModel.Action.FirebaseUpdateError)
            }
        } else {
            changeAction(ScanFriendQrViewModel.Action.FirebaseFetchError)
            return@launch
        }
    }
}

    private suspend fun retryUpdate(user: User, event: Event): Boolean {
        var maxNumberOfRetries = 3
        var updateResult: Resource<Any>?
        do {
            updateResult =
                    if ((user.eventsAttendeeSet.contains(myEventID))
                        &&
                        (event.attendeeSet.contains(user.userId))) {
                        Resource.Success(Unit)
                    } else {
                        user.eventsAttendeeSet.add(event.id)
                        when (userRepository.updateUser(user)) {
                            is Resource.Success -> Resource.Success(Unit)
                            is Resource.Failure -> Resource.Failure(Exception("Failed to update user"))
                        }
                    }
                    user.friendsSet.add(friendIDToAdd)
                    when (userRepository.updateUser(user)) {
                        is Resource.Success -> Resource.Success(Unit)
                        is Resource.Failure -> Resource.Failure(Exception("Failed to update user"))

                }
        } while ((updateResult !is Resource.Success) && (maxNumberOfRetries-- > 0))

        return updateResult is Resource.Success
    }

        fun updateDecodedString(result: String) {
        _uiState.value = _uiState.value.copy(decodedResult = result)
    }

    fun changeAction(action: ScanFriendQrViewModel.Action) {
        _uiState.value = _uiState.value.copy(action = action)
    }
    data class QrCodeScanTicketState(
        val decodedResult: String = "",
        val action: ScanFriendQrViewModel.Action = ScanFriendQrViewModel.Action.None,
        val tabState: ScanFriendQrViewModel.Tab = ScanFriendQrViewModel.Tab.MyQR
    )
}


}