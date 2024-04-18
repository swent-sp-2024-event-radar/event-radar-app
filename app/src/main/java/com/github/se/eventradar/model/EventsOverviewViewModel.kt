package com.github.se.eventradar.model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventList
import com.github.se.eventradar.model.event.getEventCategory
import com.github.se.eventradar.model.event.getEventTicket
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
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
  private val userRef = db.collection("users")

  fun getEvents() {
    eventRef
        .get()
        .addOnSuccessListener { result ->
          val events = result.documents.mapNotNull { document -> eventFromDocument(document) }
          _uiState.value =
              _uiState.value.copy(
                  eventList = EventList(events, events, _uiState.value.eventList.selectedEvent))
        }
        .addOnFailureListener { exception ->
          Log.d("EventsOverviewViewModel", "Error getting documents: ", exception)
        }
  }

  fun getUpcomingEvents(uid: String) {
    // Fetch the user's data to get their eventsAttendeeList
    userRef
        .document(uid)
        .get()
        .addOnSuccessListener { userSnapshot ->
          val user = userFromDocument(userSnapshot)
          user?.let {
            val attendeeList = it.eventsAttendeeList
            if (attendeeList.isNotEmpty()) {
              eventRef
                  .whereIn(FieldPath.documentId(), attendeeList)
                  .get()
                  .addOnSuccessListener { eventsQuerySnapshot ->
                    val upcomingEvents =
                        eventsQuerySnapshot.documents.mapNotNull { eventSnapshot ->
                          eventFromDocument(eventSnapshot)
                        }
                    _uiState.value =
                        _uiState.value.copy(
                            eventList =
                                EventList(
                                    upcomingEvents,
                                    upcomingEvents,
                                    _uiState.value.eventList.selectedEvent))
                  }
                  .addOnFailureListener { e ->
                    Log.d("EventsOverviewViewModel", "Error getting upcoming events: ", e)
                  }
            } else {
              // User has no upcoming events
              _uiState.value =
                  _uiState.value.copy(eventList = EventList(emptyList(), emptyList(), null))
            }
          }
              ?: run {
                Log.d("EventsOverviewViewModel", "Could not parse User from DocumentSnapshot.")
              }
        }
        .addOnFailureListener { e ->
          Log.d("EventsOverviewViewModel", "Error fetching user document: ", e)
        }
  }

  companion object {
    fun eventFromDocument(document: DocumentSnapshot): Event? {
      val data = document.data ?: return null
      return Event(
          eventName = data["name"] as? String ?: "Unknown Event",
          eventPhoto = data["photo_url"] as? String ?: "",
          start = getLocalDateTime(data["start"] as? String ?: "01/01/1970 00:00:00"),
          end = getLocalDateTime(data["end"] as? String ?: "01/01/1970 00:00:00"),
          location =
              getLocation(
                  data["location_name"] as? String ?: "Unknown Location",
                  (data["location_lat"] as? Number)?.toDouble() ?: 0.0,
                  (data["location_lng"] as? Number)?.toDouble() ?: 0.0),
          description = data["description"] as? String ?: "",
          ticket =
              getEventTicket(
                  data["ticket_name"] as? String ?: "",
                  (data["ticket_price"] as? Number)?.toDouble() ?: 0.0,
                  (data["ticket_quantity"] as? Number)?.toInt() ?: 0),
          hostUserId = data["main_organiser"] as? String ?: "",
          organiserList = getSetOfStrings(data["organisers_list"]),
          attendeeList = getSetOfStrings(data["attendees_list"]),
          category = getEventCategory(data["category"] as? String ?: "Not Specified"),
          fireBaseID = document.id)
    }

    fun userFromDocument(document: DocumentSnapshot): User? {
      val data = document.data ?: return null

      return User(
          userId = document.id,
          age = (data["age"] as? Number)?.toInt() ?: 0,
          email = data["email"] as? String ?: "",
          firstName = data["firstName"] as? String ?: "",
          lastName = data["lastName"] as? String ?: "",
          phoneNumber = data["phoneNumber"] as? String ?: "",
          accountStatus = data["accountStatus"] as? String ?: "Unknown",
          eventsAttendeeList =
              (data["eventsAttendeeList"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
          eventsHostList =
              (data["eventsHostList"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
          profilePicUrl = data["profilePicUrl"] as? String ?: "",
          qrCodeUrl = data["qrCodeUrl"] as? String ?: "",
          username = data["username"] as? String ?: "Unknown User")
    }

    private fun getLocalDateTime(dateTime: String): LocalDateTime {
      val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
      return LocalDateTime.parse(dateTime, formatter)
    }

    private fun getSetOfStrings(data: Any?): Set<String> {
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
