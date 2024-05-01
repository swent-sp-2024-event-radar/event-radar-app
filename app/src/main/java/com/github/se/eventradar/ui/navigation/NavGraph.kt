package com.github.se.eventradar.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.github.se.eventradar.model.event.EventDetailsViewModel
import com.github.se.eventradar.ui.event.BuyTicket
import com.github.se.eventradar.ui.event.EventDetails
import com.github.se.eventradar.ui.home.HomeScreen
import com.github.se.eventradar.ui.hosting.HostingScreen
import com.github.se.eventradar.ui.login.LoginScreen
import com.github.se.eventradar.ui.login.SignUpScreen
import com.github.se.eventradar.util.toast

@Composable
fun NavGraph(
    navController: NavHostController,
    navActions: NavigationActions = NavigationActions(navController)
) {
  val context = LocalContext.current

  NavHost(navController, startDestination = Route.LOGIN) {
    composable(Route.LOGIN) { LoginScreen(navigationActions = navActions) }
    composable(Route.SIGNUP) { SignUpScreen(navigationActions = navActions) }
    composable(Route.HOME) { HomeScreen(navigationActions = navActions) }
    composable(
        "${Route.EVENT_DETAILS}/{eventId}",
        arguments = listOf(navArgument("eventId") { type = NavType.StringType })) {
          val eventId = it.arguments!!.getString("eventId")!!

          val viewModel: EventDetailsViewModel = hiltViewModel()
          viewModel.saveEventId(eventId)

          EventDetails(viewModel = viewModel, navigationActions = navActions)
        }
    composable(
        "${Route.EVENT_DETAILS_TICKETS}/{eventId}",
        arguments = listOf(navArgument("eventId") { type = NavType.StringType })) {
          val eventId = it.arguments!!.getString("eventId")!!
          val viewModel: EventDetailsViewModel = hiltViewModel()
          viewModel.saveEventId(eventId)
          BuyTicket(viewModel = viewModel, navigationActions = navActions)
        }

    // TODO replace the Toast message with the corresponding screen function of the route
    composable(Route.SCANNER) {
      HomeScreen(navigationActions = navActions)
      context.toast("Scanner screen needs to be implemented")
    }
    composable(Route.MESSAGE) {
      HomeScreen(navigationActions = navActions)
      context.toast("Message main screen needs to be implemented")
    }
    composable(Route.PROFILE) {
      HomeScreen(navigationActions = navActions)
      context.toast("Profile screen needs to be implemented")
    }
    composable(Route.MY_HOSTING) { HostingScreen(navigationActions = navActions) }
  }
}
