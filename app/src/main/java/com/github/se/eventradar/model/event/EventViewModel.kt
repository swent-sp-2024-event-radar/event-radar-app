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

class ToDoViewModel


class EventViewModel(val db: FirebaseFirestore = Firebase.firestore): ViewModel() {

    private val _uiStateAll = MutableStateFlow<List<EventUiState>>(emptyList())
    val uiStateAll: StateFlow<List<EventUiState>> = _uiStateAll

    private val _uiStateUpcoming = MutableStateFlow<List<EventUiState>>(emptyList())
    val uiStateUpcoming: StateFlow<List<EventUiState>> = _uiStateUpcoming

    fun getAllEvents(uid) {
        viewModelScope.launch {
            val eventList = mutableListOf<EventViewModel.EventUiState>()
            try {
                db.collection("EVENT").document("")
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        for (document in querySnapshot.documents) {
                            val latitude = document.getGeoPoint("location")!!.latitude
                            val longitude = document.getGeoPoint("location")!!.longitude
                            val ticketName = document.getString("Ticket Name") ?: ""
                            val ticketPrice = document.getLong("Ticket Price")!!.toDouble()
                            val ticketQuantity = document.getLong("Ticket Quantity")!!.toInt()
                            val address = document.getString("Address") ?: ""
                            val category = document.getString("Category") ?: ""
                            val startInstant: Instant = document.getTimestamp("Start")!!.toDate().toInstant()
                            val endInstant: Instant = document.getTimestamp("End")!!.toDate().toInstant()
                            val referenceArray = document.get("OrganiserList") as? List<DocumentReference>
                            val pathList = mutableListOf<String>()
                            referenceArray?.forEach { reference ->
                                val path = reference.path // Get the path string of the reference
                                pathList.add(path) // Add the path string to the list
                            }

                            val eventUiState = EventUiState(
                                document.getString("Name") ?: "",
                                document.getString("Photo") ?: "", //TODO
                                LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault()),
                                LocalDateTime.ofInstant(endInstant, ZoneId.systemDefault()),
                                Location(latitude, longitude, address),
                                document.getString("Description") ?: "",
                                Ticket(ticketName, ticketPrice, ticketQuantity),
                                document.getString("Contact") ?: "",
                                pathList.toSet(),
                                EventCategory.valueOf(category),
                                fireBaseID = document.id
                            )
                            eventList.add(eventUiState)
                        }

                    }
                    .addOnFailureListener { exception ->
                        // Handle failure (log error, etc.)
                        println("Error fetching events: $exception")
                    }
            } catch (e: Exception) {
                // Handle exception (log error, etc.)
                println("Exception: $e")
            }
        }
    }

    data class EventUiState(
        val eventName: String = "",
        val eventPhoto: String = "",
        val start: LocalDateTime,
        val end: LocalDateTime,
        val location: Location = Location(0.0, 0.0, ""),
        val description: String = "",
        val ticket: Ticket = Ticket("", 0.0, 0),
        val contact: String = "",
        val organiserList: Set<String> = emptySet(),
        val category: EventCategory,
        val fireBaseID: String = ""
    )
}










