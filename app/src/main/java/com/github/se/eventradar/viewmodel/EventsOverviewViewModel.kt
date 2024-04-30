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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@HiltViewModel
class EventsOverviewViewModel
@Inject
constructor(
    private val eventRepository: IEventRepository,
    private val userRepository: IUserRepository
) : ViewModel() {
  private val _uiState = MutableStateFlow(EventsOverviewUiState())
  val uiState: StateFlow<EventsOverviewUiState> = _uiState

  fun onSearchQueryChanged(query: String) {
    _uiState.value = _uiState.value.copy(searchQuery = query)
    filterEvents()
  }
    fun onSearchActiveChanged(isSearchActive: Boolean) {
        _uiState.value = _uiState.value.copy(isSearchActive = isSearchActive)
    }

    fun onFilterDialogOpen(isFilterDialogOpen: Boolean) {
        _uiState.value = _uiState.value.copy(isFilterDialogOpen = isFilterDialogOpen)
    }

    fun onFilterApply(isFilterActive: Boolean) {
        _uiState.value = _uiState.value.copy(isFilterActive = isFilterActive)
        filterEvents()
    }

    fun onRadiusQueryChanged(radius: String) {
        _uiState.value = _uiState.value.copy(radiusQuery = radius)
        Log.d("UiState", "Updated UI state: ${_uiState.value.radiusQuery}")
    }

  private fun filterEvents() {
      // Filter based on search query
    val searchQuery = _uiState.value.searchQuery
      val filteredEventsSearch =
        _uiState.value.eventList.filteredEvents.filter {
          it.eventName.contains(searchQuery, ignoreCase = true)
        }
      _uiState.value =
          _uiState.value.copy(
              eventList = _uiState.value.eventList.copy(filteredEvents = filteredEventsSearch))

      // Filter based on radius query
        val radiusQuery = _uiState.value.radiusQuery.toDouble()
      // For now, use a fixed user location - but this should be updated to use the user's actual location
      val userLocation = Location(
          latitude = 38.92,
          longitude = 78.78,
          address = "Ecublens"
      )
      val filteredEventsRadius = _uiState.value.eventList.filteredEvents.filter {event ->
          val distance = calculateDistance(userLocation, event.location)
          distance <= radiusQuery
        }
        _uiState.value =
            _uiState.value.copy(
                eventList = _uiState.value.eventList.copy(filteredEvents = filteredEventsRadius))

      // Filter based on free switch
      val isFreeSwitchOn = _uiState.value.isFreeSwitchOn
        val filteredEventsFree = _uiState.value.eventList.filteredEvents.filter {
            if (isFreeSwitchOn) {
                it.ticket.price == 0.0
            } else {
                true
            }
        }
        _uiState.value =
            _uiState.value.copy(
                eventList = _uiState.value.eventList.copy(filteredEvents = filteredEventsFree))

      // Filter based on categories selected
      val categoriesCheckedList = _uiState.value.categoriesCheckedList
      val filteredEventsCategory = _uiState.value.eventList.filteredEvents.filter { event ->
          categoriesCheckedList.any { it == event.category }
      }
      _uiState.value = _uiState.value.copy(
          eventList = _uiState.value.eventList.copy(filteredEvents = filteredEventsCategory)
      )
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

  fun getEvents() {
    viewModelScope.launch {
      when (val response = eventRepository.getEvents()) {
        is Resource.Success -> {
          _uiState.value =
              _uiState.value.copy(
                  eventList =
                      EventList(
                          response.data, response.data, _uiState.value.eventList.selectedEvent))
        }
        is Resource.Failure ->
            Log.d("EventsOverviewViewModel", "Error getting events: ${response.throwable.message}")
      }
    }
  }

  fun getUpcomingEvents(uid: String) {
    viewModelScope.launch {
      when (val userResponse = userRepository.getUser(uid)) {
        is Resource.Success -> {
          val user = userResponse.data!!
          val attendeeList = user.eventsAttendeeList
          if (attendeeList.isNotEmpty()) { // will it ever be that
            when (val events = eventRepository.getEventsByIds(attendeeList)) {
              is Resource.Success -> {
                _uiState.value =
                    _uiState.value.copy(
                        eventList =
                            EventList(
                                events.data, events.data, _uiState.value.eventList.selectedEvent))
              }
              is Resource.Failure -> {
                Log.d("EventsOverviewViewModel", "Error getting events for $uid")
                _uiState.value =
                    _uiState.value.copy(eventList = EventList(emptyList(), emptyList(), null))
              }
            }
          } else {
            _uiState.value =
                _uiState.value.copy(eventList = EventList(emptyList(), emptyList(), null))
          }
        }
        is Resource.Failure -> {
          Log.d("EventsOverviewViewModel", "Error fetching user document")
          _uiState.value =
              _uiState.value.copy(eventList = EventList(emptyList(), emptyList(), null))
        }
      }
    }
  }
}

data class EventsOverviewUiState(
    val eventList: EventList = EventList(emptyList(), emptyList(), null),
    var searchQuery: String = "",
    var isSearchActive: Boolean = false,
    var isFilterDialogOpen: Boolean = false,
    var isFilterActive: Boolean = false,
    var radiusQuery: String = "",
    var isFreeSwitchOn: Boolean = true,
    var categoriesCheckedList: MutableSet<EventCategory> = mutableSetOf(),
    var viewList: Boolean = true,
    var tab: Tab = Tab.BROWSE,
)

enum class Tab {
  BROWSE,
  UPCOMING_EVENTS
}
