package com.github.se.eventradar.model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventList
import com.github.se.eventradar.model.event.getEventCategory
import com.github.se.eventradar.model.event.getEventTicket
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EventsOverviewViewModel(db: FirebaseFirestore = Firebase.firestore) : ViewModel() {
  private val _uiState = MutableStateFlow(EventsOverviewUiState())

  val uiState: StateFlow<EventsOverviewUiState> = _uiState

  private val eventRef = db.collection("events")

  fun getEvents() {
    eventRef
        .get()
        .addOnSuccessListener { result ->
          val events =
              result.documents.mapNotNull { document ->
                document.data?.let {
                  Event(
                      eventName = it["name"] as? String ?: "Unknown Event",
                      eventPhoto = it["photo_url"] as? String ?: "",
                      start = getLocalDateTime(it["start"] as? String ?: "01/01/1970 00:00:00"),
                      end = getLocalDateTime(it["end"] as? String ?: "01/01/1970 00:00:00"),
                      location =
                          getLocation(
                              it["location_name"] as? String ?: "Unknown Location",
                              (it["location_lat"] as? Number)?.toDouble() ?: 0.0,
                              (it["location_lng"] as? Number)?.toDouble() ?: 0.0),
                      description = it["description"] as? String ?: "",
                      ticket =
                          getEventTicket(
                              it["ticket_name"] as? String ?: "",
                              (it["ticket_price"] as? Number)?.toDouble() ?: 0.0,
                              (it["ticket_quantity"] as? Number)?.toInt() ?: 0),
                      hostUserId = it["main_organiser"] as? String ?: "", // Create User data class
                      organiserList = getSetOfStrings(it["organisers_list"]),
                      attendeeList = getSetOfStrings(it["attendees_list"]),
                      category = getEventCategory(it["category"] as? String ?: "Not Specified"),
                      fireBaseID = document.id)
                }
              }
          _uiState.value =
              _uiState.value.copy(
                  eventList = EventList(events, events, _uiState.value.eventList.selectedEvent))
        }
        .addOnFailureListener { exception ->
          Log.d("EventsOverviewViewModel", "Error getting documents: ", exception)
        }
  }

  fun getUpcomingEvents(uid: String) {
    eventRef
        .whereArrayContains("attendees_list", uid)
        .get()
        .addOnSuccessListener { result ->
          val upcomingEvents =
              result.documents.mapNotNull { document ->
                document.data?.let {
                  Event(
                      eventName = it["name"] as? String ?: "Unknown Event",
                      eventPhoto = it["photo_url"] as? String ?: "",
                      start = getLocalDateTime(it["start"] as? String ?: "01/01/2000 00:00:00"),
                      end = getLocalDateTime(it["end"] as? String ?: "01/01/2000 00:00:00"),
                      location =
                          getLocation(
                              it["location_name"] as? String ?: "Unknown Location",
                              (it["location_lat"] as? Number)?.toDouble() ?: 0.0,
                              (it["location_lng"] as? Number)?.toDouble() ?: 0.0),
                      description = it["description"] as? String ?: "",
                      ticket =
                          getEventTicket(
                              it["ticket_name"] as? String ?: "",
                              (it["ticket_price"] as? Number)?.toDouble() ?: 0.0,
                              (it["ticket_quantity"] as? Number)?.toInt() ?: 0),
                      hostUserId =
                          it["main_organiser"] as? String ?: "", // TODO Create User data class
                      organiserList = getSetOfStrings(it["organisers_list"]),
                      attendeeList = getSetOfStrings(it["attendees_list"]),
                      category = getEventCategory(it["category"] as? String ?: "Not Specified"),
                      fireBaseID = document.id)
                }
              }
          _uiState.value =
              _uiState.value.copy(
                  eventList =
                      EventList(
                          upcomingEvents,
                          _uiState.value.eventList.filteredEvent,
                          _uiState.value.eventList.selectedEvent))
        }
        .addOnFailureListener { exception ->
          Log.d("EventsOverviewViewModel", "Error getting upcoming events: ", exception)
        }
  }

  companion object {
    fun getLocalDateTime(dateTime: String): LocalDateTime {
      val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
      return LocalDateTime.parse(dateTime, formatter)
    }

    fun getSetOfStrings(data: Any?): Set<String> {
      return when (data) {
        is List<*> -> data.filterIsInstance<String>().toSet()
        is String -> setOf(data)
        else -> emptySet()
      }
    }
  }
}

data class EventsOverviewUiState(
    val eventList: EventList = EventList(emptyList(), emptyList(), null),
    val searchQuery: String = "",
)
