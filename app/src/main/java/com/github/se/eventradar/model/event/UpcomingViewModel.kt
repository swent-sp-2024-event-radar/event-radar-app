package com.github.se.eventradar.model.event
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class UpcomingViewModel(private val db: FirebaseFirestore = Firebase.firestore): ViewModel() {

    private val _uiState = MutableStateFlow<List<EventListingUiState>>(emptyList())
    val uiState: StateFlow<List<EventListingUiState>> = _uiState

    fun getEventData(uid: String): Unit {
        db.collection("EVENT")
            .whereArrayContains("AttendeeList", uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val startInstant: Instant = document.getTimestamp("Start")!!.toDate().toInstant()

                    val eventListView = EventListingUiState(
                        document.getString("Name") ?: "",
                        document.getString("Photo") ?: "",
                        LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault())
                    )
                    _uiState.value + eventListView
                }
            }
            .addOnFailureListener { exception ->
                println("Error fetching events: $exception")
            }
    }
    data class EventListingUiState(
        var eventName: String = "",
        var eventPhoto: String = "",
        var start: LocalDateTime,
    )
}


