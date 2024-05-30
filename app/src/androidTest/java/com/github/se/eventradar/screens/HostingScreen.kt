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
  val map: KNode = child { hasTestTag("map") }
  val eventList: KNode = child { hasTestTag("eventList") }
  val noEventsFoundText: KNode = child { hasTestTag("noEventsFoundText") }

  val searchBarAndFilter: KNode = child { hasTestTag("searchBarAndFilter") }
  val filterButton: KNode = searchBarAndFilter.child { hasTestTag("filterButton") }
  val searchBar: KNode = searchBarAndFilter.child { hasTestTag("searchBar") }
  val filterPopUp: KNode = child { hasTestTag("filterPopUp") }

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
