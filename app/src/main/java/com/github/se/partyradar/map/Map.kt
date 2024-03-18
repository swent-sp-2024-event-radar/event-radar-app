package com.github.se.partyradar.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.partyradar.model.todo.ToDo
import com.github.se.partyradar.ui.BottomNavigationMenu
import com.github.se.partyradar.ui.navigation.NavigationActions
import com.github.se.partyradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun Map(viewModel: MapViewModel = viewModel(), navigationActions: NavigationActions) {
  val uiState by viewModel.uiState.collectAsState()

  if (uiState.toDoList.getAllTask.isEmpty()) {
    viewModel.getToDos()
  }

  MapUI(uiState.toDoList.getAllTask, navigationActions)
}

@Composable
fun MapUI(todoList: List<ToDo>, navigationActions: NavigationActions) {
  val mapProperties by remember {
    mutableStateOf(MapProperties(maxZoomPreference = 50f, minZoomPreference = 0f))
  }
  val mapUiSettings by remember { mutableStateOf(MapUiSettings(mapToolbarEnabled = false)) }

  val epflCameraPosition = LatLng(46.51890374606943, 6.566587868510539)
  val cameraPositionState: CameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(epflCameraPosition, 11f)
  }

  Scaffold(
      bottomBar = {
        BottomNavigationMenu(
            onTabSelected = navigationActions::navigateTo,
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = TOP_LEVEL_DESTINATIONS[1])
      },
      modifier = Modifier.testTag("mapScreen")) {
        GoogleMap(
            properties = mapProperties,
            uiSettings = mapUiSettings,
            cameraPositionState = cameraPositionState,
            modifier = Modifier.fillMaxSize().padding(it).testTag("map")) {
              for (todo in todoList) {
                Marker(
                    contentDescription = "Marker for ${todo.title}",
                    state =
                        rememberMarkerState(
                            position = LatLng(todo.location.latitude, todo.location.longitude)),
                    title = todo.title,
                    snippet = todo.description,
                )
              }
            }
      }
}
