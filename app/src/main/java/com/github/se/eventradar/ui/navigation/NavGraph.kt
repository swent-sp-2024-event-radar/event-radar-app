package com.github.se.eventradar.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.se.eventradar.ui.login.LoginScreen
import com.github.se.eventradar.ui.login.SignUpScreen
import com.github.se.eventradar.ui.overview.Overview
import com.github.se.eventradar.util.toast

@Composable
fun NavGraph(navController: NavHostController) {
  val navActions = NavigationActions(navController)
  val context = LocalContext.current

  NavHost(navController, startDestination = Route.LOGIN) {
    composable(Route.LOGIN) { LoginScreen(navigationActions = navActions) }
    composable(Route.SIGNUP) { SignUpScreen(navigationActions = navActions) }
    composable(Route.OVERVIEW) { Overview(navigationActions = navActions) }

    // TODO replace the Toast message with the corresponding screen function of the route
    composable(Route.SCANNER) { context.toast("Scanner screen needs to be implemented") }
    composable(Route.MESSAGE) { context.toast("Message main screen needs to be implemented") }
    composable(Route.EVENTS) { context.toast("Event main screen needs to be implemented") }
    composable(Route.PROFILE) { context.toast("Profile screen needs to be implemented") }
    composable(Route.MY_HOSTING) {
      context.toast("My hosted events screen needs to be implemented")
    }
  }
}
