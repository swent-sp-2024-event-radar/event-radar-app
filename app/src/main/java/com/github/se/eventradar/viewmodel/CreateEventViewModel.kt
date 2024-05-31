package com.github.se.eventradar.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.repository.event.IEventRepository
import com.github.se.eventradar.model.repository.location.ILocationRepository
import com.github.se.eventradar.model.repository.user.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CreateEventViewModel
@Inject
constructor(
    private val locationRepository: ILocationRepository,
    private val eventRepository: IEventRepository,
    private val userRepository: IUserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateEventUiState())
    val uiState: StateFlow<CreateEventUiState> = _uiState

    /*
    This function adds a new event based on the user input.
    It firstly generates a new eventId.
    Next, it uploads the event photo given by the user onto firestore and retrieves the link to this image.
    Afterward, a hashmap is generated based on the user information.
    Finally, the event firestore database is updated with the new event, and the user's event_hosting list is updated.
     */

    fun addEvent(state: MutableStateFlow<CreateEventUiState> = _uiState) {
        viewModelScope.launch {
            userRepository.getCurrentUserId().let { userIdResource ->
                when (userIdResource) {
                    is Resource.Success -> {
                        val newEventId =
                            when (val result = eventRepository.getUniqueEventId()) {
                                is Resource.Success -> {
                                    result.data
                                }
                                is Resource.Failure -> {
                                    Log.d("CreateEventViewModel", "Error Generating Event Id")
                                    resetStateAndSetAddEventFailure(true, state)
                                    return@launch
                                }
                            }

                        val uid = userIdResource.data
                        val fetchedLocation =
                            when (val result = locationRepository.fetchLocation(state.value.location)) {
                                is Resource.Success -> {
                                    result.data[0]
                                }
                                is Resource.Failure -> {
                                    Log.d("CreateEventViewModel", "Location Fetching Failed")
                                    resetStateAndSetAddEventFailure(true, state)
                                    return@launch
                                }
                            }
                        val eventPhotoUri =
                            state.value.eventPhotoUri
                                ?: Uri.parse("android.resource://com.github.se.eventradar/drawable/placeholder")
                        userRepository.uploadImage(eventPhotoUri, newEventId, "Event_Pictures")
                        val eventPhotoUrl =
                            when (val result = userRepository.getImage(newEventId, "Event_Pictures")) {
                                is Resource.Success -> {
                                    result.data
                                }
                                is Resource.Failure -> {
                                    Log.d("CreateEventViewModel", "Fetching Profile Picture Error")
                                    resetStateAndSetAddEventFailure(true, state)
                                    return@launch
                                }
                            }

                        val eventHashMap =
                            hashMapOf(
                                "name" to state.value.eventName,
                                "photo_url" to eventPhotoUrl,
                                "start" to state.value.startDate + "T" + state.value.startTime,
                                "end" to state.value.endDate + "T" + state.value.endTime,
                                "location_lat" to fetchedLocation.latitude,
                                "location_lng" to fetchedLocation.longitude,
                                "location_name" to fetchedLocation.address,
                                "description" to state.value.eventDescription,
                                "ticket_name" to state.value.ticketName,
                                "ticket_price" to state.value.ticketPrice.toDouble(),
                                "ticket_capacity" to state.value.ticketCapacity.toLong(),
                                "ticket_purchases" to 0.toLong(),
                                "main_organiser" to uid,
                                "organisers_list" to state.value.organiserList.map{user -> user.userId},
                                "attendees_list" to mutableListOf<String>(),
                                "category" to state.value.eventCategory,
                            )
                        //get the userIds of the hosts
                        val newEvent = Event(eventHashMap, newEventId)
                        addUserEvent(uid, newEvent, state)

                        val organisers = state.value.organiserList
                        for (organiser in organisers){
                            addUserEvent(organiser.userId, newEvent, state)
                        }
                        state.value = state.value.copy(showAddEventSuccess = true)
                    }
                    is Resource.Failure -> {
                        Log.d("CreateEventViewModel", "User not logged in or error fetching user ID")
                        resetStateAndSetAddEventFailure(true, state)
                    }
                }
            }
        }
    }

    private suspend fun addUserEvent(
        uid: String,
        newEvent: Event,
        state: MutableStateFlow<CreateEventUiState> = _uiState,
    ) {
        // Adds the event
        viewModelScope.launch {
            when (eventRepository.addEvent(newEvent)) {
                is Resource.Success -> {
                    Log.d("CreateEventViewModel", "Successfully added event")
                    updateUserHostList(uid, newEvent.fireBaseID, state)
                }
                is Resource.Failure -> {
                    Log.d("CreateEventViewModel", "Failed to add event")
                    state.value = CreateEventUiState()
                }
            }
        }
    }

    private suspend fun updateUserHostList(
        uid: String,
        newEventId: String,
        state: MutableStateFlow<CreateEventUiState> = _uiState,
    ) {
        // Updates the user list
            when (val userResource = userRepository.getUser(uid)) {
                is Resource.Success -> {
                    val user = userResource.data!!
                    user.eventsHostList.add(newEventId)
                    val userValues = user.toMap()
                    val updatedUser = User(userValues, uid)
                    userRepository.updateUser(updatedUser)
                    Log.d("CreateEventViewModel", "Successfully updated user ${uid} host list")
                }
                is Resource.Failure -> {
                    Log.d("CreateEventViewModel", "Failed to find user ${uid} in database")
                    state.value = CreateEventUiState()
                }

        }

    }
    fun getHostFriendList(state: MutableStateFlow<CreateEventUiState> = _uiState){
        viewModelScope.launch {
            userRepository.getCurrentUserId().let { userIdResource ->
                when (userIdResource) {
                    is Resource.Success -> {
                        val uid = userIdResource.data
                        when (val currentUserResource = userRepository.getUser(uid)){
                            is Resource.Success -> {
                                val currentUser = currentUserResource.data
                                //need friends username list
                                val friendList = mutableListOf<User>()
                                currentUser!!.friendsList.forEach{
                                        friend ->
                                    when (val currentFriendResource = userRepository.getUser(friend)){
                                        is Resource.Success -> {
                                            friendList.add(currentFriendResource.data!!)
                                        }
                                        is Resource.Failure -> {
                                            Log.d("CreateEventViewModel", "Failed to find friend ${uid} in database")
                                        }
                                    }
                                }
                                state.value = state.value.copy(hostFriendsList = friendList)
                            }
                            is Resource.Failure -> {
                                Log.d("CreateEventViewModel", "User ${uid} does not exist in database")
                            }
                        }
                    }
                    is Resource.Failure -> {
                        Log.d("CreateEventViewModel", "User not logged in or error fetching user ID")
                        resetStateAndSetAddEventFailure(true, state)
                    }
                }
            }
        }
    }

    // Upon clicking Search Icon, the location is updated in the CreateEvent Screen
    fun updateListOfLocations(state: MutableStateFlow<CreateEventUiState> = _uiState) {
        viewModelScope.launch {
            when (val result = locationRepository.fetchLocation(state.value.location)) {
                is Resource.Success -> {
                    state.value = state.value.copy(listOfLocations = result.data)
                }
                is Resource.Failure -> {
                    state.value = state.value.copy(locationIsError = true)
                }
            }
        }
    }

    fun resetStateAndSetAddEventFailure(
        newErrorState: Boolean,
        state: MutableStateFlow<CreateEventUiState> = _uiState
    ) {
        state.value = CreateEventUiState(showAddEventFailure = newErrorState)
    }
    fun resetStateAndSetAddEventSuccess(newSuccessState : Boolean, state: MutableStateFlow<CreateEventUiState> = _uiState){
        state.value = CreateEventUiState(showAddEventSuccess = newSuccessState)
    }

    fun validateFields(state: MutableStateFlow<CreateEventUiState> = _uiState): Boolean {
        state.value =
            state.value.copy(
                eventNameIsError = state.value.eventName.isBlank(),
                eventDescriptionIsError = state.value.eventDescription.isBlank(),
                startDateIsError = !isValidDate(state.value.startDate),
                endDateIsError = !isValidDate(state.value.endDate),
                startTimeIsError = !isValidTime(state.value.startTime),
                endTimeIsError = !isValidTime(state.value.endTime),
                locationIsError = state.value.location.isBlank(),
                ticketNameIsError = state.value.ticketName.isBlank(),
                ticketCapacityIsError = !isValidNumber(state.value.ticketCapacity),
                ticketPriceIsError = !isValidDouble(state.value.ticketPrice))

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
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
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

    private fun isValidDouble(double: String): Boolean {
        val parsedNumber: Double? = double.toDoubleOrNull()
        return (parsedNumber != null && parsedNumber >= 0.0)
    }

    fun onEventNameChanged(
        eventName: String,
        state: MutableStateFlow<CreateEventUiState> = _uiState
    ) {
        state.value = state.value.copy(eventName = eventName)
    }

    fun onEventCategoryChanged(
        eventCategory: String,
        state: MutableStateFlow<CreateEventUiState> = _uiState
    ) {
        state.value = state.value.copy(eventCategory = eventCategory)
    }

    fun onEventPhotoUriChanged(
        eventPhotoUri: Uri?,
        state: MutableStateFlow<CreateEventUiState> = _uiState
    ) {
        state.value = state.value.copy(eventPhotoUri = eventPhotoUri)
    }

    fun onOrganiserListChanged(
        organiserList: List<User>,
        state: MutableStateFlow<CreateEventUiState> = _uiState
    ) {
        state.value = state.value.copy(organiserList = organiserList)
    }

    fun onEventDescriptionChanged(
        eventDescription: String,
        state: MutableStateFlow<CreateEventUiState> = _uiState
    ) {
        state.value = state.value.copy(eventDescription = eventDescription)
    }

    fun onStartDateChanged(
        startDate: String,
        state: MutableStateFlow<CreateEventUiState> = _uiState
    ) {
        state.value = state.value.copy(startDate = startDate)
    }

    fun onEndDateChanged(endDate: String, state: MutableStateFlow<CreateEventUiState> = _uiState) {
        state.value = state.value.copy(endDate = endDate)
    }

    fun onStartTimeChanged(
        startTime: String,
        state: MutableStateFlow<CreateEventUiState> = _uiState
    ) {
        state.value = state.value.copy(startTime = startTime)
    }

    fun onEndTimeChanged(endTime: String, state: MutableStateFlow<CreateEventUiState> = _uiState) {
        state.value = state.value.copy(endTime = endTime)
    }

    fun onLocationChanged(location: String, state: MutableStateFlow<CreateEventUiState> = _uiState) {
        state.value = state.value.copy(location = location)
    }

    fun onTicketNameChanged(
        ticketName: String,
        state: MutableStateFlow<CreateEventUiState> = _uiState
    ) {
        state.value = state.value.copy(ticketName = ticketName)
    }

    fun onTicketCapacityChanged(
        ticketCapacity: String,
        state: MutableStateFlow<CreateEventUiState> = _uiState
    ) {
        Log.d("Ticket Capacity", state.value.ticketCapacity)
        state.value = state.value.copy(ticketCapacity = ticketCapacity)
    }

    fun onTicketPriceChanged(
        ticketPrice: String,
        state: MutableStateFlow<CreateEventUiState> = _uiState
    ) {
        state.value = state.value.copy(ticketPrice = ticketPrice)
    }
}

data class CreateEventUiState(
    val eventName: String = "",
    val eventPhotoUri: Uri? = null,
    val eventDescription: String = "",
    val eventCategory: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val location: String = "",
    val ticketName: String = "",
    val ticketCapacity: String = "",
    val ticketPrice: String = "",
    val organiserList: List<User> = emptyList(),
    val eventNameIsError: Boolean = false,
    val eventDescriptionIsError: Boolean = false,
    val startDateIsError: Boolean = false,
    val endDateIsError: Boolean = false,
    val startTimeIsError: Boolean = false,
    val endTimeIsError: Boolean = false,
    val locationIsError: Boolean = false,
    val ticketNameIsError: Boolean = false,
    val ticketCapacityIsError: Boolean = false,
    val ticketPriceIsError: Boolean = false,
    val eventCategoryIsError : Boolean = false,
    val listOfLocations: List<Location> = emptyList(),
    val hostFriendsList : List<User> = emptyList(),
    val showAddEventSuccess : Boolean = false,
    val showAddEventFailure : Boolean = false,
)
