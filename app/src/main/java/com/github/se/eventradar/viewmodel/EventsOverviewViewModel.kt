package com.github.se.eventradar.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventList
import com.github.se.eventradar.model.repository.event.IEventRepository
import com.github.se.eventradar.model.repository.user.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
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
        Log.d("EventsOverviewViewModel", "Error fetching user document: ${userResponse.throwable.message}")
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
    val isFilterDialogOpen: Boolean = false,
    var radiusInputFilter: Double = -1.0,
    var freeEventsFilter: Boolean = false,
    val categorySelectionFilter: List<EventCategory> = emptyList(),
    val viewList: Boolean = true,
    val tab: Tab = Tab.BROWSE,
    val userLoggedIn: Boolean = false,
)

enum class Tab {
  BROWSE,
  UPCOMING
}
