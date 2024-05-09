package com.github.se.eventradar.ui.event

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.se.eventradar.R
import com.github.se.eventradar.model.event.EventDetailsViewModel
import com.github.se.eventradar.ui.BottomNavigationMenu
import com.github.se.eventradar.ui.component.EventComponentsStyle
import com.github.se.eventradar.ui.component.EventTitle
import com.github.se.eventradar.ui.component.GoBackButton
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.eventradar.util.toast

@Composable
fun SelectTicket(
    viewModel: EventDetailsViewModel = hiltViewModel(),
    navigationActions: NavigationActions
) {

  /*// TODO to be moved in viewModel init
  LaunchedEffect(Unit) { // Using `Unit` as a key to run only once
    viewModel.getEventData()
  }*/

  val eventUiState = viewModel.uiState.collectAsStateWithLifecycle().value

  val context = LocalContext.current

  val componentStyle =
      EventComponentsStyle(
          MaterialTheme.colorScheme.onSurface,
          MaterialTheme.colorScheme.onSurfaceVariant,
          MaterialTheme.colorScheme.onSurface,
      )

  Scaffold(
      modifier = Modifier.testTag("joinEventScreen"),
      topBar = {},
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
            onClick = {
              context.toast("Buy ticket needs to be implemented")
              /*TODO launch action when buying a ticket */
            },
            modifier = Modifier.padding(bottom = 16.dp, end = 16.dp).testTag("buyButton"),
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
      }) { innerPadding ->
        ConstraintLayout(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
          val (backButton, title, ticketTitle, ticketCard) = createRefs()

          GoBackButton(
              modifier =
                  Modifier.wrapContentSize().constrainAs(backButton) {
                    top.linkTo(parent.top, margin = 12.dp)
                    start.linkTo(parent.start)
                  }) {
                navigationActions.goBack()
              }

          EventTitle(
              modifier =
                  Modifier.constrainAs(title) {
                    top.linkTo(backButton.bottom, margin = 42.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                  },
              eventUiState = eventUiState,
              style = componentStyle)

          Text(
              text = stringResource(id = R.string.tickets_title),
              modifier =
                  Modifier.constrainAs(ticketTitle) {
                        top.linkTo(title.bottom, margin = 32.dp)
                        start.linkTo(parent.start, margin = 32.dp)
                      }
                      .testTag("ticketsTitle"),
              style = componentStyle.subTitleStyle)

          Card(
              modifier =
                  Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
                      .height(IntrinsicSize.Min)
                      .fillMaxWidth()
                      .testTag("ticketCard")
                      .constrainAs(ticketCard) {
                        top.linkTo(ticketTitle.bottom, margin = 32.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                      },
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
