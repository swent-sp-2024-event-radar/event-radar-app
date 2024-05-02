package com.github.se.eventradar.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class HomeScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<HomeScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("homeScreen") }) {

  val logo: KNode = child { hasTestTag("logo") }
  val tabs: KNode = child { hasTestTag("tabs") }
  val upcomingTab: KNode = tabs.child { hasTestTag("upcomingTab") }
  val browseTab: KNode = tabs.child { hasTestTag("browseTab") }
  val eventCard: KNode = onNode { hasTestTag("eventCard") }
  val bottomNav: KNode = child { hasTestTag("bottomNavMenu") }
  val viewToggleFab: KNode = child { hasTestTag("viewToggleFab") }
  val map: KNode = child { hasTestTag("map") }
  val eventList: KNode = child { hasTestTag("eventList") }
  val searchBarAndFilter: KNode = child { hasTestTag("searchBarAndFilter") }
  val filterPopUp: KNode = child { hasTestTag("filterPopUp") }
  // val noUpcomingEventsText: KNode = child { hasTestTag("noUpcomingEventsText") }
  // val pleaseLogInText: KNode = child { hasTestTag("pleaseLogInText") }
}
