package com.github.se.eventradar.ui.event

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.github.se.eventradar.R
import com.github.se.eventradar.model.event.EventDetailsViewModel
import com.github.se.eventradar.ui.BottomNavigationMenu
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS

// Temporary sizes. Needs to be responsive...
private val widthPadding = 34.dp
private val imageHeight = 191.dp

@Composable
fun EventDetails(
    viewModel: EventDetailsViewModel = hiltViewModel(),
    navigationActions: NavigationActions
) {

  LaunchedEffect(Unit) { // Using `Unit` as a key to run only once
    viewModel.getEventData()
  }

  val eventUiState = viewModel.uiState.collectAsStateWithLifecycle().value

  val componentStyle =
      EventComponentsStyle(
          MaterialTheme.colorScheme.onSurface,
          MaterialTheme.colorScheme.onSurfaceVariant,
          MaterialTheme.colorScheme.onSurface,
      )

  Scaffold(
      modifier = Modifier.testTag("EventDetailsScreen"),
      topBar = {},
      bottomBar = {
        BottomNavigationMenu(
            onTabSelected = { tab -> navigationActions.navigateTo(tab) },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = TOP_LEVEL_DESTINATIONS[2],
            modifier = Modifier.testTag("bottomNavMenu"))
      },
      floatingActionButton = {
        // register button
        FloatingActionButton(
            onClick = { /*TODO*/},
            modifier = Modifier.padding(bottom = 16.dp, end = 16.dp).testTag("ticketButton"),
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ) {
          Icon(
              painter = painterResource(id = R.drawable.ticket),
              contentDescription = "register to event button",
              modifier = Modifier.size(32.dp),
              tint = MaterialTheme.colorScheme.primary,
          )
        }
      }) { innerPadding ->
        ConstraintLayout(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
          val (image, backButton, title, description, distance, category, dateAndTime) =
              createRefs()
          val imagePainter: Painter = rememberImagePainter(eventUiState.eventPhoto)
          Image(
              painter = imagePainter,
              contentDescription = "Event banner image",
              modifier =
                  Modifier.fillMaxWidth()
                      .height(imageHeight)
                      .constrainAs(image) {
                        top.linkTo(parent.top, margin = 0.dp)
                        start.linkTo(parent.start, margin = 0.dp)
                      }
                      .testTag("eventImage"),
              contentScale = ContentScale.FillWidth)

          // Go back button
          Button(
              onClick = { navigationActions.goBack() },
              modifier =
                  Modifier.wrapContentSize()
                      .constrainAs(backButton) {
                        top.linkTo(image.bottom, margin = 8.dp)
                        start.linkTo(image.start, margin = 4.dp)
                      }
                      .testTag("goBackButton"),
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
              text = eventUiState.eventName,
              style = componentStyle.titleStyle,
              modifier =
                  Modifier.constrainAs(title) {
                        top.linkTo(image.bottom, margin = 32.dp)
                        start.linkTo(image.start)
                        end.linkTo(image.end)
                      }
                      .testTag("eventTitle"),
              color = MaterialTheme.colorScheme.onSurface)

          EventDescription(
              modifier =
                  Modifier
                      // .padding(start = widthPadding, end = widthPadding)
                      .constrainAs(description) {
                        top.linkTo(title.bottom, margin = 32.dp)
                        start.linkTo(parent.start, margin = widthPadding)
                      },
              eventUiState,
              componentStyle)

          EventDistance(
              modifier =
                  Modifier.constrainAs(distance) {
                    top.linkTo(description.bottom, margin = 32.dp)
                    start.linkTo(parent.start, margin = widthPadding)
                  },
              eventUiState,
              componentStyle)

          EventDateTime(
              modifier =
                  Modifier.constrainAs(dateAndTime) {
                    top.linkTo(distance.bottom, margin = 32.dp)
                    start.linkTo(parent.start, margin = widthPadding)
                  },
              eventUiState,
              componentStyle)

          EventCategory(
              modifier =
                  Modifier.constrainAs(category) {
                    top.linkTo(dateAndTime.bottom, margin = 32.dp)
                    start.linkTo(parent.start, margin = widthPadding)
                  },
              eventUiState,
              componentStyle)
        }
      }
}

@Composable
fun buyTicket(){

  /*
  // Go back button
  Button(
    onClick = { navigationActions.goBack() },
    modifier =
    Modifier.wrapContentSize()
      .constrainAs(backButton) {
        top.linkTo(image.bottom, margin = 8.dp)
        start.linkTo(image.start, margin = 4.dp)
      }
      .testTag("goBackButton"),
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
    text = eventUiState.eventName,
    style = componentStyle.titleStyle,
    modifier =
    Modifier.constrainAs(title) {
      top.linkTo(image.bottom, margin = 32.dp)
      start.linkTo(image.start)
      end.linkTo(image.end)
    }
      .testTag("eventTitle"),
    color = MaterialTheme.colorScheme.onSurface)

  */
}