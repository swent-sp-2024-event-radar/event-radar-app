package com.github.se.eventradar.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.github.se.eventradar.ExcludeFromJacocoGeneratedReport
import com.github.se.eventradar.R
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.repository.event.MockEventRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.ui.component.AppScaffold
import com.github.se.eventradar.ui.component.EventList
import com.github.se.eventradar.ui.component.FilterPopUp
import com.github.se.eventradar.ui.component.SearchBarAndFilter
import com.github.se.eventradar.ui.component.ViewToggleFab
import com.github.se.eventradar.ui.component.getIconFromViewListBool
import com.github.se.eventradar.ui.map.EventMap
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import com.github.se.eventradar.viewmodel.EventsOverviewUiState
import com.github.se.eventradar.viewmodel.EventsOverviewViewModel
import com.github.se.eventradar.viewmodel.Tab
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

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

  val locationProvider = LocationServices.getFusedLocationProviderClient(context)

  val locationCallback =
      object : LocationCallback() {
        // 1
        override fun onLocationResult(result: LocationResult) {
          if (ActivityCompat.checkSelfPermission(
              context, Manifest.permission.ACCESS_FINE_LOCATION) !=
              PackageManager.PERMISSION_GRANTED &&
              ActivityCompat.checkSelfPermission(
                  context, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                  PackageManager.PERMISSION_GRANTED) {
            // set default location
            val epflLocation = Location(46.519962, 6.56637, "EPFL")
            viewModel.onUserLocationChanged(epflLocation)
            return
          }

          locationProvider.lastLocation
              .addOnSuccessListener { location ->
                location?.let {
                  val lat = location.latitude
                  val long = location.longitude
                  // Update data class with location data
                  viewModel.onUserLocationChanged(Location(lat, long, "User"))
                }
              }
              .addOnFailureListener { Log.e("Location_error", "${it.message}") }
        }
      }

  GetUserLocation(locationProvider, locationCallback)

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
              onFilterDialogOpen = { viewModel.onFilterDialogOpen() },
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
                  viewModel.onFilterDialogOpen()
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

@Composable
fun GetUserLocation(
    locationProvider: FusedLocationProviderClient,
    locationCallback: LocationCallback
) {
  DisposableEffect(key1 = locationProvider) {
    locationUpdate(locationProvider, locationCallback)
    // 3
    onDispose { stopLocationUpdate(locationProvider, locationCallback) }
  }
}

@SuppressLint("MissingPermission")
private fun locationUpdate(
    locationProvider: FusedLocationProviderClient,
    locationCallback: LocationCallback
) {
  locationCallback.let {
    // An encapsulation of various parameters for requesting
    // location through FusedLocationProviderClient.
    val locationRequest: LocationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build()
    // use FusedLocationProviderClient to request location update
    locationProvider.requestLocationUpdates(locationRequest, it, Looper.getMainLooper())
  }
}

fun stopLocationUpdate(
    locationProvider: FusedLocationProviderClient,
    locationCallback: LocationCallback
) {
  try {
    // Removes all location updates for the given callback.
    val removeTask = locationProvider.removeLocationUpdates(locationCallback)
    removeTask.addOnCompleteListener { task ->
      if (task.isSuccessful) {
        Log.d("LocationProvider", "Location Callback removed.")
      } else {
        Log.d("LocationProvider", "Failed to remove Location Callback.")
      }
    }
  } catch (se: SecurityException) {
    Log.e("LocationProvider", "Failed to remove Location Callback.. $se")
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
