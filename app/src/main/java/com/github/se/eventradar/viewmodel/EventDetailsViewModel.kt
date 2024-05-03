package com.github.se.eventradar.model.event

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.repository.event.IEventRepository
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
) : ViewModel() {

  private val _uiState = MutableStateFlow(EventUiState())
  val uiState: StateFlow<EventUiState> = _uiState

  // TODO would require assisted data injection to have this as a parameters
  private lateinit var eventId: String

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
        }
        is Resource.Failure ->
            Log.d("EventDetailsViewModel", "Error getting event: ${response.throwable.message}")
      }
    }
  }

  fun isTicketFree(): Boolean {
    return !(_uiState.value.ticket.price > 0.0)
  }

  fun getTickets() : Unit{ //update ticket, while buying ticket give user 5 minutes to buy?
    viewModelScope.launch {
      when (val response = eventRepository.getEvent(eventId)) {
        is Resource.Success -> {
          _uiState.update {
            it.copy(ticket = response.data!!.ticket)
          }
        }
        is Resource.Failure ->
          Log.d("EventDetailsViewModel", "Error getting ticket for event: ${response.throwable.message}")
      }
    }
  }

  /*TODO actually, I had to recreate a view model when switching to the
   * buy ticket screen so that function is not needed anymore...
   */
  /*fun buyTicketForEvent() : Boolean{ //update the user attendee list, update the eventRepo ticket count
    viewModelScope.launch{
      if (!isTicketFree()){

      }
      //event id, new event values.
      val newState = _uiState.value.copy(ticket = _uiState.value.ticket.copy(capacity = _uiState.value.ticket.capacity-1))
      val newEvent = Event(newState, eventId) //eventUiState to Event?

      // TODO would need an atomic update of the database
      when (val response = eventRepository.updateEvent(newEvent)) {
      is Resource.Success -> {
      _uiState.update {
        it.copy(ticket = response.data!!.ticket)
      }
    }
      is Resource.Failure ->
      Log.d("EventDetailsViewModel", "Error buying ticket for event: ${response.throwable.message}")
    }
    }
    return true
  }*/

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
