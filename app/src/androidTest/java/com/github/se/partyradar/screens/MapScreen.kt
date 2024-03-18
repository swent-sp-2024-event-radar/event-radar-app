package com.github.se.partyradar.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

/**
 * This class represents the Map Screen and the elements it contains.
 *
 * It is used to interact with the UI elements during UI tests, incl. grading! You can adapt the
 * test tags if necessary to suit your own implementation, but the class properties need to stay the
 * same.
 *
 * You can refer to Figma for the naming conventions.
 * https://www.figma.com/file/PHSAMl7fCpqEkkSHGeAV92/TO-DO-APP-Mockup?type=design&node-id=435%3A3350&mode=design&t=GjYE8drHL1ACkQnD-1
 */
class MapScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<MapScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("mapScreen") }) {

  val map: KNode = child { hasTestTag("map") }

  val mapButton: KNode = onNode { hasTestTag("mapBottomNav") }
  val overviewButton: KNode = onNode { hasTestTag("overviewBottomNav") }
}
