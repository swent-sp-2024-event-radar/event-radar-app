package com.github.se.eventradar.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventTicket
import com.github.se.eventradar.model.repository.event.IEventRepository
import com.github.se.eventradar.model.repository.user.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EventDetailsViewModel
@Inject
constructor(
    private val eventRepository: IEventRepository,
    private val userRepository: IUserRepository,
) : ViewModel() {

  private val _uiState = MutableStateFlow(EventUiState())
  val uiState: StateFlow<EventUiState> = _uiState

  val errorOccurred = mutableStateOf(false)
  val registrationSuccessful = mutableStateOf(false)

  // TODO would require assisted data injection to have this as a parameters
  private lateinit var eventId: String

  private lateinit var currentUserId: String
  private lateinit var displayedEvent: Event

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
  }

  fun saveEventId(eventId: String) {
    this.eventId = eventId
  }

  fun getEventId(): String {
    return this.eventId
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

  /*TODO actually, I had to recreate a view model when switching to the
   * buy ticket screen so that function is not needed anymore...
   */
  fun getTickets(): Unit { // update ticket, while buying ticket give user 5 minutes to buy?
    viewModelScope.launch {
      when (val response = eventRepository.getEvent(eventId)) {
        is Resource.Success -> {
          _uiState.update { it.copy(ticket = response.data!!.ticket) }
        }
        is Resource.Failure ->
            Log.d(
                "EventDetailsViewModel",
                "Error getting ticket for event: ${response.throwable.message}")
      }
    }
  }

  // TODO needs to be atomic to avoid concurrency issues
  fun buyTicketForEvent() { // update the user attendee list, update the eventRepo ticket count
    viewModelScope.launch {
      if (!isTicketFree()) {
        Log.d(
            "EventDetailsViewModel",
            "Paid tickets are not supported, all of them are considered free")
      }

      if(!isUserRegistered()){
        // TODO would need an atomic update of the database, using transaction... ?
        registrationUpdateEvent()
        registrationUpdateUser()
      }
      registrationSuccessful.value = true
    }
  }

  fun isUserRegistered(): Boolean{
    return false
    // TODO reimplement this when factory is merged
    //displayedEvent.attendeeList.contains(currentUserId)
  }


  private fun registrationUpdateEvent() {
    val event: Event = displayedEvent as Event

    // add currentUserId to the event attendees list
    event.attendeeList.add(currentUserId)

    // decrement ticket capacity
    event.ticket = EventTicket(event.ticket.name, event.ticket.price, event.ticket.capacity - 1)

    viewModelScope.launch {
      // update event data to the database
      when (val updateResponse = eventRepository.updateEvent(event)) {
        is Resource.Success -> {
          // TODO implement some states to display this information to the user through the UI
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
                // TODO implement some states to display this information to the user through the UI
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
            "EventDetailsViewModel",
            "Error getting user data: ${userResponse.throwable.message}")
        }
      }
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
    val ticket: EventTicket = EventTicket("", 0.0, 0),
    val mainOrganiser: String = "",
    val category: EventCategory = EventCategory.MUSIC,
)
