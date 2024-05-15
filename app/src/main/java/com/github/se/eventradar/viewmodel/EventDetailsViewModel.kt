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

  init {
    getEventData()
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
  }

  private val _uiState = MutableStateFlow(EventUiState())
  val uiState: StateFlow<EventUiState> = _uiState

  val errorOccurred = mutableStateOf(false)
  val registrationSuccessful = mutableStateOf(false)

  private lateinit var currentUserId: String
  private var displayedEvent: Event? = null

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
          displayedEvent = response.data!!
        }
        is Resource.Failure ->
            Log.d("EventDetailsViewModel", "Error getting event: ${response.throwable.message}")
      }
    }
  }

  fun isTicketFree(): Boolean {
    return !(_uiState.value.ticket.price > 0.0)
  }

  fun buyTicketForEvent() { // update the user attendee list, update the eventRepo ticket count
    viewModelScope.launch {
      if (!isTicketFree()) {
        Log.d(
            "EventDetailsViewModel",
            "Paid tickets are not supported, all of them are considered free")
      }

      if (!isUserAttendingEvent()) {
        // TODO needs to be atomic to avoid concurrency issues
        registrationUpdateEvent()
        if (!errorOccurred.value) {
          registrationUpdateUser()
        }
      }
      if (!errorOccurred.value) {
        registrationSuccessful.value = true
      }
    }
  }

  fun isUserAttendingEvent(): Boolean {
    if (displayedEvent == null) {
      getEventData()
    }
    return displayedEvent?.attendeeList?.contains(currentUserId) ?: false
  }

  private fun registrationUpdateEvent() {
    if (displayedEvent != null) {
      val event: Event = displayedEvent as Event

      // add currentUserId to the event attendees list
      event.attendeeList.add(currentUserId)

      // decrement ticket capacity
      if (event.ticket.capacity > 0) {
        event.ticket = EventTicket(event.ticket.name, event.ticket.price, event.ticket.capacity - 1)

        viewModelScope.launch {
          // update event data to the database
          when (val updateResponse = eventRepository.updateEvent(event)) {
            is Resource.Success -> {
              Log.i("EventDetailsViewModel", "Successfully updated event")
            }
            is Resource.Failure -> {
              errorOccurred.value = true
              Log.d(
                  "EventDetailsViewModel",
                  "Error updating event data: ${updateResponse.throwable.message}")
            }
          }
        }
      } else {
        errorOccurred.value = true
        Log.d("EventDetailsViewModel", "Error updating event data: No more tickets !}")
      }
    } else {
      errorOccurred.value = true
      Log.d("EventDetailsViewModel", "Error no such event}")
    }
  }

  private fun registrationUpdateUser() {
    viewModelScope.launch {
      // fetch current user data
      when (val userResponse = userRepository.getUser(currentUserId)) {
        is Resource.Success -> {
          val currentUser = userResponse.data
          if (currentUser == null) {
            Log.d("EventDetailsViewModel", "No existing users")
          } else {
            // adding eventId to current user attended event list
            currentUser.eventsAttendeeList.add(eventId)

            // update user data to the database
            when (val updateResponse = userRepository.updateUser(currentUser)) {
              is Resource.Success -> {
                Log.i("EventDetailsViewModel", "Successfully updated user")
              }
              is Resource.Failure -> {
                errorOccurred.value = true
                Log.d(
                    "EventDetailsViewModel",
                    "Error updating user data: ${updateResponse.throwable.message}")
              }
            }
          }
        }
        is Resource.Failure -> {
          errorOccurred.value = true
          Log.d(
              "EventDetailsViewModel", "Error getting user data: ${userResponse.throwable.message}")
        }
      }
    }
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
