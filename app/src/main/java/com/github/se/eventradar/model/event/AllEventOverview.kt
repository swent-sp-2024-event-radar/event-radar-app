package com.github.se.eventradar.model.event
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

