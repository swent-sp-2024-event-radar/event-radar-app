package com.github.se.eventradar.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventTicket
import com.github.se.eventradar.model.repository.event.IEventRepository
import com.github.se.eventradar.model.repository.user.IUserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = EventDetailsViewModel.Factory::class)
class EventDetailsViewModel
@AssistedInject
constructor(
    private val eventRepository: IEventRepository,
    private val userRepository: IUserRepository,
    @Assisted val eventId: String,
) : ViewModel() {

  private val _uiState = MutableStateFlow(EventUiState())
  val uiState: StateFlow<EventUiState> = _uiState

  private val _isUserAttending = MutableStateFlow(false)
  val isUserAttending: StateFlow<Boolean>
    get() = _isUserAttending

  val showErrorOccurredDialog = mutableStateOf(false)
  val showSuccessfulRegistrationDialog = mutableStateOf(false)
  val showCancelRegistrationDialog = mutableStateOf(false)

  private lateinit var currentUserId: String
  private var displayedEvent: Event? = null

  init {
    viewModelScope.launch {
      when (val response = userRepository.getCurrentUserId()) {
        is Resource.Success -> {
          currentUserId = response.data
        }
        is Resource.Failure ->
            Log.d(
                "EventDetailsViewModel", "Could not get the user Id: ${response.throwable.message}")
      }
    }
    getEventData()
  }

  fun getEventData() {
    viewModelScope.launch {
      when (val response = eventRepository.getEvent(eventId)) {
        is Resource.Success -> {
          _uiState.update {
            it.copy(
                eventName = response.data!!.eventName,
                eventPhoto = response.data.eventPhoto,
                start = response.data.start,
                end = response.data.end,
                location = response.data.location,
                description = response.data.description,
                ticket = response.data.ticket,
                mainOrganiser = response.data.mainOrganiser,
                category = response.data.category,
            )
          }
          displayedEvent = response.data
          _isUserAttending.update { response.data!!.attendeeList.contains(currentUserId) }
        }
        is Resource.Failure ->
            Log.d("EventDetailsViewModel", "Error getting event: ${response.throwable.message}")
      }
    }
  }

  fun isTicketFree(): Boolean {
    return !(_uiState.value.ticket.price > 0.0)
  }

  fun refreshAttendance() {
    getEventData()
  }

  fun buyTicketForEvent() {
    viewModelScope.launch {
      if (!isTicketFree()) {
        Log.d(
            "EventDetailsViewModel",
            "Paid tickets are not supported, all of them are considered free")
      }

      if (!_isUserAttending.value) {
        if (registrationProcedure(eventId, currentUserId)) {
          showSuccessfulRegistrationDialog.value = true
          _isUserAttending.update { true }
        } else {
          showErrorOccurredDialog.value = true
        }
      }
    }
  }

  fun removeUserFromEvent() {
    viewModelScope.launch {
      if (unregistrationProcedure(eventId, currentUserId)) {
        _isUserAttending.update { false }
        showCancelRegistrationDialog.value = false
      } else {
        showErrorOccurredDialog.value = true
      }
    }
  }

  private suspend fun unregistrationProcedure(eventId: String, userId: String): Boolean {
    // is atomic but TODO no checks for consistency between these 3 values

    val resEvent = eventRepository.removeAttendee(eventId, userId)
    if (resEvent is Resource.Failure) {
      Log.d(
          "EventDetailsViewModel",
          "Error removing attendee in event: ${resEvent.throwable.message}")
    }
    val resPurchases = eventRepository.decrementPurchases(eventId)
    if (resPurchases is Resource.Failure) {
      Log.d(
          "EventDetailsViewModel",
          "Error decrementing purchases: ${resPurchases.throwable.message}")
    }
    val resUser = userRepository.removeEventFromAttendeeList(userId, eventId)
    if (resUser is Resource.Failure) {
      Log.d(
          "EventDetailsViewModel",
          "Error removing attendance in user: ${resUser.throwable.message}")
    }

    return (resEvent is Resource.Success &&
        resPurchases is Resource.Success &&
        resUser is Resource.Success)
  }

  private suspend fun registrationProcedure(eventId: String, userId: String): Boolean {
    // is atomic but TODO no checks for consistency between these 3 values

    val resEvent = eventRepository.addAttendee(eventId, userId)
    if (resEvent is Resource.Failure) {
      Log.d(
          "EventDetailsViewModel", "Error adding attendee in event: ${resEvent.throwable.message}")
    }
    val resPurchases = eventRepository.incrementPurchases(eventId)
    if (resPurchases is Resource.Failure) {
      Log.d(
          "EventDetailsViewModel",
          "Error incrementing purchases: ${resPurchases.throwable.message}")
    }
    val resUser = userRepository.addEventToAttendeeList(userId, eventId)
    if (resUser is Resource.Failure) {
      Log.d(
          "EventDetailsViewModel", "Error adding attendance in user: ${resUser.throwable.message}")
    }

    return (resEvent is Resource.Success &&
        resPurchases is Resource.Success &&
        resUser is Resource.Success)
  }

  // Code for creating an instance of EventDetailsViewModel
  @AssistedFactory
  interface Factory {
    fun create(eventId: String): EventDetailsViewModel
  }

  companion object {
    @Composable
    fun create(eventId: String): EventDetailsViewModel {
      return hiltViewModel<EventDetailsViewModel, Factory>(
          creationCallback = { factory -> factory.create(eventId = eventId) })
    }
  }
}

data class EventUiState(
    val eventName: String = "",
    val eventPhoto: String = "",
    val start: LocalDateTime = LocalDateTime.MIN,
    val end: LocalDateTime = LocalDateTime.MAX,
    val location: Location = Location(0.0, 0.0, ""),
    val description: String = "",
    val ticket: EventTicket = EventTicket("", 0.0, 0, 0),
    val mainOrganiser: String = "",
    val category: EventCategory = EventCategory.MUSIC,
)
