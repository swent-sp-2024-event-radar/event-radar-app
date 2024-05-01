package com.github.se.eventradar.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.event.EventList
import com.github.se.eventradar.model.repository.event.IEventRepository
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

  fun getHostedEvents(uid: String? = Firebase.auth.currentUser?.uid) {
    if (uid == null) {
      Log.d("HostedEventsViewModel", "User not logged in")
    } else {
      viewModelScope.launch {
        when (val userResponse = userRepository.getUser(uid)) {
          is Resource.Success -> {
            val user = userResponse.data!!
            val eventsHostList = user.eventsHostList
            if (eventsHostList.isNotEmpty()) {
              when (val events = eventRepository.getEventsByIds(eventsHostList)) {
                is Resource.Success -> {
                  _uiState.value =
                      _uiState.value.copy(
                          eventList =
                              EventList(
                                  events.data, events.data, _uiState.value.eventList.selectedEvent))
                }
                is Resource.Failure -> {
                  Log.d("HostedEventsViewModel", "Error getting hosted events for $uid")
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
            Log.d("HostedEventsViewModel", "Error fetching user document")
            _uiState.value =
                _uiState.value.copy(eventList = EventList(emptyList(), emptyList(), null))
          }
        }
      }
    }
  }

  fun onViewListStatusChanged(
      viewList: Boolean,
      state: MutableStateFlow<HostedEventsUiState> = _uiState
  ) {
    state.value = _uiState.value.copy(viewList = viewList)
  }
}

data class HostedEventsUiState(
    val eventList: EventList = EventList(emptyList(), emptyList(), null),
    var viewList: Boolean = true,
    val searchQuery: String = "",
)
