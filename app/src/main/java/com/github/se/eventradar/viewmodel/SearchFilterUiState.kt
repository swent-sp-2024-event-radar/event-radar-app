package com.github.se.eventradar.viewmodel

import com.github.se.eventradar.model.event.EventCategory

// This class represents a common uiState for search and filter operations
// It allows uiState in FilterPopUp to be of either type EventsOverviewUiState or HostedEventUiState
sealed class SearchFilterUiState {
  abstract val searchQuery: String
  abstract val isSearchActive: Boolean
  abstract val isFilterDialogOpen: Boolean
  abstract val isFilterActive: Boolean
  abstract val radiusQuery: String
  abstract val isFreeSwitchOn: Boolean
  abstract val categoriesCheckedList: MutableSet<EventCategory>
}
