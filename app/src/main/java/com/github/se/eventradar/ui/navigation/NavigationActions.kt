package com.github.se.eventradar.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.github.se.eventradar.R

object Route {
    const val LOGIN = "login/Login"
    const val OVERVIEW = "overview/Overview"
    const val MAP = "map/Map"
    const val SIGN_UP = "sign_up"
  // The NEW_TASK and EDIT_TASK routes are being temporarily disabled as we restructure our app
  /*
  const val NEW_TASK = "newTask/NewTask"
  const val EDIT_TASK = "editTask/EditTask"
     */
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

val TOP_LEVEL_DESTINATIONS =
    listOf(
        TopLevelDestination(
            route = Route.OVERVIEW,
            icon = R.drawable.menu,
            textId = R.string.overview,
        ),
        TopLevelDestination(
            route = Route.MAP,
            icon = R.drawable.language,
            textId = R.string.map,
        ),
    )
