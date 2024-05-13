package com.github.se.eventradar.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventList
import com.github.se.eventradar.model.event.EventTicket
import com.github.se.eventradar.model.repository.event.IEventRepository
import com.github.se.eventradar.model.repository.user.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

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
                        addUserEvent(uid)
                    }
                    is Resource.Failure -> {
                        Log.d("CreateEventViewModel", "User not logged in or error fetching user ID")
                        _uiState.value =
                            _uiState.value.copy() //make all the fields null to ensure it doesn't preserve prev state
                    }
                }
            }
        }
    }

    //fun checkEventFields - is it null or not?

    private suspend fun addUserEvent(uid: String, state: MutableStateFlow<CreateEventUiState> = _uiState) {
        //update the user event list
        //update the event list
        viewModelScope.launch {
            val eventHashMap =
                hashMapOf(
                    "name" to state.value.eventName,
                    "photo_url" to state.value.eventPhoto,
                    "start" to state.value.start,
                    "end" to state.value.end,
                    "location_lat" to state.value.location.latitude,
                    "location_lng" to state.value.location.longitude,
                    "location_name" to state.value.location.address,
                    "description" to state.value.description,
                    "ticket_name" to state.value.ticket.name,
                    "ticket_price" to state.value.ticket.price,
                    "ticket_quantity" to state.value.ticket.capacity,
                    "main_organiser" to state.value.mainOrganiser,
                    "organisers_list" to state.value.organiserList,
                    "attendees_list" to state.value.attendeeList,
                    "category" to state.value.category,
                )
            val newEvent = Event(eventHashMap)

            when (eventRepository.addEvent(newEvent)){
                is Resource.Success ->{
                    Log.d("CreateEventViewModel", "Successfully added event")}
                is Resource.Failure ->{
                    Log.d("CreateEventViewModel", "Failed to add event")
                }
            }
        }
        }
}
//parse date to string, then add it to the firebase.
data class CreateEventUiState(
    var eventName: String = "",
    var eventPhoto: String = "",
    var start: LocalDateTime,
    var end: LocalDateTime,
    var location: Location,
    var description: String,
    var ticket: EventTicket,
    var mainOrganiser: String,
    val organiserList: MutableList<String>,
    val attendeeList: MutableList<String>,
    var category: EventCategory,
)
