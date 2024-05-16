package com.github.se.eventradar.ui.event

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberImagePainter
import com.github.se.eventradar.R
import com.github.se.eventradar.model.event.EventDetailsViewModel
import com.github.se.eventradar.ui.BottomNavigationMenu
import com.github.se.eventradar.ui.component.EventCategory
import com.github.se.eventradar.ui.component.EventComponentsStyle
import com.github.se.eventradar.ui.component.EventDateTime
import com.github.se.eventradar.ui.component.EventDescription
import com.github.se.eventradar.ui.component.EventDistance
import com.github.se.eventradar.ui.component.EventTitle
import com.github.se.eventradar.ui.component.GoBackButton
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS

// Temporary sizes. Needs to be responsive...
private val widthPadding = 34.dp
private val imageHeight = 191.dp

@Composable
fun EventDetails(viewModel: EventDetailsViewModel, navigationActions: NavigationActions) {

  val eventUiState = viewModel.uiState.collectAsStateWithLifecycle().value

  val componentStyle =
      EventComponentsStyle(
          MaterialTheme.colorScheme.onSurface,
          MaterialTheme.colorScheme.onSurfaceVariant,
          MaterialTheme.colorScheme.onSurface,
      )

  Scaffold(
      modifier = Modifier.testTag("eventDetailsScreen"),
      topBar = {},
      bottomBar = {
        BottomNavigationMenu(
            onTabSelected = { tab -> navigationActions.navigateTo(tab) },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = TOP_LEVEL_DESTINATIONS[2],
            modifier = Modifier.testTag("bottomNavMenu"))
      },
      floatingActionButton = {
        // view ticket button
        FloatingActionButton(
            onClick = {
              navigationActions.navController.navigate(
                  "${Route.EVENT_DETAILS_TICKETS}/${viewModel.eventId}")
            },
            modifier = Modifier.padding(bottom = 16.dp, end = 16.dp).testTag("ticketButton"),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ) {
          Icon(
              painter = painterResource(id = R.drawable.ticket),
              contentDescription = "view tickets button",
              modifier = Modifier.size(32.dp),
              tint = MaterialTheme.colorScheme.onPrimaryContainer,
          )
        }
      }) { innerPadding ->
        ConstraintLayout(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
          val (image, backButton, title, description, distance, category, dateAndTime) =
              createRefs()

          // TODO uncomment when image are implemented
          // val imagePainter: Painter = rememberImagePainter(eventUiState.eventPhoto)
          val imagePainter: Painter = rememberImagePainter(R.drawable.placeholderbig)
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
          GoBackButton(
              modifier =
                  Modifier.wrapContentSize().constrainAs(backButton) {
                    top.linkTo(image.bottom, margin = 8.dp)
                    start.linkTo(image.start, margin = 4.dp)
                  }) {
                navigationActions.goBack()
              }

          EventTitle(
              modifier =
                  Modifier.constrainAs(title) {
                    top.linkTo(image.bottom, margin = 32.dp)
                    start.linkTo(image.start)
                    end.linkTo(image.end)
                  },
              eventUiState = eventUiState,
              style = componentStyle)

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
