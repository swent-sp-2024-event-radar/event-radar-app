package com.github.se.partyradar.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.github.se.partyradar.map.Map
import com.github.se.partyradar.model.ToDoViewModel
import com.github.se.partyradar.ui.CreateToDo
import com.github.se.partyradar.ui.EditToDo
import com.github.se.partyradar.ui.login.LoginScreen
import com.github.se.partyradar.ui.overview.Overview

@Composable
fun TodoNavGraph(navController: NavHostController) {
  val navActions = NavigationActions(navController)

  NavHost(navController, startDestination = Route.LOGIN) {
    composable(Route.LOGIN) { LoginScreen(navigationActions = navActions) }
    composable(Route.OVERVIEW) { Overview(navigationActions = navActions) }
    composable(Route.MAP) { Map(navigationActions = navActions) }
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
  }
}
