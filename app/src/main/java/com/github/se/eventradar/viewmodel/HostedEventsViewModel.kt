package com.github.se.eventradar.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventList
import com.github.se.eventradar.model.repository.event.IEventRepository
import com.github.se.eventradar.model.repository.user.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HostedEventsViewModel
@Inject
constructor(
    private val eventRepository: IEventRepository,
    private val userRepository: IUserRepository
) : ViewModel() {
  private val _uiState = MutableStateFlow(HostedEventsUiState())
  val uiState: StateFlow<HostedEventsUiState> = _uiState

  fun getHostedEvents() {
    viewModelScope.launch {
      userRepository.getCurrentUserId().let { userIdResource ->
        when (userIdResource) {
          is Resource.Success -> {
            val uid = userIdResource.data
            getUserHostedEvents(uid)
          }
          is Resource.Failure -> {
            Log.d("HostedEventsViewModel", "User not logged in or error fetching user ID")
            _uiState.update { it.copy(eventList = EventList(emptyList(), emptyList(), null)) }
          }
        }
      }
    }
  }

  private suspend fun getUserHostedEvents(uid: String) {
    when (val userResponse = userRepository.getUser(uid)) {
      is Resource.Success -> {
        val user = userResponse.data!!
        val eventsHostList = user.eventsHostList
        if (eventsHostList.isNotEmpty()) {
          when (val events = eventRepository.getEventsByIds(eventsHostList.toList())) {
            is Resource.Success -> {
              _uiState.update {
                it.copy(
                    eventList =
                        EventList(events.data, events.data, _uiState.value.eventList.selectedEvent))
              }
              filterHostedEvents()
            }
            is Resource.Failure -> {
              Log.d("HostedEventsViewModel", "Error getting hosted events for $uid")
              _uiState.update { it.copy(eventList = EventList(emptyList(), emptyList(), null)) }
            }
          }
        } else {
          _uiState.update { it.copy(eventList = EventList(emptyList(), emptyList(), null)) }
        }
      }
      is Resource.Failure -> {
        Log.d("HostedEventsViewModel", "Error fetching user document")
        _uiState.update { it.copy(eventList = EventList(emptyList(), emptyList(), null)) }
      }
    }
  }

  fun onViewListStatusChanged(state: MutableStateFlow<HostedEventsUiState> = _uiState) {
    state.update { currentState ->
      currentState.copy(isFilterDialogOpen = false, viewList = !currentState.viewList)
    }
  }

  fun onSearchQueryChanged(query: String, state: MutableStateFlow<HostedEventsUiState> = _uiState) {
    state.update { currentState -> currentState.copy(searchQuery = query) }
    filterHostedEvents()
  }

  fun onSearchActiveChanged(
      isSearchActive: Boolean,
      state: MutableStateFlow<HostedEventsUiState> = _uiState
  ) {
    state.update { currentState ->
      currentState.copy(isFilterDialogOpen = false, isSearchActive = isSearchActive)
    }
  }
  fun onFilterDialogOpen(state: MutableStateFlow<HostedEventsUiState> = _uiState) {
    state.update { currentState ->
      currentState.copy(isFilterDialogOpen = !currentState.isFilterDialogOpen)
    }
  }

  fun onRadiusQueryChanged(
      radius: String,
      state: MutableStateFlow<HostedEventsUiState> = _uiState
  ) {
    state.update { currentState -> currentState.copy(radiusQuery = radius) }
  }

  fun onFreeSwitchChanged(state: MutableStateFlow<HostedEventsUiState> = _uiState) {
    state.update { currentState ->
      currentState.copy(isFreeSwitchOn = !currentState.isFreeSwitchOn)
    }
  }

  fun onFilterApply(state: MutableStateFlow<HostedEventsUiState> = _uiState) {
    state.update { currentState -> currentState.copy(isFilterActive = true) }
    filterHostedEvents()
  }

  fun onUserLocationChanged(location: Location) {
    _uiState.update { currentState -> currentState.copy(userLocation = location) }
  }

  fun filterHostedEvents() {
    val eventList = _uiState.value.eventList.allEvents

    if (_uiState.value.radiusQuery.isNotEmpty() && _uiState.value.radiusQuery.toDouble() < 0.0) {
      Log.d("HostedEventsViewModel", "Invalid radius query: ${_uiState.value.radiusQuery}")
      _uiState.value = _uiState.value.copy(radiusQuery = "")
    }

    val filteredEvents =
        eventList.filter { event ->
          // Search filter
          event.eventName.contains(_uiState.value.searchQuery, ignoreCase = true) &&
              // Radius filter
              (_uiState.value.radiusQuery.isEmpty() ||
                  calculateDistance(_uiState.value.userLocation, event.location) <=
                      _uiState.value.radiusQuery.toDouble()) &&
              // Free event filter
              (!_uiState.value.isFreeSwitchOn || event.ticket.price == 0.0) &&
              // Category filter
              (_uiState.value.categoriesCheckedList.isEmpty() ||
                  _uiState.value.categoriesCheckedList.contains(event.category))
        }

    // Update the UI state with the filtered events
    _uiState.update {
      it.copy(eventList = _uiState.value.eventList.copy(filteredEvents = filteredEvents))
    }
  }

  // Calculates distance between 2 coordinate points based on Haversine formula
  // Accounts for earth's curvature
  private fun calculateDistance(location1: Location, location2: Location): Double {
    val lat1 = Math.toRadians(location1.latitude)
    val lon1 = Math.toRadians(location1.longitude)
    val lat2 = Math.toRadians(location2.latitude)
    val lon2 = Math.toRadians(location2.longitude)

    val diffLon = lon2 - lon1
    val diffLat = lat2 - lat1

    val a = sin(diffLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(diffLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    // Radius of the Earth in km (since radius input is in km)
    val radius = 6371.0

    return radius * c
  }
}

data class HostedEventsUiState(
    val eventList: EventList = EventList(emptyList(), emptyList(), null),
    var viewList: Boolean = true,
    override val searchQuery: String = "",
    override val isSearchActive: Boolean = false,
    override val isFilterDialogOpen: Boolean = false,
    override val isFilterActive: Boolean = false,
    override val radiusQuery: String = "",
    override val isFreeSwitchOn: Boolean = false,
    override val categoriesCheckedList: MutableSet<EventCategory> = mutableSetOf(),
    override val userLocation: Location = Location(46.519962, 6.56637, "EPFL"),
) : SearchFilterUiState()
