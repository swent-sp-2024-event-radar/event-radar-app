package com.github.se.eventradar.viewmodel.qrCode

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.repository.event.IEventRepository
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.github.se.eventradar.viewmodel.EventUiState
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ScanTicketQrViewModel.Factory::class)
class ScanTicketQrViewModel
@AssistedInject
constructor(
    private val userRepository: IUserRepository,
    private val eventRepository: IEventRepository,
    val qrCodeAnalyser: QrCodeAnalyser,
    @Assisted private val myEventID: String
) : ViewModel() {

  @AssistedFactory
  interface Factory {
    fun create(eventId: String): ScanTicketQrViewModel
  }

  companion object {
    @Composable
    fun create(eventId: String): ScanTicketQrViewModel {
      return hiltViewModel<ScanTicketQrViewModel, Factory>(
          creationCallback = { factory -> factory.create(eventId = eventId) })
    }
  }

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

  private val _uiState = MutableStateFlow(QrCodeScanTicketState())
  val uiState: StateFlow<QrCodeScanTicketState> = _uiState

  private val initialUiState: StateFlow<ScanTicketQrViewModel.QrCodeScanTicketState> =
      flow {
            val decodedResult =
                callbackFlow {
                      qrCodeAnalyser.onDecoded = { decodedString ->
                        trySend(decodedString)
                        close()
                      }
                      awaitClose { qrCodeAnalyser.onDecoded = null }
                    }
                    .first()
            if (decodedResult != null) {
              updatePermissions(decodedResult)
              emit(QrCodeScanTicketState(isLoading = false, decodedResult = decodedResult))
            } else {
              emit(QrCodeScanTicketState(isLoading = false, action = Action.AnalyserError))
            }
            getEventData()
          }
          .stateIn(
              viewModelScope,
              SharingStarted.WhileSubscribed(5000),
              QrCodeScanTicketState(isLoading = true))

  init {
    viewModelScope.launch { initialUiState.collect { newState -> _uiState.value = newState } }
  }

  private fun updatePermissions(attendeeID: String) {
    Log.d("QrCodeTicketViewModel", "Ticket User ID: $attendeeID")
    viewModelScope.launch {
      val attendeeUserDeferred = async { userRepository.getUser(attendeeID) }
      val currentEventDeferred = async { eventRepository.getEvent(myEventID) }

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
    if (user.eventsAttendeeList.contains(myEventID) && event.attendeeList.contains(user.userId)) {
      println("both contain one another")
      user.eventsAttendeeList.remove(myEventID)
      val userUpdateResult = userRepository.updateUser(user)
      event.attendeeList.remove(user.userId)
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
      Log.d("error one does not contain the other", "one does not contain the other")
      changeAction(Action.DenyEntry)
    }
  }

  private fun updateDecodedString(result: String) {
    _uiState.update { it.copy(decodedResult = result) }
  }

  fun resetConditions() {
    qrCodeAnalyser.onDecoded = null
    changeAction(Action.ScanTicket)
    _uiState.update { it.copy(decodedResult = "") }
    changeTabState(Tab.MyEvent)
  }

  fun changeTabState(tab: Tab) {
    _uiState.update { it.copy(tabState = tab) }
  }

  fun changeAction(action: Action) {
    _uiState.update { it.copy(action = action) }
  }

  fun getEventData() {
    viewModelScope.launch {
      when (val response = eventRepository.getEvent(myEventID)) {
        is Resource.Success -> {
          _uiState.update {
            it.copy(
                eventUiState =
                    EventUiState(
                        eventName = response.data!!.eventName,
                        eventPhoto = response.data.eventPhoto,
                        start = response.data.start,
                        end = response.data.end,
                        location = response.data.location,
                        description = response.data.description,
                        ticket = response.data.ticket,
                        mainOrganiser = response.data.mainOrganiser,
                        category = response.data.category))
          }
        }
        is Resource.Failure ->
            Log.d("EventDetailsViewModel", "Error getting event: ${response.throwable.message}")
      }
    }
  }

  data class QrCodeScanTicketState(
      val isLoading: Boolean = true,
      val decodedResult: String = "",
      val action: Action = Action.ScanTicket,
      val tabState: Tab = Tab.MyEvent,
      val eventUiState: EventUiState = EventUiState()
  )
}
