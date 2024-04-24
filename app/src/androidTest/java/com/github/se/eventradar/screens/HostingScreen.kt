package com.github.se.eventradar.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class HostingScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<HostingScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("hostingScreen") }) {

  val logo: KNode = child { hasTestTag("logo") }
  val myHostedEventsTitle: KNode = child { hasTestTag("myHostedEventsTitle") }
  val eventCard: KNode = onNode { hasTestTag("eventCard") }
  val bottomNav: KNode = child { hasTestTag("bottomNavMenu") }
  val floatingActionButtons: KNode = child { hasTestTag("floatingActionButtons") }
  val createEventFab: KNode = floatingActionButtons.child { hasTestTag("createEventFab") }
  val viewToggleFab: KNode = floatingActionButtons.child { hasTestTag("viewToggleFab") }
<<<<<<< HEAD
  val map: KNode = child { hasTestTag("map") }
=======
>>>>>>> e48d663 (Created ReusableComponents class, changes made according to PR Review)
}
