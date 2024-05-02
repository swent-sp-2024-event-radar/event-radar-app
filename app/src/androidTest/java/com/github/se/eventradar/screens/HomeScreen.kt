package com.github.se.eventradar.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.hasTestTag
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
  val filterButton: KNode = searchBarAndFilter.child { hasTestTag("filterButton") }
  val searchBar: KNode = searchBarAndFilter.child { hasTestTag("searchBar") }
  val eventSearch: KNode = searchBar.child { hasSetTextAction() }
  val filterPopUp: KNode = child { hasTestTag("filterPopUp") }
  val filteredEventList: KNode = child { hasTestTag("filteredEventList") }
  val filteredMap: KNode = child { hasTestTag("filteredMap") }
  val radiusInput: KNode = filterPopUp.child { hasTestTag("radiusInput") }
  val freeSwitch: KNode = filterPopUp.child { hasTestTag("freeSwitch") }
  val filterApplyButton: KNode = filterPopUp.child { hasTestTag("filterApplyButton") }
}
