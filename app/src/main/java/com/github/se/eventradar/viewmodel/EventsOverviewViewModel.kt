package com.github.se.eventradar.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.event.Event
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
    observeAllEvents()
    observeUpcomingEvents()
  }

  fun onSearchQueryChanged(
      query: String,
      state: MutableStateFlow<EventsOverviewUiState> = _uiState
  ) {
    state.value = state.value.copy(searchQuery = query)
    filterEvents()
  }

  fun onSearchActiveChanged(
      isSearchActive: Boolean,
      state: MutableStateFlow<EventsOverviewUiState> = _uiState
  ) {
    state.value = state.value.copy(isFilterDialogOpen = false)
    state.value = state.value.copy(isSearchActive = isSearchActive)
  }

  fun onFilterDialogOpen(state: MutableStateFlow<EventsOverviewUiState> = _uiState) {
    state.value = state.value.copy(isFilterDialogOpen = !state.value.isFilterDialogOpen)
  }

  fun onRadiusQueryChanged(
      radius: String,
      state: MutableStateFlow<EventsOverviewUiState> = _uiState
  ) {
    state.value = state.value.copy(radiusQuery = radius)
  }

  fun onFreeSwitchChanged(state: MutableStateFlow<EventsOverviewUiState> = _uiState) {
    state.value = state.value.copy(isFreeSwitchOn = !state.value.isFreeSwitchOn)
  }

  fun onFilterApply(state: MutableStateFlow<EventsOverviewUiState> = _uiState) {
    state.value = state.value.copy(isFilterActive = true)
    filterEvents()
  }

  fun filterEvents() {
    // Select the correct event list based on the active tab
    val eventList =
        if (_uiState.value.tab == Tab.BROWSE) {
              _uiState.value.eventList
            } else {
              _uiState.value.upcomingEventList
            }
            .allEvents

    // User location should ideally be dynamic but is fixed for the purpose of this example
    val userLocation = Location(latitude = 38.92, longitude = 78.78, address = "Ecublens")

    val filteredEvents =
        eventList.filter { event ->
          // Search filter
          event.eventName.contains(_uiState.value.searchQuery, ignoreCase = true) &&
              // Radius filter
              (_uiState.value.radiusQuery.isEmpty() ||
                  calculateDistance(userLocation, event.location) <=
                      _uiState.value.radiusQuery.toDouble()) &&
              // Free event filter
              (!_uiState.value.isFreeSwitchOn || event.ticket.price == 0.0) &&
              // Category filter
              (_uiState.value.categoriesCheckedList.isEmpty() ||
                  _uiState.value.categoriesCheckedList.contains(event.category))
        }

    // Update the UI state with the filtered events for the respective tab
    _uiState.value =
        if (_uiState.value.tab == Tab.BROWSE) {
          _uiState.value.copy(
              eventList = _uiState.value.eventList.copy(filteredEvents = filteredEvents))
        } else {
          _uiState.value.copy(
              upcomingEventList =
                  _uiState.value.upcomingEventList.copy(filteredEvents = filteredEvents))
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

  fun getEvents() {
    viewModelScope.launch {
      when (val response = eventRepository.getEvents()) {
        is Resource.Success -> {
          _uiState.value =
              _uiState.value.copy(
                  eventList =
                      EventList(
                          response.data, response.data, _uiState.value.eventList.selectedEvent))
          filterEvents()
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
                _uiState.value.copy(upcomingEventList = EventList(emptyList(), emptyList(), null))
          }
        }
      }
    }
  }
    //mas,mns

  private suspend fun getUserUpcomingEvents(uid: String) {
    when (val userResponse = userRepository.getUser(uid)) {
      is Resource.Success -> {
        val user = userResponse.data!!
        val attendeeList = user.eventsAttendeeList.toList()
        if (attendeeList.isNotEmpty()) {
          when (val events = eventRepository.getEventsByIds(attendeeList)) {
            is Resource.Success -> {
              _uiState.value =
                  _uiState.value.copy(
                      upcomingEventList =
                          EventList(
                              events.data, events.data, _uiState.value.eventList.selectedEvent))
              filterEvents()
            }
            is Resource.Failure -> {
              Log.d("EventsOverviewViewModel", "Error getting events for $uid")
              _uiState.value =
                  _uiState.value.copy(upcomingEventList = EventList(emptyList(), emptyList(), null))
            }
          }
        } else {
          _uiState.value =
              _uiState.value.copy(upcomingEventList = EventList(emptyList(), emptyList(), null))
        }
      }
      is Resource.Failure -> {
        Log.d("EventsOverviewViewModel", "Error fetching user document")
        _uiState.value =
            _uiState.value.copy(upcomingEventList = EventList(emptyList(), emptyList(), null))
      }
    }
  }

  private fun observeAllEvents() {
    viewModelScope.launch {
      eventRepository.observeAllEvents().collect { resource ->
        when (resource) {
          is Resource.Success -> {
            _uiState.update { currentState ->
              currentState.copy(
                  eventList =
                      EventList(
                          resource.data, resource.data, _uiState.value.eventList.selectedEvent))
            }
            filterEvents()
          }
          is Resource.Failure ->
              Log.d("EventsOverviewViewModel", "Failed to fetch events: ${resource.throwable}")
        }
      }
    }
  }

  private fun observeUpcomingEvents() {
    viewModelScope.launch {
      val userIdResource = userRepository.getCurrentUserId()
      handleUserIdResource(userIdResource)
    }
  }

  private suspend fun handleUserIdResource(userIdResource: Resource<String>) {
    when (userIdResource) {
      is Resource.Success -> {
        val uid = userIdResource.data
        eventRepository.observeUpcomingEvents(uid).collect { eventsResource ->
          handleEventsResource(eventsResource)
        }
      }
      is Resource.Failure -> {
        Log.d(
            "EventsOverviewViewModel",
            "Error fetching user ID: ${userIdResource.throwable.message}")
      }
    }
  }

  private fun handleEventsResource(eventsResource: Resource<List<Event>>) {
    when (eventsResource) {
      is Resource.Success -> {
        updateUiState(eventsResource.data)
        filterEvents()
      }
      is Resource.Failure -> {
        Log.d(
            "EventsOverviewViewModel",
            "Failed to fetch upcoming events: ${eventsResource.throwable.message}")
      }
    }
  }

  private fun updateUiState(events: List<Event>) {
    _uiState.update { currentState ->
      currentState.copy(
          upcomingEventList =
              EventList(
                  allEvents = events,
                  filteredEvents = events,
                  selectedEvent = currentState.upcomingEventList.selectedEvent))
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
    // Reset search and filter when tab is changed
    state.value =
        state.value.copy(
            searchQuery = "",
            isSearchActive = false,
            isFilterDialogOpen = false,
            isFilterActive = false,
            radiusQuery = "",
            isFreeSwitchOn = false,
            categoriesCheckedList = mutableSetOf(),
            tab = tab)
  }

  fun onViewListStatusChanged(state: MutableStateFlow<EventsOverviewUiState> = _uiState) {
    state.value = state.value.copy(isFilterDialogOpen = false)
    state.value = state.value.copy(viewList = !state.value.viewList)
  }
}

data class EventsOverviewUiState(
    val eventList: EventList = EventList(emptyList(), emptyList(), null),
    val upcomingEventList: EventList = EventList(emptyList(), emptyList(), null),
    override val searchQuery: String = "",
    override val isSearchActive: Boolean = false,
    override val isFilterDialogOpen: Boolean = false,
    override val isFilterActive: Boolean = false,
    override val radiusQuery: String = "",
    override val isFreeSwitchOn: Boolean = false,
    override val categoriesCheckedList: MutableSet<EventCategory> = mutableSetOf(),
    val viewList: Boolean = true,
    val tab: Tab = Tab.BROWSE,
    val userLoggedIn: Boolean = false,
) : SearchFilterUiState()

enum class Tab {
  BROWSE,
  UPCOMING
}
