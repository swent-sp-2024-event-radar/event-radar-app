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
  val homeTab: KNode = bottomNav.child { hasTestTag("homeScreenEventBottomNav") }
  val messagesTab: KNode = bottomNav.child { hasTestTag("messageChatBottomNav") }
  val viewToggleFab: KNode = child { hasTestTag("viewToggleFab") }
  val map: KNode = child { hasTestTag("eventMap") }
  val eventList: KNode = child { hasTestTag("eventList") }
  val searchBarAndFilter: KNode = child { hasTestTag("searchBarAndFilter") }
  val filterButton: KNode = searchBarAndFilter.child { hasTestTag("filterButton") }
  val searchBar: KNode = searchBarAndFilter.child { hasTestTag("searchBar") }
  val filterPopUp: KNode = child { hasTestTag("filterPopUp") }
  val noUpcomingEventsText: KNode = child { hasTestTag("noUpcomingEventsText") }
  val pleaseLogInText: KNode = child { hasTestTag("pleaseLogInText") }
  val eventListUpcoming: KNode = child { hasTestTag("eventListUpcoming") }
  val mapUpcoming: KNode = child { hasTestTag("eventMapUpcoming") }

  private val filterCard: KNode = filterPopUp.child { hasTestTag("filterCard") }
  private val filterCardColumn: KNode = filterCard.child { hasTestTag("filterCardColumn") }
  private val filterCardColumnRow: KNode =
      filterCardColumn.child { hasTestTag("filterCardColumnRow") }
  private val filterCardColumnRowRadius: KNode =
      filterCardColumnRow.child { hasTestTag("filterCardColumnRowRadius") }
  val radiusLabel: KNode = filterCardColumnRowRadius.child { hasTestTag("radiusLabel") }
  private val filterCardColumnRowKm: KNode =
      filterCardColumnRow.child { hasTestTag("filterCardColumnRowKm") }
  val kmLabel: KNode = filterCardColumnRowKm.child { hasTestTag("kmLabel") }
  private val radiusInputBox: KNode = filterCardColumnRow.child { hasTestTag("radiusInputBox") }
  val radiusInput: KNode = radiusInputBox.child { hasTestTag("radiusInput") }
  private val freeSwitchRow: KNode = filterCardColumn.child { hasTestTag("freeSwitchRow") }
  val freeSwitchLabel: KNode = freeSwitchRow.child { hasTestTag("freeSwitchLabel") }
  val freeSwitch: KNode = freeSwitchRow.child { hasTestTag("freeSwitch") }
  val categoryLabel: KNode = filterCardColumn.child { hasTestTag("categoryLabel") }
  private val filterApplyRow: KNode = filterCardColumn.child { hasTestTag("filterApplyRow") }
  val filterApplyButton: KNode = filterApplyRow.child { hasTestTag("filterApplyButton") }
  private val categoryRow: KNode = filterCardColumn.child { hasTestTag("categoryRow") }
  val categoryOptionsColumn: KNode = categoryRow.child { hasTestTag("categoryOptionsColumn") }
  val categoryOptionRow: KNode =
      categoryOptionsColumn.child { hasTestTag("categoryOptionRow-Music") }
}
