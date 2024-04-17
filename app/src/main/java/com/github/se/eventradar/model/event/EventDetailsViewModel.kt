package com.github.se.eventradar.model.event

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

class EventDetailsViewModel(
  private val db: FirebaseFirestore = Firebase.firestore,
  eventId: String
) : ViewModel() {

  private val _uiState = MutableStateFlow(EventUiState())
  val uiState: StateFlow<EventUiState> = _uiState

  fun getEventData(eventId: String): Unit {
    // Fetch the specific document from Firebase
    db.collection("event")
      .document(eventId)
      .get()
      .addOnSuccessListener { document ->
        // Check if the document exists
        if (document.exists()) {
          val event = createEventFromDocument(document, eventId)
          fillUIStateFromEvent(event)
        } else {
          // TODO Document does not exist, popup message error ? Could not load event
          println("Document does not exist")
        }
      }
      .addOnFailureListener { exception ->
        // TODO Handle any errors that occur during fetching
        println("Error fetching event: $exception")
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

  private fun createEventFromDocument(document: DocumentSnapshot, eventId: String): Event {
    val startInstant: Instant = document.getTimestamp("Start")!!.toDate().toInstant()
    val endInstant: Instant = document.getTimestamp("End")!!.toDate().toInstant()
    val latitude = document.getGeoPoint("location")!!.latitude
    val longitude = document.getGeoPoint("location")!!.longitude
    val address = document.getString("Address") ?: ""
    val ticketName = document.getString("Ticket Name") ?: ""
    val ticketPrice = document.getLong("Ticket Price")!!.toDouble()
    val ticketQuantity = document.getLong("Ticket Quantity")!!.toInt()
    val category = document.getString("Category") ?: ""

    return Event(
      eventName = document.getString("Name") ?: "",
      eventPhoto = document.getString("Photo") ?: "",
      start = LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault()),
      end = LocalDateTime.ofInstant(endInstant, ZoneId.systemDefault()),
      location = Location(latitude, longitude, address),
      description = document.getString("Description") ?: "",
      ticket = Ticket(ticketName, ticketPrice, ticketQuantity),
      contact = document.getString("Contact") ?: "",
      organiserList = (document["OrganiserList"] as List<*>).map { it -> it.toString() }.toSet(),
      attendeeList = (document["AttendeeList"] as List<*>).map { it -> it.toString() }.toSet(),
      category = EventCategory.valueOf(category), // TODO add empty category value ?
      fireBaseID = eventId,
    )
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
