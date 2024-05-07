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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EventsOverviewViewModel
@Inject
constructor(
    private val eventRepository: IEventRepository,
    private val userRepository: IUserRepository
) : ViewModel() {
  private val _uiState = MutableStateFlow(EventsOverviewUiState())
  val uiState: StateFlow<EventsOverviewUiState> = _uiState.asStateFlow()

  init {
    checkUserLoginStatus()
  }

  fun onSearchQueryChanged(query: String) {
    _uiState.value = _uiState.value.copy(searchQuery = query)
  }

  fun onSearchActiveChanged(isSearchActive: Boolean) {
    _uiState.value = _uiState.value.copy(isSearchActive = isSearchActive)
  }

  fun onFilterDialogOpen(state: MutableStateFlow<EventsOverviewUiState> = _uiState) {
    state.value = state.value.copy(isFilterDialogOpen = !state.value.isFilterDialogOpen)
  }

  fun onRadiusQueryChanged(radius: String) {
    _uiState.value = _uiState.value.copy(radiusQuery = radius)
  }

  fun onFreeSwitchChanged(state: MutableStateFlow<EventsOverviewUiState> = _uiState) {
    state.value = state.value.copy(isFreeSwitchOn = !state.value.isFreeSwitchOn)
  }

  // TO DO: Function used to convert category selection to MVVM
  //  fun onCategorySelectionChanged(category: EventCategory, isChecked: Boolean) {
  //    val categoriesCheckedList = _uiState.value.categoriesCheckedList
  //    Log.d("CategorySelection", "categoriesCheckedList $categoriesCheckedList")
  //
  //    if (isChecked) {
  //      categoriesCheckedList.remove(category)
  //    } else {
  //      categoriesCheckedList.add(category)
  //    }
  //    _uiState.value = _uiState.value.copy(categoriesCheckedList = categoriesCheckedList)
  //    Log.d("CategorySelection", "Category vm ${_uiState.value.categoriesCheckedList}")
  //  }

  fun onFilterApply(state: MutableStateFlow<EventsOverviewUiState> = _uiState) {
    state.value = state.value.copy(isFilterActive = true)
    filterEvents()
  }

  fun filterEvents() {
    // Filter based on search query
    val query = _uiState.value.searchQuery
    val filteredEventsSearch =
        _uiState.value.eventList.allEvents.filter {
          it.eventName.contains(query, ignoreCase = true)
        }

    // Filter based on radius query
    val radiusQuery = _uiState.value.radiusQuery

    // For now, use a fixed user location - but this should be updated to use the user's actual
    // location
    val userLocation = Location(latitude = 38.92, longitude = 78.78, address = "Ecublens")
    val filteredEventsRadius =
        if (radiusQuery == "") {
          _uiState.value.eventList.allEvents
        } else {
          _uiState.value.eventList.allEvents.filter { event ->
            val distance = calculateDistance(userLocation, event.location)
            distance <= radiusQuery.toDouble()
          }
        }

    // Filter based on free switch
    val isFreeSwitchOn = _uiState.value.isFreeSwitchOn
    val filteredEventsFree =
        _uiState.value.eventList.allEvents.filter {
          if (isFreeSwitchOn) {
            it.ticket.price == 0.0
          } else {
            true
          }
        }

    // Filter based on categories selected
    val categoriesCheckedList = _uiState.value.categoriesCheckedList
    val filteredEventsCategory =
        _uiState.value.eventList.allEvents.filter { event ->
          categoriesCheckedList.any { it == event.category }
        }

    val filteredEvents =
        filteredEventsSearch
            .intersect(filteredEventsRadius.toSet())
            .intersect(filteredEventsFree.toSet())
            .intersect(filteredEventsCategory.toSet())

    _uiState.value =
        _uiState.value.copy(
            eventList = _uiState.value.eventList.copy(filteredEvents = filteredEvents.toList()))
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

  fun getUpcomingEvents() {
    viewModelScope.launch {
      userRepository.getCurrentUserId().let { userIdResource ->
        when (userIdResource) {
          is Resource.Success -> {
            val uid = userIdResource.data
            getUserUpcomingEvents(uid)
          }
          is Resource.Failure -> {
            Log.d(
                "EventsOverviewViewModel",
                "Error fetching user ID: ${userIdResource.throwable.message}")
            _uiState.value =
                _uiState.value.copy(eventList = EventList(emptyList(), emptyList(), null))
          }
        }
      }
    }
  }

  private suspend fun getUserUpcomingEvents(uid: String) {
    when (val userResponse = userRepository.getUser(uid)) {
      is Resource.Success -> {
        val user = userResponse.data!!
        val attendeeList = user.eventsAttendeeSet.toList()
        if (attendeeList.isNotEmpty()) {
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
        _uiState.value = _uiState.value.copy(eventList = EventList(emptyList(), emptyList(), null))
      }
    }
  }

  fun checkUserLoginStatus() {
    viewModelScope.launch {
      val userIdResource = userRepository.getCurrentUserId()
      val isLoggedIn = userIdResource is Resource.Success
      _uiState.update { currentState -> currentState.copy(userLoggedIn = isLoggedIn) }
    }
  }

  fun onTabChanged(tab: Tab, state: MutableStateFlow<EventsOverviewUiState> = _uiState) {
    state.value = state.value.copy(tab = tab)
  }

  fun onViewListStatusChanged(state: MutableStateFlow<EventsOverviewUiState> = _uiState) {
    state.value = state.value.copy(viewList = !state.value.viewList)
  }

  fun changeFilterDialogOpen() {
    val current = _uiState.value.isFilterDialogOpen
    _uiState.update { currentState -> currentState.copy(isFilterDialogOpen = !current) }
  }

  fun onSearchQueryChange(newQuery: String) {
    _uiState.update { currentState -> currentState.copy(searchQuery = newQuery) }
  }
}

data class EventsOverviewUiState(
    val eventList: EventList = EventList(emptyList(), emptyList(), null),
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val isFilterDialogOpen: Boolean = false,
    val isFilterActive: Boolean = false,
    val radiusQuery: String = "",
    val isFreeSwitchOn: Boolean = true,
    val categoriesCheckedList: MutableSet<EventCategory> =
        mutableSetOf(*enumValues<EventCategory>()),
    val viewList: Boolean = true,
    val tab: Tab = Tab.BROWSE,
    val userLoggedIn: Boolean = false,
)

enum class Tab {
  BROWSE,
  UPCOMING
}
