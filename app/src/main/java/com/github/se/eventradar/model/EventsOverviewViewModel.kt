package com.github.se.eventradar.model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventList
import com.github.se.eventradar.model.event.getEventCategory
import com.github.se.eventradar.model.event.getEventTicket
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EventsOverviewViewModel(db: FirebaseFirestore = Firebase.firestore) : ViewModel() {
  private val _uiState = MutableStateFlow(EventsOverviewUiState())

  val uiState: StateFlow<EventsOverviewUiState> = _uiState

  private val eventRef = db.collection("events")
  private val userRef = db.collection("users")

  fun getEvents() {
    eventRef
        .get()
        .addOnSuccessListener { result ->
          val events = result.documents.mapNotNull { document -> eventFromDocument(document) }
          _uiState.value =
              _uiState.value.copy(
                  eventList = EventList(events, events, _uiState.value.eventList.selectedEvent))
        }
        .addOnFailureListener { exception ->
          Log.d("EventsOverviewViewModel", "Error getting documents: ", exception)
        }
  }

  fun getUpcomingEvents(uid: String) {
    // Fetch the user's data to get their eventsAttendeeList
    userRef
        .document(uid)
        .get()
        .addOnSuccessListener { userSnapshot ->
          val user = userFromDocument(userSnapshot)
          user?.let {
            val attendeeList = it.eventsAttendeeList
            if (attendeeList.isNotEmpty()) {
              eventRef
                  .whereIn(FieldPath.documentId(), attendeeList)
                  .get()
                  .addOnSuccessListener { eventsQuerySnapshot ->
                    val upcomingEvents =
                        eventsQuerySnapshot.documents.mapNotNull { eventSnapshot ->
                          eventFromDocument(eventSnapshot)
                        }
                    _uiState.value =
                        _uiState.value.copy(
                            eventList =
                                EventList(
                                    upcomingEvents,
                                    upcomingEvents,
                                    _uiState.value.eventList.selectedEvent))
                  }
                  .addOnFailureListener { e ->
                    Log.d("EventsOverviewViewModel", "Error getting upcoming events: ", e)
                  }
            } else {
              // User has no upcoming events
              _uiState.value =
                  _uiState.value.copy(eventList = EventList(emptyList(), emptyList(), null))
            }
          }
              ?: run {
                Log.d("EventsOverviewViewModel", "Could not parse User from DocumentSnapshot.")
              }
        }
        .addOnFailureListener { e ->
          Log.d("EventsOverviewViewModel", "Error fetching user document: ", e)
        }
  }

  companion object {
    fun eventFromDocument(document: DocumentSnapshot): Event? {
        val data = document.data
        if (data == null) {
            Log.w("EventsOverviewViewModel", "DocumentSnapshot data is null for id: ${document.id}")
            return null
        }
        val eventName = data["name"] as? String ?: "No event name"
        if (eventName == "No event name") {
            Log.w("EventsOverviewViewModel", "Missing eventName for id: ${document.id}")
        }
        val eventPhoto = data["photo_url"] as? String ?: "No photo available"
        if (eventPhoto == "No photo available") {
            Log.w("EventsOverviewViewModel", "Missing event photo url for id: ${document.id}")
        }
        val start = data["start"] as? String ?: "01/01/1970 00:00:00"
        val end = data["end"] as? String ?: "01/01/1970 00:00:00"
        if (start == "01/01/1970 00:00:00" || end == "01/01/1970 00:00:00") {
            Log.w("EventsOverviewViewModel", "Missing event time details for id: ${document.id}")
        }
        val locationName = data["location_name"] as? String ?: "Unknown location"
        val locationLat = (data["location_lat"] as? Number)?.toDouble() ?: 0.0
        val locationLng = (data["location_lng"] as? Number)?.toDouble() ?: 0.0
        if (locationName == "Unknown location" || locationLat == 0.0 || locationLng == 0.0) {
            Log.w("EventsOverviewViewModel", "Incomplete location details for id: ${document.id}")
        }
        val description = data["description"] as? String ?: "Description not available"
        if (description == "Description not available") {
            Log.w("EventsOverviewViewModel", "Missing description for id: ${document.id}")
        }
        val ticketName = data["ticket_name"] as? String ?: "No ticket name"
        val ticketPrice = (data["ticket_price"] as? Number)?.toDouble() ?: 0.0
        val ticketQuantity = (data["ticket_quantity"] as? Number)?.toInt() ?: 0
        if (ticketName == "No ticket name" || ticketPrice == 0.0 || ticketQuantity == 0) {
            Log.w("EventsOverviewViewModel", "Missing ticket details for id: ${document.id}")
        }
        val hostUserId = data["main_organiser"] as? String ?: "Unknown organiser"
        if (hostUserId == "Unknown organiser") {
            Log.w("EventsOverviewViewModel", "Missing event organizer ID for id: ${document.id}")
        }
        val organiserList = convertToSetOfStrings(data["organisers_list"])
        if (organiserList.isEmpty()) {
            Log.w("EventsOverviewViewModel", "Missing or empty organisers list for id: ${document.id}")
        }
        val attendeeList = convertToSetOfStrings(data["attendees_list"])
        if (attendeeList.isEmpty()) {
            Log.w("EventsOverviewViewModel", "Missing or empty attendees list for id: ${document.id}")
        }
        val category = (data["category"] as? String) ?: "None"
        if (category == "None") {
            Log.w("EventsOverviewViewModel", "Missing category for id: ${document.id}")
        }
        return Event(
          eventName = eventName,
          eventPhoto = eventPhoto,
          start = getLocalDateTime(start),
          end = getLocalDateTime(end),
          location =
              getLocation(
                  locationName,
                  locationLat,
                  locationLng),
          description = description,
          ticket =
              getEventTicket(
                  ticketName,
                  ticketPrice,
                  ticketQuantity),
          hostUserId = hostUserId,
          organiserList = organiserList,
          attendeeList = attendeeList,
          category = getEventCategory(category),
          fireBaseID = document.id)
    }

    fun userFromDocument(document: DocumentSnapshot): User? {
        val data = document.data
        if (data == null) {
            Log.w("UserViewModel", "DocumentSnapshot data is null for user id: ${document.id}")
            return null
        }

        val age = (data["age"] as? Number)?.toInt() ?: 0
        if (age <= 0) {
            Log.w("UserViewModel", "Invalid or missing age for user id: ${document.id}")
        }
        val email = data["email"] as? String ?: "No email provided"
        if (email == "No email provided") {
            Log.w("UserViewModel", "Missing email for user id: ${document.id}")
        }
        val firstName = data["firstName"] as? String ?: "Unknown first name"
        if (firstName == "Unknown first name") {
            Log.w("UserViewModel", "Missing firstName for user id: ${document.id}")
        }
        val lastName = data["lastName"] as? String ?: "Unknown last name"
        if (lastName == "Unknown last name") {
            Log.w("UserViewModel", "Missing lastName for user id: ${document.id}")
        }
        val phoneNumber = data["phoneNumber"] as? String ?: "No phone number"
        if (phoneNumber == "No phone number") {
            Log.w("UserViewModel", "Missing phoneNumber for user id: ${document.id}")
        }
        val accountStatus = data["accountStatus"] as? String ?: "Unknown account status"
        if (accountStatus == "Unknown account status") {
            Log.w("UserViewModel", "Missing accountStatus for user id: ${document.id}")
        }
        val eventsAttendeeList = convertToListOfStrings(data["eventsAttendeeList"])
        if (eventsAttendeeList.isEmpty()) {
            Log.w("UserViewModel", "Empty or invalid eventsAttendeeList for user id: ${document.id}")
        }
        val eventsHostList = convertToListOfStrings(data["eventsHostList"])
        if (eventsHostList.isEmpty()) {
            Log.w("UserViewModel", "Empty or invalid eventsHostList for user id: ${document.id}")
        }
        val profilePicUrl = data["profilePicUrl"] as? String ?: "No profile picture"
        if (profilePicUrl == "No profile picture") {
            Log.w("UserViewModel", "Missing profilePicUrl for user id: ${document.id}")
        }
        val qrCodeUrl = data["qrCodeUrl"] as? String ?: "No QR code"
        if (qrCodeUrl == "No QR code") {
            Log.w("UserViewModel", "Missing QR code URL for user id: ${document.id}")
        }
        val username = data["username"] as? String ?: "Unknown username"
        if (username == "Unknown username") {
            Log.w("UserViewModel", "Missing username for user id: ${document.id}")
        }
        return User(
          userId = document.id,
          age = age,
          email = email,
          firstName = firstName,
          lastName = lastName,
          phoneNumber = phoneNumber,
          accountStatus = accountStatus,
          eventsAttendeeList = eventsAttendeeList,
          eventsHostList = eventsHostList,
          profilePicUrl = profilePicUrl,
          qrCodeUrl = qrCodeUrl,
          username = username)
    }

    private fun getLocalDateTime(dateTime: String): LocalDateTime {
      val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
      return LocalDateTime.parse(dateTime, formatter)
    }
      private fun convertToListOfStrings(data: Any?): List<String> {
          return when (data) {
              is List<*> -> data.filterIsInstance<String>().toList()
              is String -> listOf(data)
              else -> emptyList()
          }
      }
    private fun convertToSetOfStrings(data: Any?): Set<String> {
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
