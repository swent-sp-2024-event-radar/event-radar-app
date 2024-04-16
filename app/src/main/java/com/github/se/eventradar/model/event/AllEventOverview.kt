package com.github.se.eventradar.model.event
<<<<<<< Updated upstream
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Location
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

class EventDetailsOverview(private val db: FirebaseFirestore = Firebase.firestore): ViewModel {


    private val _uiState = MutableStateFlow<List<EventListingUiState>>(emptyList())
    val uiState: StateFlow<List<EventListingUiState>> = _uiState

    fun getEventData(): Unit {
        viewModelScope.launch {
            val eventList = mutableListOf<List<EventListingUiState>>()
            try {
                db.collection("EVENT")
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val eventUiState = createEventUiStateFromDocument(document)
                            eventList.add(eventUiState)
                        }
                        _uiState.value = eventList
                    }
                    .addOnFailureListener { exception ->
                        println("Error fetching events: $exception")
                    }
            } catch (e: Exception) {
                println("Error fetching events: $e")
            }
        }
    }

    private fun createEventUiStateFromDocument(document: DocumentSnapshot): EventUiState {
        val latitude = document.getGeoPoint("location")!!.latitude
        val longitude = document.getGeoPoint("location")!!.longitude
        val ticketName = document.getString("Ticket Name") ?: ""
        val ticketPrice = document.getLong("Ticket Price")!!.toDouble()
        val ticketQuantity = document.getLong("Ticket Quantity")!!.toInt()
        val address = document.getString("Address") ?: ""
        val category = document.getString("Category") ?: ""
        val startInstant = document.getTimestamp("Start")!!.toDate().toInstant()
        val endInstant = document.getTimestamp("End")!!.toDate().toInstant()

        val eventUiState = EventUiState(
            eventName = document.getString("Name") ?: "",
            eventPhoto = document.getString("Photo") ?: "",
            start = LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault()),
            end = LocalDateTime.ofInstant(endInstant, ZoneId.systemDefault()),
            location = Location(latitude, longitude, address),
            description = document.getString("Description") ?: "",
            ticket = Ticket(ticketName, ticketPrice, ticketQuantity),
            contact = document.getString("Contact") ?: "",
            category = EventCategory.valueOf(category)
        )
        _uiState.value = _uiState.value + eventUiState
        return eventUiState
    }


    data class EventListingUiState(
        val eventName: String = "",
        val eventPhoto: String = "",
        val start: LocalDateTime,
    )
}

=======
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


class AllEventOverview (private val db: FirebaseFirestore = Firebase.firestore): ViewModel {
    private val _uiState = MutableStateFlow<List<Event>>(emptyList())
    val uiState: StateFlow<List<EventUiState>> = _uiState
}


class EventDetailsOverview(

) : ViewModel() {

    private val _uiState = MutableStateFlow(initialEventState())
    val uiState: StateFlow<EventUiState> = _uiState

    fun getEventData(eventId: String): Unit {

        try {
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
                        _uiState.value.ticket = Ticket(ticketName, ticketPrice, ticketQuantity)
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
        } catch (e: Exception) {
            // Handle any errors that occur during fetching
            // You may want to log the error or show a message to the user
            println("Error fetching event: $e")
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
        Ticket("", 0.0, 0),
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
    var ticket: Ticket,
    var contact: String,
    var category: EventCategory,
)
>>>>>>> Stashed changes
