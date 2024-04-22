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
                val eventName = document.data?.get("name") as? String
                val eventPhoto = document.data?.get("photo_url") as? String
                val start = (document.data?.get("start") as? String)?.let { getLocalDateTime(it) }
                val end = (document.data?.get("end") as? String)?.let { getLocalDateTime(it) }
                val locationName = document.data?.get("location_name") as? String
                val locationLat = document.data?.get("location_lat") as? Double
                val locationLng = document.data?.get("location_lng") as? Double
                val description = document.data?.get("description") as? String
                val ticketName = document.data?.get("ticket_name") as? String
                val ticketPrice = document.data?.get("ticket_price") as? Double
                val ticketQuantity = document.data?.get("ticket_quantity") as? Int
                val mainOrganiser = document.data?.get("main_organiser") as? String
                val organisersList = document.data?.get("organisers_list")
                val attendeesList = document.data?.get("attendees_list")
                val category = document.data?.get("category") as? String

                if (eventName != null &&
                    eventPhoto != null &&
                    start != null &&
                    end != null &&
                    locationName != null &&
                    locationLat != null &&
                    locationLng != null &&
                    description != null &&
                    ticketName != null &&
                    ticketPrice != null &&
                    ticketQuantity != null &&
                    mainOrganiser != null &&
                    category != null) {
                  Event(
                      eventName = eventName,
                      eventPhoto = eventPhoto,
                      start = start,
                      end = end,
                      location = getLocation(locationName, locationLat, locationLng),
                      description = description,
                      ticket = getEventTicket(ticketName, ticketPrice, ticketQuantity),
                      contact = getEventContact(mainOrganiser),
                      organiserList = getSetOfStrings(organisersList),
                      attendeeList = getSetOfStrings(attendeesList),
                      category = getEventCategory(category),
                      fireBaseID = document.id)
                } else {
                  null
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

  private val userRef = db.collection("users")

  private fun getEventContact(contactId: String): String {
    var contactEmail = ""

    userRef
        .document(contactId)
        .collection("private")
        .limit(1) // Limit the query to retrieve only one document
        .get()
        .addOnSuccessListener { querySnapshot ->
          for (documentSnapshot in querySnapshot.documents) {
            contactEmail = documentSnapshot.data?.get("email") as String
            break
          }
        }
        .addOnFailureListener { exception ->
          Log.d("EventsOverviewViewModel", "Error getting event contact: ", exception)
        }

    return contactEmail
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
