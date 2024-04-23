package com.github.se.eventradar.model.event

import androidx.lifecycle.ViewModel
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.repository.event.FirebaseEventRepository
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EventDetailsViewModel(val eventId: String? = null) : ViewModel() {

  private val _uiState = MutableStateFlow(EventUiState())
  val uiState: StateFlow<EventUiState> = _uiState

  data class EventUiState(
      val eventName: String = "",
      val eventPhoto: String = "",
      val start: LocalDateTime = LocalDateTime.MIN,
      val end: LocalDateTime = LocalDateTime.MAX,
      val location: Location = Location(0.0, 0.0, ""),
      val description: String = "",
      val ticket: EventTicket = EventTicket("", 0.0, 0),
      val contact: String = "",
      val category: EventCategory = EventCategory.MUSIC,
  )

  suspend fun getEventData() {
    if (eventId != null) {
      when (val event = FirebaseEventRepository().getEvent(eventId)) {
        is Resource.Success -> {
          _uiState.value =
              _uiState.value.copy(
                  eventName = event.data!!.eventName,
                  eventPhoto = event.data.eventPhoto,
                  start = event.data.start,
                  end = event.data.end,
                  location = event.data.location,
                  description = event.data.description,
                  ticket = event.data.ticket,
                  contact = event.data.contact,
                  category = event.data.category,
              )
        }
        is Resource.Failure -> {
          println("Failed to Fetch from Database")
        }
      }
    }
  }
}
