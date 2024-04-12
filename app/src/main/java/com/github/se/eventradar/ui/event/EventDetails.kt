package com.github.se.eventradar.ui.event

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.github.se.eventradar.R
import com.github.se.eventradar.ui.BottomNavigationMenu
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.eventradar.ui.theme.MyApplicationTheme

// Temporary sizes. Needs to be responsive...
private val widthPadding = 34.dp
private val imageHeight = 191.dp
private val titleTextSize = 32.sp
private val contentTextSize = 14.sp

@Composable
fun EventDetails(navigationActions: NavigationActions) {

  val fieldTitleColor = MaterialTheme.colorScheme.onSurfaceVariant
  val fieldContentColor = MaterialTheme.colorScheme.onSurface

  // Temporary text field values to be replaced with some ViewModel.stateVar.collectAsState()
  val eventImage = R.drawable.ic_launcher_background
  val eventTitle = "Event Title"
  val headerDescription = "Description"
  val headerDistance = "Distance from you"
  val headerDate = "Date"
  val headerCategory = "Category"
  val headerTime = "Time"
  val contentDescription =
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc et ornare  dui. Integer convallis purus odio, vitae mattis erat ultricies non.  Donec et magna hendrerit, molestie lorem vel, facilisis augue. "
  val contentDistance = "xx km"
  val contentDate = "dd/mm/yyyy"
  val contentCategory = "Cat x"
  val contentTime = "xx:xx"

  Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
    Image(
        painter = painterResource(eventImage),
        contentDescription = "Event Image",
        modifier = Modifier.fillMaxWidth().height(imageHeight),
        contentScale = ContentScale.FillWidth)

    // Go back button
    Button(
        onClick = { navigationActions.goBack() },
        modifier = Modifier.wrapContentSize().align(Alignment.Start).testTag("backButton"),
        colors =
            ButtonDefaults.buttonColors(
                contentColor = Color.Transparent,
                containerColor = Color.Transparent,
            ),
    ) {
      Icon(
          painter = painterResource(id = R.drawable.back_arrow),
          contentDescription = "Back navigation arrow",
          tint = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier.width(24.dp).height(24.dp).align(Alignment.CenterVertically))
    }

    Text(
        text = eventTitle,
        color = MaterialTheme.colorScheme.onSurface,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Bold,
        fontSize = titleTextSize,
        modifier = Modifier.align(Alignment.CenterHorizontally))

    Column(modifier = Modifier.padding(start = widthPadding, end = widthPadding)) {
      Text(text = headerDescription, color = fieldTitleColor, fontSize = contentTextSize)
      Text(text = contentDescription, color = fieldContentColor, fontSize = contentTextSize)
    }

    Row(
        modifier =
            Modifier.fillMaxWidth().padding(start = widthPadding, end = widthPadding).height(64.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {
          Column(modifier = Modifier.weight(1f)) {
            Text(text = headerDistance, color = fieldTitleColor, fontSize = contentTextSize)
            Text(text = contentDistance, color = fieldContentColor, fontSize = contentTextSize)
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(text = headerDate, color = fieldTitleColor, fontSize = contentTextSize)
            Text(text = contentDate, color = fieldContentColor, fontSize = contentTextSize)
          }
        }

    Row(
        modifier =
            Modifier.fillMaxWidth().padding(start = widthPadding, end = widthPadding).height(64.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {
          Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.SpaceBetween) {
            Text(text = headerCategory, color = fieldTitleColor, fontSize = contentTextSize)
            Text(text = contentCategory, color = fieldContentColor, fontSize = contentTextSize)
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(text = headerTime, color = fieldTitleColor, fontSize = contentTextSize)
            Text(text = contentTime, color = fieldContentColor, fontSize = contentTextSize)
          }
        }

    // register button
    FloatingActionButton(
        onClick = { /*TODO*/},
        modifier = Modifier.padding(bottom = 16.dp, end = 16.dp).align(Alignment.End),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ) {
      Icon(
          painter = painterResource(id = R.drawable.ticket),
          contentDescription = "register to event button",
          modifier = Modifier.size(32.dp),
          tint = MaterialTheme.colorScheme.primaryContainer,
      )
    }

    BottomNavigationMenu(
        onTabSelected = { tab -> navigationActions.navigateTo(tab) },
        tabList = TOP_LEVEL_DESTINATIONS,
        selectedItem = TOP_LEVEL_DESTINATIONS[0])
  }
}

@Preview(showBackground = true)
@Composable
fun EventDetailsPreview() {
  MyApplicationTheme {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
      val nav = NavigationActions(rememberNavController())
      EventDetails(nav)
    }
  }
}
