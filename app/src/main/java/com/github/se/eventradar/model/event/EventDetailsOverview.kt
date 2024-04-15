package com.github.se.eventradar.model.event
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.Instant
import java.time.ZoneId

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Card

import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.text.style.TextOverflow

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.github.se.eventradar.R
import com.github.se.eventradar.model.Location

import java.time.LocalDateTime

import java.time.format.DateTimeFormatter

class EventDetailsOverview(private val db: FirebaseFirestore = Firebase.firestore, eventId: String): ViewModel() {

    private val _uiState = MutableStateFlow(initialEventState())
    val uiState: StateFlow<EventUiState> = _uiState


    fun getEventData(eventId: String): Unit {
        try {
            // Fetch the specific document from Firebase
            db.collection("EVENT").document(eventId)
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
                        val startInstant: Instant =
                            document.getTimestamp("Start")!!.toDate().toInstant()
                        val endInstant: Instant =
                            document.getTimestamp("End")!!.toDate().toInstant()

                        val currentEventUi = EventUiState(
                            document.getString("Name") ?: "",
                            document.getString("Photo") ?: "", //TODO
                            LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault()),
                            LocalDateTime.ofInstant(endInstant, ZoneId.systemDefault()),
                            Location(latitude, longitude, address),
                            document.getString("Description") ?: "",
                            Ticket(ticketName, ticketPrice, ticketQuantity),
                            document.getString("Contact") ?: "",
                            EventCategory.valueOf(category),
                        )

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

    @Composable
    fun DisplayEventOverview(event: EventUiState): Unit {

        var isChecked: Boolean = false

        val titleTextStyle = TextStyle(
            fontSize = 18.sp,
            fontFamily = FontFamily(Font(R.font.roboto)),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )

        val subTitleTextStyle = TextStyle(
            fontSize = 17.sp,
            fontFamily = FontFamily(Font(R.font.roboto)),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )

        val standardTextStyle = TextStyle(
            fontSize = 18.sp,
            fontFamily = FontFamily(Font(R.font.roboto)),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = event.eventName,
                    style = titleTextStyle

                )
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth() // todo needed?
                        .padding(16.dp),
                    elevation = cardElevation(defaultElevation = 8.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = event.description,
                        overflow = TextOverflow.Ellipsis,
                        style = standardTextStyle
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth() // todo needed?
                        .padding(16.dp),
                    elevation = cardElevation(defaultElevation = 8.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = event.location.address, //todo on click to open MAP
                        style = standardTextStyle,
                        modifier = Modifier.padding(bottom = 8.dp),
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth() // todo needed?
                        .padding(16.dp),
                    elevation = cardElevation(defaultElevation = 8.dp),
                    shape = MaterialTheme.shapes.medium
                ) {

                    Text(
                        text = "${event.category}",
                        style = standardTextStyle,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth() // todo needed?
                        .padding(16.dp),
                    elevation = cardElevation(defaultElevation = 8.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "Start: ${formatDateTime(event.start)}",
                        style = standardTextStyle,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth() // todo needed?
                        .padding(16.dp),
                    elevation = cardElevation(defaultElevation = 8.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "End: ${formatDateTime(event.start)}",
                        style = standardTextStyle,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Tickets",
                    style = subTitleTextStyle
                )
                Spacer(modifier = Modifier.height(16.dp))
                //tickets
                Card(
                    modifier = Modifier
                        .fillMaxWidth() // todo needed?
                        .padding(16.dp),
                    elevation = cardElevation(defaultElevation = 8.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { selected ->
                                // Update the selection state of the ticket item
                                isChecked = selected
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = event.ticket.name,
                            style = standardTextStyle,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "${event.ticket.price}",
                            style = standardTextStyle,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            }
        }
    }
}


private fun formatDateTime(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm")
    return dateTime.format(formatter)
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
    val eventName: String,
    val eventPhoto: String,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val location: Location,
    val description: String,
    val ticket: Ticket,
    val contact: String,
    val category: EventCategory,
)



