package com.github.se.eventradar.model.event

import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.getLocation
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.format.DateTimeFormatter

class EventDetailsViewModel(
  private val db: FirebaseFirestore = Firebase.firestore,
  val eventId: String? = null
) : ViewModel() {

  private val tag = "EventDetailsViewModel"

  private val _uiState = MutableStateFlow(EventUiState())
  val uiState: StateFlow<EventUiState> = _uiState

  fun getEventData() {
    if(eventId != null){
      db.collection("events")
        .document(eventId)
        .get()
        .addOnSuccessListener { document ->
          if (document.exists()) {
            val event = createEventFromDocument(document, eventId)
            fillUIStateFromEvent(event)
          } else {
            // TODO Document does not exist, popup message error ? Could not load event
            Log.d(tag, "Document does not exist")
          }
        }
        .addOnFailureListener { exception ->
          // TODO Handle any errors that occur during fetching
          Log.d(tag, "Error fetching event: $exception")
        }
    }
    else{
      Log.d(tag, "id is null")
    }

  }

  private fun fillUIStateFromEvent(event: Event) {
    _uiState.value =
      _uiState.value.copy(
        eventName = event.eventName,
        eventPhoto = event.eventPhoto,
        start = event.start,
        end = event.end,
        location = event.location,
        description = event.description,
        ticket = event.ticket,
        contact = event.contact,
        category = event.category,
        )
  }

  private fun createEventFromDocument(document: DocumentSnapshot, eventId: String) : Event {
    val startInstant: Instant = document.getTimestamp("start")!!.toDate().toInstant()
    val endInstant: Instant = document.getTimestamp("end")!!.toDate().toInstant()
    val latitude = document.getGeoPoint("location")!!.latitude
    val longitude = document.getGeoPoint("location")!!.longitude
    val address = document.getString("address") ?: ""
    val ticketName = document.getString("ticketName") ?: ""
    val ticketPrice = document.getLong("ticketPrice")!!.toDouble()
    val ticketQuantity = document.getLong("ticketQuantity")!!.toInt()
    val category = document.getString("category") ?: ""

    return Event(
      eventName = document.getString("name") ?: "",
      eventPhoto = document.getString("photo") ?: "",
      start = LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault()),
      end = LocalDateTime.ofInstant(endInstant, ZoneId.systemDefault()),
      location = Location(latitude, longitude, address),
      description = document.getString("description") ?: "",
      ticket = Ticket(ticketName, ticketPrice, ticketQuantity),
      contact = document.getString("contact") ?: "",
      organiserList = (document["organiserList"] as List<*>).map { it.toString() }.toSet(),
      attendeeList = (document["attendeeList"] as List<*>).map { it.toString() }.toSet(),
      category = getCategory(category),
      fireBaseID = eventId,
    )
  }
  private fun getCategory(value: String) : EventCategory{
    var category = EventCategory.UNDEFINED
    try {
      category = EventCategory.valueOf(value)
    } catch (e: IllegalArgumentException) {
      Log.d(tag, "No valid category")
    }
    return category
  }
}

data class EventUiState(
  val eventName: String = "",
  val eventPhoto: String = "",
  val start: LocalDateTime = LocalDateTime.MIN,
  val end: LocalDateTime = LocalDateTime.MAX,
  val location: Location = Location(0.0, 0.0, ""), //TODO add standard val to location class ?
  val description: String = "",
  val ticket: Ticket = Ticket("", 0.0, 0),
  val contact: String = "",
  val category: EventCategory = EventCategory.MUSIC,
)
