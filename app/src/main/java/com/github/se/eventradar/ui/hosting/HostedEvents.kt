package com.github.se.eventradar.ui.hosting

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.se.eventradar.R
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.Ticket
import com.github.se.eventradar.ui.BottomNavigationMenu
import com.github.se.eventradar.ui.home.EventList
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import java.time.LocalDateTime

@Composable
fun HostedEvents(navigationActions: NavigationActions) {
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
  val context = LocalContext.current

  ConstraintLayout(modifier = Modifier.fillMaxSize().testTag("hostedEventsScreen")) {
    val (logo, tabs, eventList, bottomNav, buttons) = createRefs()
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
              modifier = Modifier.testTag("myHostedEventsTab"),
          ) {
            Text(
                text = "My Hosted Events",
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

    if (selectedTabIndex == 0) { // events I host vs events I staff?
      EventList(
          mockEvents,
          Modifier.fillMaxWidth().constrainAs(eventList) {
            top.linkTo(tabs.bottom, margin = 8.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
          })
    }
    // To Do: Floating Action Point Button. when button clicked, it should turn into mapView +
    // button logo changes to List logo
    Row(
        modifier =
            Modifier.constrainAs(ref = buttons) {
                  bottom.linkTo(bottomNav.top, margin = 10.dp)
                  centerHorizontallyTo(parent)
                }
                .fillMaxWidth()
                .padding(16.dp),
        horizontalArrangement = Arrangement.Absolute.Left,
        verticalAlignment = Alignment.CenterVertically) {
          ExtendedFloatingActionButton(
              text = {
                  Text(
                      text = "Create New Event",
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
              icon = { Icon(Icons.Filled.Add, "Floating action button.") },
              onClick = { /*TODO*/},
              modifier = Modifier.fillMaxWidth(0.8f),
              containerColor = MaterialTheme.colorScheme.secondaryContainer,
              contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
        Spacer(modifier = Modifier.width(16.dp))
        FloatingActionButton( // to be replaced with mapview later.
              onClick = { /*TODO*/},
              modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
          ) {
            Icon(
                painter = painterResource(id = R.drawable.map),
                contentDescription = "register to event button",
                modifier = Modifier.size(32.dp).padding(5.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
          }
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
  }
}

@Composable
fun EventList(events: List<Event>, modifier: Modifier = Modifier) {
  LazyColumn(modifier = modifier) { items(events) { event -> EventCard(event) } }
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
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
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
