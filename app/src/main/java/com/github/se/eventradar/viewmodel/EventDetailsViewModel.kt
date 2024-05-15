package com.github.se.eventradar.model.event

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.repository.event.IEventRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = EventDetailsViewModel.Factory::class)
class EventDetailsViewModel
@AssistedInject
constructor(
    private val eventRepository: IEventRepository,
    @Assisted val eventId: String,
) : ViewModel() {

    init {
        getEventData()
    }

    private val _uiState = MutableStateFlow(EventUiState())
    val uiState: StateFlow<EventUiState> = _uiState

    fun getEventData() {
        viewModelScope.launch {
            when (val response = eventRepository.getEvent(eventId)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            eventName = response.data!!.eventName,
                            eventPhoto = response.data.eventPhoto,
                            start = response.data.start,
                            end = response.data.end,
                            location = response.data.location,
                            description = response.data.description,
                            ticket = response.data.ticket,
                            mainOrganiser = response.data.mainOrganiser,
                            category = response.data.category,
                        )
                    }
                }
                is Resource.Failure ->
                    Log.d("EventDetailsViewModel", "Error getting event: ${response.throwable.message}")
            }
        }
    }

    fun isTicketFree(): Boolean {
        return !(_uiState.value.ticket.price > 0.0)
    }

    // Code for creating an instance of EventDetailsViewModel
    @AssistedFactory
    interface Factory {
        fun create(eventId: String): EventDetailsViewModel
    }

    companion object {
        @Composable
        fun create(eventId: String): EventDetailsViewModel {
            return hiltViewModel<EventDetailsViewModel, Factory>(
                creationCallback = { factory -> factory.create(eventId = eventId) })
        }
    }
}

data class EventUiState(
    val eventName: String = "",
    val eventPhoto: String = "",
    val start: LocalDateTime = LocalDateTime.MIN,
    val end: LocalDateTime = LocalDateTime.MAX,
    val location: Location = Location(0.0, 0.0, ""),
    val description: String = "",
    val ticket: EventTicket = EventTicket("", 0.0, 0),
    val mainOrganiser: String = "",
    val category: EventCategory = EventCategory.MUSIC,
)
