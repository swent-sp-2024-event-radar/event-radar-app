package com.github.se.eventradar.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.se.eventradar.model.event.Event
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
fun EventMap(
    events: List<Event>,
    modifier: Modifier = Modifier,
    onCardClick: (String) -> Unit,
) {
  val mapProperties by remember {
    mutableStateOf(MapProperties(maxZoomPreference = 50f, minZoomPreference = 0f))
  }
  val mapUiSettings by remember { mutableStateOf(MapUiSettings(mapToolbarEnabled = false)) }

  // TODO: Use actual user location
  val epflCameraPosition = LatLng(46.51890374606943, 6.566587868510539)
  val cameraPositionState: CameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(epflCameraPosition, 11f)
  }

  GoogleMap(
      properties = mapProperties,
      uiSettings = mapUiSettings,
      cameraPositionState = cameraPositionState,
      modifier = modifier.fillMaxSize().testTag("map")) {
        for (event in events) {
          Marker(
              contentDescription = "Marker for ${event.eventName}",
              state =
                  rememberMarkerState(
                      position = LatLng(event.location.latitude, event.location.longitude)),
              title = event.eventName,
              snippet = event.description,
              onInfoWindowClick = { onCardClick(event.fireBaseID) })
        }
      }
}
