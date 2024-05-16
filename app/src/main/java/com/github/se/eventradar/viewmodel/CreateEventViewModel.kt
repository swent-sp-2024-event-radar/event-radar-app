package com.github.se.eventradar.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.repository.event.IEventRepository
import com.github.se.eventradar.model.repository.user.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray

@HiltViewModel
class CreateEventViewModel
@Inject
constructor(
    private val eventRepository: IEventRepository,
    private val userRepository: IUserRepository
) : ViewModel() {
  private val _uiState = MutableStateFlow(CreateEventUiState())
  val uiState: StateFlow<CreateEventUiState> = _uiState

  fun addEvent(state: MutableStateFlow<CreateEventUiState> = _uiState) {
    viewModelScope.launch {
      userRepository.getCurrentUserId().let { userIdResource ->
        when (userIdResource) {
          is Resource.Success -> {
            val uid = userIdResource.data
            val fetchedLocation = fetchLocation(state.value.location, state)!! //fields will always be validated before, so this cannot be null.
              val eventPhotoUri =
                  state.value.eventPhotoUri
                      ?: Uri.parse("android.resource://com.github.se.eventradar/drawable/placeholder")
              //event name?
              userRepository.uploadImage(eventPhotoUri, uid, "Event_Pictures") //uid_event_name?
              val eventPhotoUrl =
                  when (val result = userRepository.getImage(uid, "Profile_Pictures")) {
                      is Resource.Success -> {
                          result.data
                      }
                      is Resource.Failure -> {
                          Log.d("LoginScreenViewModel", "Fetching Profile Picture Error")
                          ""
                      }
                  }
            val eventHashMap =
                hashMapOf(
                    "name" to state.value.eventName,
                    "photo_url" to state.value.eve,
                    "start" to state.value.startDate + "T" + state.value.startTime,
                    "end" to state.value.endDate + "T" + state.value.endTime,
                    "location_lat" to fetchedLocation.latitude, // convert location into lat and long
                    "location_lng" to fetchedLocation.longitude,
                    "location_name" to fetchedLocation.address,
                    "description" to state.value.eventDescription,
                    "ticket_name" to state.value.ticketName,
                    "ticket_price" to state.value.ticketPrice,
                    "ticket_capacity" to state.value.ticketCapacity,
                    "ticket_purchases" to 0,
                    "main_organiser" to uid,
                    "organisers_list" to state.value.organiserList,
                    "attendees_list" to mutableListOf<String>(),
                    "category" to state.value.eventCategory,
                )
            val newEvent = Event(eventHashMap)
            addUserEvent(uid, newEvent)
          }
          is Resource.Failure -> {
            Log.d("CreateEventViewModel", "User not logged in or error fetching user ID")
            _uiState.value =
                _uiState.value
                    .copy() // make all the fields null to ensure it doesn't preserve prev state
          }
        }
      }
    }
  }

  private fun fetchLocation(locationName: String, state: MutableStateFlow<CreateEventUiState>) : Location? {
    val client = OkHttpClient()
    val url = "https://nominatim.openstreetmap.org/search?q=${locationName}&format=json"
    val request = Request.Builder().url(url).build()
      var fetchedLocationName = ""
      var latitude = 0.0
      var longitude = 0.0
    client
        .newCall(request)
        .enqueue(
            object : Callback {
              override fun onFailure(call: Call, e: IOException) {
                Log.d("CreateEventViewModel", "OnFailure IOException ${e.message}")
              }

              override fun onResponse(call: Call, response: Response) {
                response.use {
                  if (!response.isSuccessful) {
                      Log.d("CreateEventViewModel", "OnResponse IOException ${response}")
                  }
                  val jsonArray = JSONArray(response.body!!.string())
                  val firstObject = jsonArray.getJSONObject(0)
                  fetchedLocationName = firstObject.getString("name")
                    latitude = firstObject.getString("lat").toDouble()
                    longitude = firstObject.getString("lon").toDouble()
                  state.value = state.value.copy(location = fetchedLocationName)
                }
              }
            })
      return when (fetchedLocationName.isBlank() && latitude == 0.0 && longitude == 0.0){
          false -> {null}
          true ->{Location(latitude,longitude,fetchedLocationName)}
      }
  }

  private suspend fun addUserEvent(
      uid: String,
      newEvent: Event,
      state: MutableStateFlow<CreateEventUiState> = _uiState
  ) {
    // update the user event list
    // add the event.
    viewModelScope.launch {
      when (eventRepository.addEvent(newEvent)) {
        is Resource.Success -> {
          // update user!
          updateUserHostList(uid, newEvent.fireBaseID)
          Log.d("CreateEventViewModel", "Successfully added event")
        }
        is Resource.Failure -> {
          Log.d("CreateEventViewModel", "Failed to add event")
        }
      }
    }
  }

  private suspend fun updateUserHostList(
      uid: String,
      newEventId: String,
      state: MutableStateFlow<CreateEventUiState> = _uiState
  ) {
    when (val userResource = userRepository.getUser(uid)) {
      is Resource.Success -> {
        val user = userResource.data!!
        val userValues =
            hashMapOf(
                "private/firstName" to user.firstName,
                "private/lastName" to user.lastName,
                "private/phoneNumber" to user.phoneNumber,
                "private/birthDate" to user.birthDate,
                "private/email" to user.email,
                "profilePicUrl" to user.profilePicUrl,
                "qrCodeUrl" to user.qrCodeUrl,
                "username" to user.username,
                "accountStatus" to user.accountStatus, // need a user bio!
                "eventsAttendeeList" to user.eventsAttendeeList,
                "eventsHostList" to user.eventsHostList.add(newEventId))
        val newUser = User(userValues, uid)
        userRepository.updateUser(newUser)
      }
      is Resource.Failure -> {
        Log.d("CreateEventViewModel", "Failed to find user ${uid} in database")
      }
    }
  }

  fun validateFields(state: MutableStateFlow<CreateEventUiState>): Boolean {
    state.value =
        state.value.copy(
            eventNameIsError = state.value.eventName.isBlank(),
            eventDescriptionIsError = state.value.eventDescription.isBlank(),
            startDateIsError = !isValidDate(state.value.startDate),
            endDateIsError = !isValidDate(state.value.endDate),
            startTimeIsError = !isValidTime(state.value.startTime),
            endTimeIsError = !isValidTime(state.value.endTime),
            locationIsError = state.value.location.isBlank() || (fetchLocation(state.value.location, state) == null),
            ticketNameIsError = state.value.ticketName.isBlank(),
            ticketCapacityIsError = !isValidNumber(state.value.ticketCapacity),
            ticketPriceIsError = !isValidNumber(state.value.ticketPrice))


    return !state.value.eventNameIsError &&
        !state.value.eventDescriptionIsError &&
        !state.value.startDateIsError &&
        !state.value.endDateIsError &&
        !state.value.startTimeIsError &&
        !state.value.endTimeIsError &&
        !state.value.locationIsError &&
        !state.value.ticketNameIsError &&
        !state.value.ticketCapacityIsError &&
        !state.value.ticketPriceIsError
  }

  // Assuming simple validation functions exist:
  private fun isValidDate(date: String): Boolean {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    format.isLenient = false
    return try {
      format.parse(date)
      true
    } catch (e: Exception) {
      false
    }
  }

  private fun isValidTime(time: String): Boolean {
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    format.isLenient = false
    return try {
      format.parse(time)
      true
    } catch (e: Exception) {
      false
    }
  }

  private fun isValidNumber(number: String): Boolean {
    val parsedNumber: Int? = number.toIntOrNull()
    return (parsedNumber != null && parsedNumber > 0)
  }

  fun onEventNameChanged(eventName: String, state: MutableStateFlow<CreateEventUiState>) {
    state.value = state.value.copy(eventName = eventName)
  }

  fun onEventPhotoUriChanged(eventPhotoUri: Uri?, state: MutableStateFlow<CreateEventUiState>) {
    state.value = state.value.copy(eventPhotoUri = eventPhotoUri)
  }

  fun onEventDescriptionChanged(
      eventDescription: String,
      state: MutableStateFlow<CreateEventUiState>
  ) {
    state.value = state.value.copy(eventDescription = eventDescription)
  }

  fun onStartDateChanged(startDate: String, state: MutableStateFlow<CreateEventUiState>) {
    state.value = state.value.copy(startDate = startDate)
  }

  fun onEndDateChanged(endDate: String, state: MutableStateFlow<CreateEventUiState>) {
    state.value = state.value.copy(endDate = endDate)
  }

  fun onStartTimeChanged(startTime: String, state: MutableStateFlow<CreateEventUiState>) {
    state.value = state.value.copy(startTime = startTime)
  }

  fun onEndTimeChanged(endTime: String, state: MutableStateFlow<CreateEventUiState>) {
    state.value = state.value.copy(endTime = endTime)
  }

  fun onLocationChanged(location: String, state: MutableStateFlow<CreateEventUiState>) {
    state.value = state.value.copy(location = location)
  }

  fun onTicketNameChanged(ticketName: String, state: MutableStateFlow<CreateEventUiState>) {
    state.value = state.value.copy(ticketName = ticketName)
  }

  fun onTicketCapacityChanged(ticketCapacity: String, state: MutableStateFlow<CreateEventUiState>) {
    state.value = state.value.copy(ticketCapacity = ticketCapacity)
  }

  fun onTicketPriceChanged(ticketPrice: String, state: MutableStateFlow<CreateEventUiState>) {
    state.value = state.value.copy(ticketPrice = ticketPrice)
  }
}

data class CreateEventUiState(
    val eventName: String = "",
    val eventPhotoUri: Uri? = null,
    val eventDescription: String = "",
    val eventCategory: EventCategory = EventCategory.COMMUNITY,
    val startDate: String = "",
    val endDate: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val location: String = "",
    val ticketName: String = "",
    val ticketCapacity: String = "",
    val ticketPrice: String = "",
    val organiserList: MutableList<String> = mutableListOf(),
    val eventNameIsError: Boolean = false,
    val eventDescriptionIsError: Boolean = false,
    val startDateIsError: Boolean = false,
    val endDateIsError: Boolean = false,
    val startTimeIsError: Boolean = false,
    val endTimeIsError: Boolean = false,
    val locationIsError: Boolean = false,
    val ticketNameIsError: Boolean = false,
    val ticketCapacityIsError: Boolean = false,
    val ticketPriceIsError: Boolean = false
)
