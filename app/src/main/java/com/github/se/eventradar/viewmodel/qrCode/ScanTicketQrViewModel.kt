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
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ScanTicketQrViewModel
@Inject // Dependency injection
constructor(
    private val userRepository: IUserRepository,
    private val eventRepository: IEventRepository,
    val qrCodeAnalyser: QrCodeAnalyser,
) : ViewModel() {

  private val _uiState = MutableStateFlow(QrCodeScanTicketState())
  val uiState: StateFlow<QrCodeScanTicketState> = _uiState

  enum class Action {
    ScanTicket,
    ApproveEntry,
    DenyEntry,
    FirebaseUpdateError,
    FirebaseFetchError,
    AnalyserError,
  }

  enum class Tab {
    MyEvent,
    ScanQr
  }

  private var myEventID: String? = null

  init {
    qrCodeAnalyser.onTicketDecoded = { decodedString ->
      val result = decodedString ?: "Failed to decode QR Code"
      updateDecodedString(result) // Update state flow
      if (result != "Failed to decode QR Code") {
        //        println("correctly decoded")
        updatePermissions(result) // Directly call updateFriendList
      } else {
        //      println("wrongly decoded")
        changeAction(Action.AnalyserError)
        //        println("checkpoint 2")
      }
    }
  }

  private fun updatePermissions(decodedString: String) {
    while (myEventID == null) {
      // Wait until myEventID is not null
    }
    println("entered updatePermissions")
    val uiLength = 28
    val attendeeID = decodedString.take(uiLength)
    Log.d("QrCodeFriendViewModel", "Ticket User ID: $attendeeID")

    viewModelScope.launch {
      val attendeeUserDeferred = async { userRepository.getUser(attendeeID) }
      val currentEventDeferred = async { eventRepository.getEvent(myEventID!!) }

      val attendeeUser = attendeeUserDeferred.await()
      val currentEvent = currentEventDeferred.await()

      if (attendeeUser is Resource.Success && currentEvent is Resource.Success) {
        retryUpdate(attendeeUser.data!!, currentEvent.data!!)
      } else {
        changeAction(Action.FirebaseFetchError)
        return@launch
      }
    }
  }

  private suspend fun retryUpdate(user: User, event: Event) {
    println("entered retryUpdate")
    var maxNumberOfRetries = 3
    var updateResult: Resource<Any>?
    if (user.eventsAttendeeSet.contains(myEventID) && event.attendeeSet.contains(user.userId)) {
      println("both contain one another")
      user.eventsAttendeeSet.remove(myEventID)
      val userUpdateResult = userRepository.updateUser(user)
      event.attendeeSet.remove(user.userId)
      val eventUpdateResult = eventRepository.updateEvent(event)

      do {
        // Check if both updates were successful
        updateResult =
            if (userUpdateResult is Resource.Success && eventUpdateResult is Resource.Success) {
              changeAction(Action.ApproveEntry)
              Resource.Success(Unit)
            } else {
              changeAction(Action.FirebaseUpdateError)
              Resource.Failure(Exception("Failed to update user and event"))
            }
      } while ((updateResult !is Resource.Success) && (maxNumberOfRetries-- > 0))
    } else {
      println("one does not contain the other")
      changeAction(Action.DenyEntry)
    }
  }

  private fun updateDecodedString(result: String) {
    _uiState.value = _uiState.value.copy(decodedResult = result)
  }

  fun saveEventID(eventID: String) {
    myEventID = eventID
  }

  fun changeTabState(tab: Tab) {
    _uiState.value = _uiState.value.copy(tabState = tab)
  }

  fun changeAction(action: Action) {
    println("Changing action to $action")
    _uiState.value = _uiState.value.copy(action = action)
  }

  fun resetConditions() {
    changeAction(Action.ScanTicket)
    qrCodeAnalyser.onTicketDecoded = null
    _uiState.value = _uiState.value.copy(decodedResult = "")
    _uiState.value = _uiState.value.copy(action = Action.ScanTicket)
    changeTabState(Tab.MyEvent)
  }

  data class QrCodeScanTicketState(
      val decodedResult: String = "uuu",
      val action: Action = Action.ScanTicket,
      val tabState: Tab = Tab.MyEvent
  )
}
