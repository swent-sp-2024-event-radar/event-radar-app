package com.github.se.eventradar.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.github.se.eventradar.map.Map
import com.github.se.eventradar.ui.login.LoginScreen
import com.github.se.eventradar.ui.login.SignUpScreen
import com.github.se.eventradar.ui.overview.Overview

@Composable
fun NavGraph(navController: NavHostController) {
  val navActions = NavigationActions(navController)

  NavHost(navController, startDestination = Route.LOGIN) {
    composable(Route.LOGIN) { LoginScreen(navigationActions = navActions) }
    composable(Route.OVERVIEW) { Overview(navigationActions = navActions) }
    composable(Route.MAP) { Map(navigationActions = navActions) }
    composable(Route.SIGN_UP) { SignUpScreen(navigationActions = navActions) }
    // The NEW_TASK and EDIT_TASK routes are being temporarily disabled as we restructure our app
    /*
    composable(Route.NEW_TASK) { CreateToDo(navigationActions = navActions) }
    composable(
        "${Route.EDIT_TASK}/{taskId}",
        arguments = listOf(navArgument("taskId") { type = NavType.StringType })) {
          val taskId = it.arguments!!.getString("taskId")!!
          EditToDo(
              taskId = taskId,
              viewModel = ToDoViewModel(uid = taskId),
              navigationActions = navActions)
        }
       */
  }
}
