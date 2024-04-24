package com.github.se.eventradar.ui.navigation

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.github.se.eventradar.R

object Route {
  // Event radar main screens
  const val SCANNER = "scanner"
  const val MESSAGE = "message"
  const val HOME = "home/Home"
  const val PROFILE = "profile"
  const val MY_HOSTING = "my_hosting"

  // Event radar secondary screens
  const val EVENT_DETAILS = "event_details"

  const val LOGIN = "login/Login"
  const val SIGNUP = "login/SignUp"
}

data class TopLevelDestination(val route: String, val icon: Int, val textId: Int)

class NavigationActions(val navController: NavController) {
  fun navigateTo(destination: TopLevelDestination) {
    navController.navigate(destination.route) {
      // Pop up to the start destination of the graph to
      // avoid building up a large stack of destinations
      // on the back stack as users select items
      popUpTo(navController.graph.findStartDestination().id) { saveState = true }
      // Avoid multiple copies of the same destination when
      // reselecting the same item
      launchSingleTop = true
      // Restore state when reselecting a previously selected item
      restoreState = true
    }
  }

  fun goBack() {
    navController.popBackStack()
  }
}

fun getTopLevelDestination(targetTextId: Int): TopLevelDestination {
  val foundDestination = TOP_LEVEL_DESTINATIONS.find { it.textId == targetTextId }
  if (foundDestination != null) {
    return foundDestination
  } else {
    Log.d("Null Error", "Top level destination does not exist for R String Id: $targetTextId")
    return TOP_LEVEL_DESTINATIONS[2]
  }
}

val TOP_LEVEL_DESTINATIONS =
    listOf(
        TopLevelDestination(
            route = Route.SCANNER,
            icon = R.drawable.qr_code,
            textId = R.string.scan_QR,
        ),
        TopLevelDestination(
            route = Route.MESSAGE,
            icon = R.drawable.chat_bubble,
            textId = R.string.message_chats,
        ),
        TopLevelDestination(
            route = Route.HOME,
            icon = R.drawable.home,
            textId = R.string.homeScreen_events,
        ),
        TopLevelDestination(
            route = Route.MY_HOSTING,
            icon = R.drawable.celebration,
            textId = R.string.hosting,
        ),
        TopLevelDestination(
            route = Route.PROFILE,
            icon = R.drawable.user_profile,
            textId = R.string.user_profile,
        ),
    )
