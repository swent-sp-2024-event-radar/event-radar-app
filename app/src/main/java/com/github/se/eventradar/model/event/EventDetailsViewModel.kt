package com.github.se.eventradar.model.event

import androidx.lifecycle.ViewModel
import com.github.se.eventradar.model.Location
import com.google.firebase.Firebase
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

  private val _uiState = MutableStateFlow(initialEventState())
  val uiState: StateFlow<EventUiState> = _uiState

  fun getEventData(eventId: String): Unit {

    // Fetch the specific document from Firebase
    db.collection("EVENT")
        .document(eventId)
        .get()
        .addOnSuccessListener { document ->
          // Check if the document exists
          if (document.exists()) {
            // Extract data from the document and create an Event object
            val latitude = document.getGeoPoint("location")!!.latitude
            val longitude = document.getGeoPoint("location")!!.longitude
            val ticketName = document.getString("Ticket Name") ?: ""
            val ticketPrice = document.getLong("Ticket Price")!!.toDouble()
            val ticketQuantity = document.getLong("Ticket Quantity")!!.toInt()
            val address = document.getString("Address") ?: ""
            val category = document.getString("Category") ?: ""
            val startInstant: Instant = document.getTimestamp("Start")!!.toDate().toInstant()
            val endInstant: Instant = document.getTimestamp("End")!!.toDate().toInstant()

            _uiState.value.eventName = document.getString("Name") ?: ""
            _uiState.value.eventPhoto = document.getString("Photo") ?: ""
            _uiState.value.start = LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault())
            _uiState.value.end = LocalDateTime.ofInstant(endInstant, ZoneId.systemDefault())
            _uiState.value.location = Location(latitude, longitude, address)
            _uiState.value.description = document.getString("Description") ?: ""
            _uiState.value.ticket = EventTicket(ticketName, ticketPrice, ticketQuantity)
            _uiState.value.contact = document.getString("Contact") ?: ""
            _uiState.value.category = EventCategory.valueOf(category)
          } else {
            // Document does not exist
            println("Document does not exist")
          }
        }
        .addOnFailureListener { exception ->
          // Handle any errors that occur during fetching
          // You may want to log the error or show a message to the user
          println("Error fetching event: $exception")
        }
  }
}

private fun initialEventState(): EventUiState {
  return EventUiState(
      "",
      "",
      LocalDateTime.MIN,
      LocalDateTime.MAX,
      Location(0.0, 0.0, ""),
      "",
      EventTicket("", 0.0, 0),
      "",
      EventCategory.MUSIC,
  )
}

data class EventUiState(
    var eventName: String,
    var eventPhoto: String,
    var start: LocalDateTime,
    var end: LocalDateTime,
    var location: Location,
    var description: String,
    var ticket: EventTicket,
    var contact: String,
    var category: EventCategory,
)
