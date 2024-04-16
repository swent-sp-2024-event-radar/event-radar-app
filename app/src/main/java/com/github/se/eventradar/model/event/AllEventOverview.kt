package com.github.se.eventradar.model.event
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class AllEventOverview(private val db: FirebaseFirestore = Firebase.firestore): ViewModel() {

    private val _uiState = MutableStateFlow<List<EventListingUiState>>(emptyList())
    val uiState: StateFlow<List<EventListingUiState>> = _uiState

    fun getEventData(): Unit {
        viewModelScope.launch {
            try {
                db.collection("EVENT")
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
            } catch (e: Exception) {
                println("Error fetching events: $e")
            }
        }
    }
    data class EventListingUiState(
        var eventName: String = "",
        var eventPhoto: String = "",
        var start: LocalDateTime,
    )
}


