package com.github.se.eventradar.map

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.eventradar.ui.navigation.NavigationActions

@Composable
fun Map(viewModel: MapViewModel = viewModel(), navigationActions: NavigationActions) {
  // The following block is temporarily commented out as part of the transition from managing ToDos
  // to Events.
  /*
  val uiState by viewModel.uiState.collectAsState()

    if (uiState.toDoList.getAllTask.isEmpty()) {
    viewModel.getToDos()
  }

  MapUI(uiState.toDoList.getAllTask, navigationActions)
   */
}

// Temporarily commenting out MapUI as we transition from ToDos to Events.
/*
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
*/
