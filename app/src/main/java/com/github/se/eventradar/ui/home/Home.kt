package com.github.se.eventradar.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.github.se.eventradar.ExcludeFromJacocoGeneratedReport
import com.github.se.eventradar.R
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.repository.event.MockEventRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.ui.component.AppScaffold
import com.github.se.eventradar.ui.component.EventList
import com.github.se.eventradar.ui.component.FilterPopUp
import com.github.se.eventradar.ui.component.GetUserLocation
import com.github.se.eventradar.ui.component.SearchBarAndFilter
import com.github.se.eventradar.ui.component.ViewToggleFab
import com.github.se.eventradar.ui.component.getIconFromViewListBool
import com.github.se.eventradar.ui.map.EventMap
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import com.github.se.eventradar.viewmodel.EventsOverviewUiState
import com.github.se.eventradar.viewmodel.EventsOverviewViewModel
import com.github.se.eventradar.viewmodel.Tab

@Composable
fun HomeScreen(
    viewModel: EventsOverviewViewModel = hiltViewModel(),
    navigationActions: NavigationActions
) {
    // Ui States handled by viewModel
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = uiState.isSearchActive, key2 = uiState.isFilterActive) {
        if (uiState.isSearchActive || uiState.isFilterActive) {
            viewModel.filterEvents()
        }
    }

    LaunchedEffect(Unit) {
        when {
            (uiState.tab == Tab.BROWSE) -> viewModel.getEvents()
            else -> viewModel.getUpcomingEvents()
        }
    }

    GetUserLocation(context, viewModel::onUserLocationChanged)

    AppScaffold(
        modifier = Modifier.testTag("homeScreen"),
        floatingActionButton = {
            ViewToggleFab(
                modifier = Modifier.padding(16.dp).testTag("viewToggleFab"),
                onClick = { viewModel.onViewListStatusChanged() },
                iconVector = getIconFromViewListBool(uiState.viewList))
        },
        navigationActions = navigationActions,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(it).padding(top = 16.dp).testTag("homeScreen"),
            horizontalAlignment = Alignment.CenterHorizontally) {
            TabRow(
                selectedTabIndex = getTabIndexFromTabEnum(uiState.tab),
                modifier = Modifier.fillMaxWidth().testTag("tabs"),
                contentColor = MaterialTheme.colorScheme.primary) {
                Tab(
                    selected = getTabIndexFromTabEnum(uiState.tab) == 0,
                    onClick = {
                        viewModel.onTabChanged(Tab.BROWSE)
                        viewModel.getEvents()
                    },
                    modifier = Modifier.testTag("browseTab"),
                ) {
                    Text(
                        text = "Browse",
                        style =
                        TextStyle(
                            fontSize = 19.sp,
                            lineHeight = 17.sp,
                            fontFamily = FontFamily(Font(R.font.roboto)),
                            fontWeight = FontWeight(500),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            letterSpacing = 0.25.sp,
                        ),
                        modifier = Modifier.padding(bottom = 8.dp))
                }
                Tab(
                    selected = getTabIndexFromTabEnum(uiState.tab) == 1,
                    onClick = {
                        viewModel.onTabChanged(Tab.UPCOMING)
                        viewModel.getUpcomingEvents()
                    },
                    modifier = Modifier.testTag("upcomingTab")) {
                    Text(
                        text = "Upcoming",
                        style =
                        TextStyle(
                            fontSize = 19.sp,
                            lineHeight = 17.sp,
                            fontFamily = FontFamily(Font(R.font.roboto)),
                            fontWeight = FontWeight(500),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            letterSpacing = 0.25.sp,
                        ),
                        modifier = Modifier.padding(bottom = 8.dp))
                }
            }

            SearchBarAndFilter(
                onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
                searchQuery = uiState.searchQuery,
                onSearchActiveChanged = { viewModel.onSearchActiveChanged(it) },
                onFilterDialogOpen = { viewModel.onFilterDialogOpenChanged() },
                modifier =
                Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                    .testTag("searchBarAndFilter"),
                placeholderStringResource = R.string.home_search_placeholder)

            if (uiState.isFilterDialogOpen) {
                FilterPopUp(
                    onFreeSwitchChanged = { viewModel.onFreeSwitchChanged() },
                    onFilterApply = {
                        if (uiState.radiusQuery != "") {
                            viewModel.onRadiusQueryChanged(uiState.radiusQuery)
                        }
                        viewModel.onFilterApply()

                        // automatically close dialog
                        viewModel.onFilterDialogOpenChanged()
                    },
                    uiState = uiState,
                    onRadiusQueryChanged = { viewModel.onRadiusQueryChanged(it) },
                    modifier = Modifier.testTag("filterPopUp"),
                )
            }

            EventsOverview(
                uiState = uiState,
                navigationActions = navigationActions,
            )
        }
    }
}

@Composable
fun EventsOverview(
    uiState: EventsOverviewUiState,
    navigationActions: NavigationActions,
    modifier: Modifier = Modifier
) {
    val events = getEventsBasedOffUiState(uiState)
    if (uiState.tab == Tab.BROWSE) {
        when (uiState.viewList) {
            true ->
                EventList(events, modifier.testTag("eventList").fillMaxWidth()) { eventId ->
                    navigationActions.navController.navigate("${Route.EVENT_DETAILS}/${eventId}")
                }
            false ->
                EventMap(events, modifier.testTag("eventMap").fillMaxWidth(), uiState.userLocation!!) {
                        eventId ->
                    navigationActions.navController.navigate("${Route.EVENT_DETAILS}/${eventId}")
                }
        }
    } else {
        when {
            (!uiState.userLoggedIn) ->
                Text("Please log in", modifier = modifier.testTag("pleaseLogInText"))
            (uiState.viewList && uiState.upcomingEventList.allEvents.isEmpty()) ->
                Text(
                    "You have no upcoming events",
                    textAlign = TextAlign.Center,
                    modifier = modifier.testTag("noUpcomingEventsText"))
            // In list view + search or filter is active
            uiState.viewList ->
                EventList(
                    events = events, modifier = modifier.testTag("eventListUpcoming").fillMaxWidth()) {
                        eventId ->
                    navigationActions.navController.navigate("${Route.EVENT_DETAILS}/${eventId}")
                }
            else ->
                EventMap(
                    events,
                    modifier.testTag("eventMapUpcoming").fillMaxWidth(),
                    uiState.userLocation!!) { eventId ->
                    navigationActions.navController.navigate("${Route.EVENT_DETAILS}/${eventId}")
                }
        }
    }
}

fun getTabIndexFromTabEnum(tab: Tab): Int {
    val selectedTabIndex =
        if (tab == Tab.BROWSE) {
            0
        } else {
            1
        }
    return selectedTabIndex
}

fun getEventsBasedOffUiState(uiState: EventsOverviewUiState): List<Event> {
    return if (uiState.tab == Tab.BROWSE) {
        when {
            uiState.isSearchActive || uiState.isFilterActive -> uiState.eventList.filteredEvents
            else -> uiState.eventList.allEvents
        }
    } else {
        when {
            uiState.isSearchActive || uiState.isFilterActive -> uiState.upcomingEventList.filteredEvents
            else -> uiState.upcomingEventList.allEvents
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@ExcludeFromJacocoGeneratedReport
@Composable
fun HomeScreenPreview() {
    val mockEventRepo = MockEventRepository()
    val mockUserRepo = MockUserRepository()
    HomeScreen(
        EventsOverviewViewModel(mockEventRepo, mockUserRepo),
        NavigationActions(rememberNavController()))
}
