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
import kotlinx.coroutines.launch

@HiltViewModel
class EventsOverviewViewModel
@Inject
constructor(
    private val eventRepository: IEventRepository,
    private val userRepository: IUserRepository
) : ViewModel() {
  private val _uiState = MutableStateFlow(EventsOverviewUiState())
  val uiState: StateFlow<EventsOverviewUiState> = _uiState

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
          _uiState.value =
              _uiState.value.copy(eventList = EventList(emptyList(), emptyList(), null))
        }
      }
    }
  }

  fun onTabChanged(tab: Tab, state: MutableStateFlow<EventsOverviewUiState> = _uiState) {
    state.value = state.value.copy(tab = tab)
  }

  fun onViewListStatusChanged(
      viewList: Boolean,
      state: MutableStateFlow<EventsOverviewUiState> = _uiState
  ) {
    state.value = _uiState.value.copy(viewList = viewList)
  }
}

data class EventsOverviewUiState(
    val eventList: EventList = EventList(emptyList(), emptyList(), null),
    var searchQuery: String = "",
    var isFilterDialogOpen: Boolean = false,
    var radiusInputFilter: Double = -1.0,
    var freeEventsFilter: Boolean = false,
    var categorySelectionFilter: List<EventCategory> = emptyList(),
    var viewList: Boolean = true,
    var tab: Tab = Tab.BROWSE,
)

enum class Tab {
  BROWSE,
  UPCOMING
}
