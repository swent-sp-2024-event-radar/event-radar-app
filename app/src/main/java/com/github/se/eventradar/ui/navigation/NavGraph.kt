package com.github.se.eventradar.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.se.eventradar.ui.home.HomeScreen
import com.github.se.eventradar.ui.login.LoginScreen
import com.github.se.eventradar.ui.login.SignUpScreen
import com.github.se.eventradar.ui.qrCode.QrCodeScreen
import com.github.se.eventradar.util.toast

@Composable
fun NavGraph(navController: NavHostController) {
  val navActions = NavigationActions(navController)
  val context = LocalContext.current

  NavHost(navController, startDestination = Route.LOGIN) {
    composable(Route.LOGIN) { LoginScreen(navigationActions = navActions) }
    composable(Route.SIGNUP) { SignUpScreen(navigationActions = navActions) }
    composable(Route.HOME) { HomeScreen(navigationActions = navActions) }

    // TODO replace the Toast message with the corresponding screen function of the route
    composable(Route.SCANNER) {
      QrCodeScreen(navigationActions = navActions)
    }
    composable(Route.MESSAGE) {
      HomeScreen(navigationActions = navActions)
      context.toast("Message main screen needs to be implemented")
    }
    composable(Route.PROFILE) {
      HomeScreen(navigationActions = navActions)
      context.toast("Profile screen needs to be implemented")
    }
    composable(Route.MY_HOSTING) {
      HomeScreen(navigationActions = navActions)
      context.toast("My hosted events screen needs to be implemented")
    }
  }
}
