package com.github.se.eventradar.ui.hosting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.se.eventradar.R
import com.github.se.eventradar.ui.BottomNavigationMenu
import com.github.se.eventradar.ui.component.*
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.eventradar.ui.navigation.getTopLevelDestination

@Composable
fun HostingScreen(navigationActions: NavigationActions) {
  val mockEvents = getMockEvents()
  var viewMapOrListIndex by remember { mutableIntStateOf(0) }
  ConstraintLayout(modifier = Modifier.fillMaxSize().testTag("hostingScreen")) {
    val (logo, title, divider, eventList, bottomNav, buttons) = createRefs()
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
    if (viewMapOrListIndex == 0) {
      EventList(
          mockEvents,
          Modifier.fillMaxWidth().constrainAs(eventList) {
            top.linkTo(divider.bottom, margin = 8.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
          })
    } else {
      // To Do (Map View)
    }

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
          CreateEventFab(onClick = { /*TODO*/}, modifier = Modifier.testTag("createEventFab"))
          Spacer(modifier = Modifier.width(16.dp))
          var viewToggleIcon =
              if (viewMapOrListIndex == 0) Icons.Default.Place else Icons.AutoMirrored.Filled.List
          ViewToggleFab(
              modifier = Modifier.testTag("viewToggleFab"),
              onClick = {
                viewMapOrListIndex = if (viewMapOrListIndex == 0) 1 else 0
                viewToggleIcon =
                    if (viewMapOrListIndex == 0) Icons.Default.Place
                    else Icons.AutoMirrored.Filled.List
              },
              iconVector = viewToggleIcon)
        }
    BottomNavigationMenu(
        onTabSelected = { tab -> navigationActions.navigateTo(tab) },
        tabList = TOP_LEVEL_DESTINATIONS,
        selectedItem = getTopLevelDestination(R.string.hosting), // Don't hardcode
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
      text = LocalContext.current.getString(R.string.hosted_events_title),
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
            text = LocalContext.current.getString(R.string.create_event_button),
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
