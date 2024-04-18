package com.github.se.eventradar.ui.home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.compose.rememberNavController
import com.github.se.eventradar.R
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.Ticket
import com.github.se.eventradar.ui.BottomNavigationMenu
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime

@Composable
fun HomeScreen(navigationActions: NavigationActions) {

  val mockEvents =
      listOf(
          Event(
              "NYE2025",
              "User1", // todo
              LocalDateTime.MIN,
              LocalDateTime.MAX,
              Location(83.39, 2.992, "EPFL"),
              "enjoy your time on the dacefloor",
              Ticket("Standard", 0.0, 500),
              "jg@joytigoel.com",
              mutableSetOf("2989jdgj23", "32923jkbd23"),
              mutableSetOf("20982jwdwk", "j1ou1e]d8223"),
              EventCategory.MUSIC,
              "89379"),
          Event(
              "NYE2026",
              "User2", // todo
              LocalDateTime.now(),
              LocalDateTime.MAX,
              Location(83.49, 56.992, "161 makepeace avenue, n666es"),
              "Forget and Enjoy",
              Ticket("regular", 0.0, 10000),
              "valerian@joytigoel.com",
              mutableSetOf("298jhk", "jwj8223"),
              mutableSetOf("20982jhk", "j1ou1e8223"),
              EventCategory.SPORTS,
              "89298"),
          Event(
              "NYE2027",
              "User3", // todo
              LocalDateTime.now(),
              LocalDateTime.MIN,
              Location(83.39, 66.992, "161 makepeace avenue, n666es"),
              "Join the Community",
              Ticket("regular", 0.0, 10000),
              "valerian@joytigoel.com",
              mutableSetOf("298jhk", "jwj8223"),
              mutableSetOf("20982e2hk", "j1ou223e8223"),
              EventCategory.COMMUNITY,
              "89298"))

  var selectedTabIndex by remember { mutableIntStateOf(0) }
  var viewToggleBrowseIndex by remember { mutableIntStateOf(0) }
  val context = LocalContext.current

  ConstraintLayout(modifier = Modifier.fillMaxSize().testTag("homeScreen")) {
    val (logo, tabs, eventList, bottomNav, viewToggle) = createRefs()
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .constrainAs(logo) {
                  top.linkTo(parent.top, margin = 32.dp)
                  start.linkTo(parent.start, margin = 16.dp)
                }
                .testTag("logo"),
        verticalAlignment = Alignment.CenterVertically) {
          Image(
              painter = painterResource(id = R.drawable.event_logo),
              contentDescription = "Event Radar Logo",
              modifier = Modifier.size(width = 186.dp, height = 50.dp))
        }

    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier =
            Modifier.fillMaxWidth()
                .padding(top = 8.dp)
                .constrainAs(tabs) {
                  top.linkTo(logo.bottom, margin = 16.dp)
                  start.linkTo(parent.start)
                  end.linkTo(parent.end)
                }
                .testTag("tabs"),
        contentColor = MaterialTheme.colorScheme.primary) {
          Tab(
              selected = selectedTabIndex == 0,
              onClick = { selectedTabIndex = 0 },
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
              selected = selectedTabIndex == 1,
              onClick = { selectedTabIndex = 1 },
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

    if (selectedTabIndex == 0) {
      if (viewToggleBrowseIndex == 0) {
        EventList(
            mockEvents,
            Modifier.fillMaxWidth().constrainAs(eventList) {
              top.linkTo(tabs.bottom, margin = 8.dp)
              start.linkTo(parent.start)
              end.linkTo(parent.end)
            })
      } else {
        EventMap(
            mockEvents,
            navigationActions,
            Modifier.fillMaxWidth().constrainAs(eventList) {
              top.linkTo(tabs.bottom, margin = 8.dp)
              start.linkTo(parent.start)
              end.linkTo(parent.end)
            })
      }
    } else {
      // "Upcoming" tab content
      // TODO: Implement upcoming events
      Toast.makeText(context, "Upcoming events not yet available", Toast.LENGTH_SHORT).show()
      selectedTabIndex = 0
    }

    BottomNavigationMenu(
        onTabSelected = { tab -> navigationActions.navigateTo(tab) },
        tabList = TOP_LEVEL_DESTINATIONS,
        selectedItem = TOP_LEVEL_DESTINATIONS[2],
        modifier =
            Modifier.testTag("bottomNavMenu").constrainAs(bottomNav) {
              bottom.linkTo(parent.bottom)
              start.linkTo(parent.start)
              end.linkTo(parent.end)
            })

    var viewToggleIcon =
        if (viewToggleBrowseIndex == 0) Icons.Default.Place else Icons.AutoMirrored.Filled.List
    FloatingActionButton(
        onClick = {
          viewToggleBrowseIndex = if (viewToggleBrowseIndex == 0) 1 else 0
          viewToggleIcon =
              if (viewToggleBrowseIndex == 0) Icons.Default.Place
              else Icons.AutoMirrored.Filled.List
        },
        modifier =
            Modifier.testTag("viewToggleFab").constrainAs(viewToggle) {
              bottom.linkTo(bottomNav.top, margin = 16.dp)
              start.linkTo(parent.start, margin = 16.dp)
            }) {
          Icon(imageVector = viewToggleIcon, contentDescription = null)
        }
  }
}

@Composable
fun EventList(events: List<Event>, modifier: Modifier = Modifier) {
  LazyColumn(modifier = modifier) { items(events) { event -> EventCard(event) } }
}

@Composable
fun EventMap(events: List<Event>, navigationActions: NavigationActions, modifier: Modifier = Modifier) {
  val mapProperties by remember {
    mutableStateOf(MapProperties(maxZoomPreference = 50f, minZoomPreference = 0f))
  }
  val mapUiSettings by remember { mutableStateOf(MapUiSettings(mapToolbarEnabled = false)) }
  
  // TODO: Use actual user location
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
      for (event in events) {
        Marker(
          contentDescription = "Marker for ${event.name}",
          state =
          rememberMarkerState(
            position = LatLng(event.location.latitude, event.location.longitude)),
          title = event.name,
          snippet = event.description,
        )
      }
    }
  }
}

@Composable
fun EventCard(event: Event) {
  // TODO: connected to event details
  val context = LocalContext.current
  Card(
      modifier =
          Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
              .height(IntrinsicSize.Min)
              .testTag("eventCard"),
      colors = cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = cardElevation(defaultElevation = 4.dp),
      onClick = {
        Toast.makeText(context, "Event details not yet available", Toast.LENGTH_SHORT).show()
      }) {
        Row(modifier = Modifier.fillMaxSize()) {
          Column(
              modifier =
                  Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 8.dp)
                      .weight(1f),
              verticalArrangement = Arrangement.Center) {
                Text(
                    text = event.eventName,
                    style =
                        TextStyle(
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.roboto)),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        ))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = event.description,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style =
                        TextStyle(
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.roboto)),
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onPrimaryContainer))
              }
          Image(
              painter = painterResource(id = R.drawable.placeholder),
              contentDescription = "Event Image",
              contentScale = ContentScale.FillBounds,
              modifier = Modifier.weight(0.3f).fillMaxHeight())
        }
      }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
  HomeScreen(NavigationActions(rememberNavController()))
}
