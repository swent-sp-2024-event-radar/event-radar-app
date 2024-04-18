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
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime

class EventsOverviewViewModel(db: FirebaseFirestore = Firebase.firestore) : ViewModel() {
    private val _uiState = MutableStateFlow(EventsOverviewUiState())

    val uiState: StateFlow<EventsOverviewUiState> = _uiState

    private val eventRef = db.collection("events")

    fun getEvents() {
        eventRef
            .get()
            .addOnSuccessListener { result ->
                val events =
                    result.documents.map { document ->
                        Event(
                            eventName = document.data?.get("name") as String,
                            eventPhoto = document.data?.get("photo_url") as String,
                            start = getLocalDateTime(document.data?.get("start") as String),
                            end = getLocalDateTime(document.data?.get("end") as String),
                            location =
                                getLocation(
                                    document.data?.get("location_name") as String,
                                    document.data?.get("location_lat") as Double,
                                    document.data?.get("location_lng") as Double,
                                ),
                            description = document.data?.get("description") as String,
                            ticket = getEventTicket(
                                document.data?.get("ticket_name") as String,
                                document.data?.get("ticket_price") as Double,
                                document.data?.get("ticket_quantity") as Int,
                            ),
                            contact = document.data?.get("contact_email") as String,
                            organiserList = getSet(document.data?.get("organisers_list")),
                            attendeeList = getSet(document.data?.get("attendees_list")),
                            category = getEventCategory(document.data?.get("category") as String),
                            fireBaseID = document.id
                        )
                    }
                _uiState.value =
                    _uiState.value.copy(
                        eventList = EventList(events, events, _uiState.value.eventList.getEvent))
            }
            .addOnFailureListener { exception ->
                Log.d("EventsOverviewViewModel", "Error getting documents: ", exception)
            }
    }
    fun onTaskClicked(id: String) {
        // Temporarily commented out as we transition from handling to-dos to handling events.

        /*
        val to_Do = _uiState.value.toDoList.getAllTask.find { it.id == id }
        _uiState.value =
            _uiState.value.copy(
                toDoList =
                    ToDoList(
                        _uiState.value.toDoList.getAllTask,
                        _uiState.value.toDoList.getFilteredTask,
                        to_Do))
           */
    }

    companion object {
        fun getLocalDateTime(dateTime: String): LocalDateTime {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            return LocalDateTime.parse(dateTime, formatter)
        }

        fun getSet(data: Any?): Set<String> {
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
