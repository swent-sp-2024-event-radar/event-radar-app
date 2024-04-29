package com.github.se.eventradar.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

/**  */
class LoginScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<LoginScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("loginScreen") }) {

  // Structural elements of the UI of the Signup screen
  val eventRadarLogo: KNode = onNode { hasTestTag("eventRadarLogo") }
  val loginButton: KNode = onNode { hasTestTag("loginButton") }
  val errorDialog: KNode = onNode { hasTestTag("loginErrorDialog") }
  val errorDialogText: KNode = onNode { hasTestTag("loginErrorDisplayText") }
  val errorDialogButton: KNode = onNode { hasTestTag("errorDialogConfirmButton") }
  val errorDialogIcon: KNode = onNode { hasTestTag("errorDialogIcon") }
  val errorDialogTitle: KNode = onNode { hasTestTag("loginErrorTitle") }
}
