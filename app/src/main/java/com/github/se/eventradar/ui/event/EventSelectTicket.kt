package com.github.se.eventradar.ui.event

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.github.se.eventradar.R
import com.github.se.eventradar.ui.BottomNavigationMenu
import com.github.se.eventradar.ui.component.EventComponentsStyle
import com.github.se.eventradar.ui.component.EventTitle
import com.github.se.eventradar.ui.component.GenericDialogBox
import com.github.se.eventradar.ui.component.GoBackButton
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.eventradar.util.toast
import com.github.se.eventradar.viewmodel.EventDetailsViewModel

@Composable
fun SelectTicket(viewModel: EventDetailsViewModel, navigationActions: NavigationActions) {

  val eventUiState = viewModel.uiState.collectAsStateWithLifecycle().value

  val context = LocalContext.current

  val componentStyle =
      EventComponentsStyle(
          MaterialTheme.colorScheme.onSurface,
          MaterialTheme.colorScheme.onSurfaceVariant,
          MaterialTheme.colorScheme.onSurface,
      )

  // Error
  GenericDialogBox(
      viewModel.errorOccurred,
      modifier = Modifier.testTag("buyingTicketErrorDialog"),
      "Registration Failure",
      "We are sorry an error occurred during the registration process.") {
        Icon(
            painter = painterResource(id = R.drawable.ticket),
            contentDescription = "ticket icon",
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
        )
      }

  // Success
  GenericDialogBox(
      viewModel.registrationSuccessful,
      modifier = Modifier.testTag("buyingTicketSuccessDialog"),
      "Successful registration",
      "You successfully joined that event",
      { navigationActions.goBack() }) {
        Icon(
            painter = painterResource(id = R.drawable.check),
            contentDescription = "ticket icon",
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
        )
      }

  Scaffold(
      modifier = Modifier.testTag("joinEventScreen"),
      topBar = {
        GoBackButton(modifier = Modifier.wrapContentSize()) { navigationActions.goBack() }
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelected = { tab -> navigationActions.navigateTo(tab) },
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = TOP_LEVEL_DESTINATIONS[2],
            modifier = Modifier.testTag("bottomNavMenu"))
      },
      floatingActionButton = {
        // buy ticket button
        FloatingActionButton(
            onClick = { viewModel.buyTicketForEvent() },
            modifier = Modifier.testTag("buyButton"),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ) {
          val icon = if (viewModel.isTicketFree()) R.drawable.check else R.drawable.credit_card
          Icon(
              painter = painterResource(id = icon),
              contentDescription = "buy ticket button",
              modifier = Modifier.size(32.dp),
              tint = MaterialTheme.colorScheme.onPrimaryContainer,
          )
        }
      }) {
        Column(modifier = Modifier.fillMaxWidth().padding(it).padding(horizontal = 8.dp)) {
          EventTitle(
              modifier = Modifier.align(Alignment.CenterHorizontally),
              eventUiState = eventUiState,
              style = componentStyle)

          Spacer(modifier = Modifier.height(24.dp))

          Text(
              text = stringResource(id = R.string.tickets_title),
              modifier = Modifier.testTag("ticketsTitle"),
              style = componentStyle.subTitleStyle)

          Spacer(modifier = Modifier.height(8.dp))

          Card(
              modifier =
                  Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                      .height(IntrinsicSize.Min)
                      .fillMaxWidth()
                      .testTag("ticketCard"),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
              onClick = {
                context.toast("Multiple ticket types is not part of V1")
                // Would select ticket type the user wants to buy. Not in V1
              }) {
                Row(
                    modifier =
                        Modifier.fillMaxWidth()
                            .fillMaxHeight()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .testTag("ticketInfo"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                      val style =
                          TextStyle(
                              fontSize = 16.sp,
                              fontFamily = FontFamily(Font(R.font.roboto)),
                              fontWeight = FontWeight.Bold,
                          )
                      Text(
                          text = eventUiState.ticket.name,
                          style = style,
                          modifier = Modifier.testTag("ticketName"))
                      Text(
                          text =
                              "${eventUiState.ticket.price} ${stringResource(id = R.string.ticket_currency)}",
                          style = style,
                          modifier = Modifier.testTag("ticketPrice"))
                    }
              }
        }
      }
}
