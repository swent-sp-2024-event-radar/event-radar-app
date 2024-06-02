package com.github.se.eventradar.ui.event

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.github.se.eventradar.R
import com.github.se.eventradar.ui.component.AppScaffold
import com.github.se.eventradar.ui.component.EventCategory
import com.github.se.eventradar.ui.component.EventComponentsStyle
import com.github.se.eventradar.ui.component.EventDate
import com.github.se.eventradar.ui.component.EventDescription
import com.github.se.eventradar.ui.component.EventLocation
import com.github.se.eventradar.ui.component.EventTime
import com.github.se.eventradar.ui.component.EventTitle
import com.github.se.eventradar.ui.component.GenericDialogBox
import com.github.se.eventradar.ui.component.GoBackButton
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import com.github.se.eventradar.viewmodel.EventDetailsViewModel

// Temporary sizes. Needs to be responsive...
private val imageHeight = 191.dp

@Composable
fun EventDetails(viewModel: EventDetailsViewModel, navigationActions: NavigationActions) {

  val isUserAttending = viewModel.isUserAttending.collectAsStateWithLifecycle().value
  val eventUiState = viewModel.uiState.collectAsStateWithLifecycle().value

  LaunchedEffect(isUserAttending) { viewModel.refreshAttendance() }

  val componentStyle =
      EventComponentsStyle(
          MaterialTheme.colorScheme.onSurface,
          MaterialTheme.colorScheme.onSurfaceVariant,
          MaterialTheme.colorScheme.onSurface,
      )

  AppScaffold(
      modifier = Modifier.testTag("eventDetailsScreen"),
      topBar = {
        GoBackButton(modifier = Modifier.wrapContentSize()) { navigationActions.goBack() }
      },
      floatingActionButton = {
        // view ticket button
        val icon = if (isUserAttending) R.drawable.cancel else R.drawable.ticket
        FloatingActionButton(
            onClick = {
              if (isUserAttending) {
                viewModel.showCancelRegistrationDialog.value = true
              } else {
                navigationActions.navController.navigate(
                    "${Route.EVENT_DETAILS_TICKETS}/${viewModel.eventId}")
              }
            },
            modifier = Modifier.testTag("registrationButton"),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ) {
          Icon(
              painter = painterResource(id = icon),
              contentDescription = "Event details FAB",
              modifier = Modifier.size(32.dp),
              tint = MaterialTheme.colorScheme.onPrimaryContainer,
          )
        }
      },
      navigationActions = navigationActions) {
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(it)) {
              val imagePainter: Painter =
                  if (eventUiState.eventPhoto == "") {
                    painterResource(id = R.drawable.placeholder)
                  } else {
                    rememberAsyncImagePainter(eventUiState.eventPhoto)
                  }
              Image(
                  painter = imagePainter,
                  contentDescription = "Event banner image",
                  modifier = Modifier.fillMaxWidth().height(imageHeight).testTag("eventImage"),
                  contentScale = ContentScale.FillWidth)

              EventTitle(
                  modifier = Modifier.align(Alignment.CenterHorizontally),
                  eventUiState = eventUiState,
                  style = componentStyle)

              Spacer(modifier = Modifier.height(8.dp))

              Column(
                  modifier = Modifier.padding(horizontal = 16.dp),
                  horizontalAlignment = Alignment.CenterHorizontally) {
                    EventDescription(modifier = Modifier, eventUiState, componentStyle)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                      EventLocation(modifier = Modifier.weight(2f), eventUiState, componentStyle)

                      EventDate(modifier = Modifier.weight(1f), eventUiState, componentStyle)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                      EventCategory(modifier = Modifier.weight(2f), eventUiState, componentStyle)

                      EventTime(modifier = Modifier.weight(1f), eventUiState, componentStyle)
                    }

                    if (isUserAttending) {
                      Spacer(modifier = Modifier.height(16.dp))
                      Text(
                          text = stringResource(id = R.string.event_attendance_message),
                          modifier = Modifier.testTag("attendance"),
                          style =
                              TextStyle(
                                  fontSize = 18.sp,
                                  fontFamily = FontFamily(Font(R.font.roboto)),
                                  fontWeight = FontWeight.Bold,
                              ),
                          color = MaterialTheme.colorScheme.primary)
                    }
                  }
            }
      }

  GenericDialogBox(
      openDialog = viewModel.showCancelRegistrationDialog,
      title = "Confirm cancellation",
      modifier = Modifier.testTag("cancelRegistrationDialog"),
      message = stringResource(id = R.string.cancel_registration_message),
      onClickConfirmButton = { viewModel.removeUserFromEvent() },
      boxIcon = null)
}
