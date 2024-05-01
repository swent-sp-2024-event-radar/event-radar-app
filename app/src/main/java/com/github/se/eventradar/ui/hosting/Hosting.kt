package com.github.se.eventradar.ui.hosting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.se.eventradar.R
import com.github.se.eventradar.ui.BottomNavigationMenu
import com.github.se.eventradar.ui.component.*
import com.github.se.eventradar.ui.map.EventMap
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.eventradar.ui.navigation.getTopLevelDestination
import com.github.se.eventradar.util.toast
import com.github.se.eventradar.viewmodel.HostedEventsViewModel

@Composable
fun HostingScreen(
    viewModel: HostedEventsViewModel = hiltViewModel(),
    navigationActions: NavigationActions
) {
  // Ui States handled by viewModel
  val uiState by viewModel.uiState.collectAsState()
  LaunchedEffect(key1 = uiState.eventList) { viewModel.getHostedEvents() }
  ConstraintLayout(modifier = Modifier.fillMaxSize().testTag("hostingScreen")) {
    val (logo, title, divider, eventList, eventMap, bottomNav, buttons) = createRefs()
    Logo(
        modifier =
            Modifier.fillMaxWidth()
                .constrainAs(logo) {
                  top.linkTo(parent.top, margin = 32.dp)
                  start.linkTo(parent.start, margin = 16.dp)
                }
                .testTag("logo"))
    Title(
        modifier =
            Modifier.testTag("myHostedEventsTitle")
                .constrainAs(
                    title,
                    {
                      top.linkTo(logo.bottom, margin = 16.dp)
                      start.linkTo(parent.start)
                      end.linkTo(parent.end)
                    }))
    HorizontalDivider(
        modifier = Modifier.constrainAs(divider, { top.linkTo(title.bottom, margin = 10.dp) }))
    if (uiState.viewList) {
      EventList(
          uiState.eventList.filteredEvents,
          Modifier.testTag("eventList").fillMaxWidth().constrainAs(eventList) {
            top.linkTo(divider.bottom, margin = 8.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
          })
    } else {
      EventMap(
          uiState.eventList.allEvents,
          navigationActions,
          Modifier.testTag("map").fillMaxWidth().constrainAs(eventMap) {
            top.linkTo(divider.bottom, margin = 8.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
          })
    }
    val context = LocalContext.current
    Row(
        modifier =
            Modifier.constrainAs(ref = buttons) {
                  bottom.linkTo(bottomNav.top)
                  centerHorizontallyTo(parent)
                }
                .testTag("floatingActionButtons")
                .fillMaxWidth()
                .padding(16.dp),
        horizontalArrangement = Arrangement.Absolute.Left,
        verticalAlignment = Alignment.CenterVertically) {
          CreateEventFab(
              onClick = { context.toast("Create Event still needs to be implemented") },
              modifier = Modifier.testTag("createEventFab"))
          Spacer(modifier = Modifier.width(16.dp))
          ViewToggleFab(
              modifier = Modifier.testTag("viewToggleFab"),
              onClick = { viewModel.onViewListStatusChanged() },
              iconVector = getIconFromViewListBool(uiState.viewList))
        }
    BottomNavigationMenu(
        onTabSelected = navigationActions::navigateTo,
        tabList = TOP_LEVEL_DESTINATIONS,
        selectedItem = getTopLevelDestination(Route.MY_HOSTING),
        modifier =
            Modifier.testTag("bottomNavMenu").constrainAs(bottomNav) {
              bottom.linkTo(parent.bottom)
              start.linkTo(parent.start)
              end.linkTo(parent.end)
            })
  }
}

@Composable
fun Title(modifier: Modifier) {
  Text(
      text = stringResource(R.string.hosted_events_title),
      modifier = modifier,
      style =
          TextStyle(
              fontSize = 19.sp,
              lineHeight = 17.sp,
              fontFamily = FontFamily(Font(R.font.roboto)),
              fontWeight = FontWeight(500),
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              textAlign = TextAlign.Center,
              letterSpacing = 0.25.sp,
          ))
}

@Composable
fun CreateEventFab(onClick: () -> Unit, modifier: Modifier = Modifier) {
  ExtendedFloatingActionButton(
      text = {
        Text(
            text = stringResource(R.string.create_event_button),
            style =
                TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 17.sp,
                    fontFamily = FontFamily(Font(R.font.roboto)),
                    fontWeight = FontWeight(500),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.25.sp,
                ))
      },
      icon = { Icon(Icons.Filled.Add, "Create Event Icon") },
      onClick = onClick,
      modifier = modifier.fillMaxWidth(0.8f))
}
