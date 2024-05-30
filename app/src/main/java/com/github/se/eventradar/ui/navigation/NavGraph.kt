package com.github.se.eventradar.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.github.se.eventradar.ui.chat.ChatScreen
import com.github.se.eventradar.ui.event.EventDetails
import com.github.se.eventradar.ui.event.SelectTicket
import com.github.se.eventradar.ui.home.HomeScreen
import com.github.se.eventradar.ui.hosting.HostingScreen
import com.github.se.eventradar.ui.login.LoginScreen
import com.github.se.eventradar.ui.login.SignUpScreen
import com.github.se.eventradar.ui.messages.MessagesScreen
import com.github.se.eventradar.ui.qrCode.QrCodeScreen
import com.github.se.eventradar.ui.qrCode.QrCodeTicketUi
import com.github.se.eventradar.ui.viewProfile.ViewFriendsProfileUi
import com.github.se.eventradar.util.toast
import com.github.se.eventradar.viewmodel.ChatViewModel
import com.github.se.eventradar.viewmodel.EventDetailsViewModel
import com.github.se.eventradar.viewmodel.ViewFriendsProfileViewModel
import com.github.se.eventradar.viewmodel.qrCode.ScanFriendQrViewModel
import com.github.se.eventradar.viewmodel.qrCode.ScanTicketQrViewModel

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
          val viewModel = EventDetailsViewModel.create(eventId = eventId)
          EventDetails(viewModel = viewModel, navigationActions = navActions)
        }
    composable(
        "${Route.EVENT_DETAILS_TICKETS}/{eventId}",
        arguments = listOf(navArgument("eventId") { type = NavType.StringType })) {
          val eventId = it.arguments!!.getString("eventId")!!
          val viewModel = EventDetailsViewModel.create(eventId = eventId)
          SelectTicket(viewModel = viewModel, navigationActions = navActions)
        }

    composable(
        "${Route.PRIVATE_CHAT}/{opponentId}",
        arguments = listOf(navArgument("opponentId") { type = NavType.StringType })) {
          val opponentId = it.arguments!!.getString("opponentId")!!
          val viewModel = ChatViewModel.create(opponentId = opponentId)
          ChatScreen(viewModel = viewModel, navigationActions = navActions)
        }
    composable(
        "${Route.PROFILE}/{friendUserId}",
        arguments = listOf(navArgument("friendUserId") { type = NavType.StringType })) {
          val friendUserId = it.arguments!!.getString("friendUserId")!!
          val viewModel = ViewFriendsProfileViewModel.create(friendUserId = friendUserId)
          ViewFriendsProfileUi(viewModel = viewModel, navigationActions = navActions)
        }
    // TODO replace the Toast message with the corresponding screen function of the route

    composable(
        "${Route.MY_EVENT}/{eventId}",
        arguments = listOf(navArgument("eventId") { type = NavType.StringType })) {
          val eventId = it.arguments!!.getString("eventId")!!
          val viewModel = ScanTicketQrViewModel.create(eventId = eventId)
          QrCodeTicketUi(viewModel, navigationActions = navActions)
        }

    composable(Route.MESSAGE) { MessagesScreen(navigationActions = navActions) }
    composable(Route.SCANNER) {
      val viewModel = ScanFriendQrViewModel.create(navigationActions = navActions)
      QrCodeScreen(viewModel = viewModel, navigationActions = navActions)
    }
    composable(Route.PROFILE) {
      HomeScreen(navigationActions = navActions)
      context.toast("Profile screen needs to be implemented")
    }
    composable(Route.MY_HOSTING) { HostingScreen(navigationActions = navActions) }
  }
}
