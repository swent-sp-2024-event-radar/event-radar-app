package com.github.se.eventradar.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

public class EventDetailsScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<EventDetailsScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("eventDetailsScreen") }) {

  val ticketButton: KNode = onNode { hasTestTag("ticketButton") }
  val goBackButton: KNode = onNode { hasTestTag("goBackButton") }
  val bottomNav: KNode = onNode { hasTestTag("bottomNavMenu") }
  val eventImage: KNode = onNode { hasTestTag("eventImage") }

  // Text fields
  val eventTitle: KNode = onNode { hasTestTag("eventTitle") }
  val descriptionTitle: KNode = onNode { hasTestTag("descriptionTitle") }
  val descriptionContent: KNode = onNode { hasTestTag("descriptionContent") }
  val distanceTitle: KNode = onNode { hasTestTag("distanceTitle") }
  val distanceContent: KNode = onNode { hasTestTag("distanceContent") }
  val dateTitle: KNode = onNode { hasTestTag("dateTitle") }
  val dateContent: KNode = onNode { hasTestTag("dateContent") }
  val timeTitle: KNode = onNode { hasTestTag("timeTitle") }
  val timeContent: KNode = onNode { hasTestTag("timeContent") }
  val categoryTitle: KNode = onNode { hasTestTag("categoryTitle") }
  val categoryContent: KNode = onNode { hasTestTag("categoryContent") }
  val attendance: KNode = onNode { hasTestTag("attendance") }

  val cancelRegistrationDialog: KNode = onNode { hasTestTag("cancelRegistrationDialog") }
  val confirmButton: KNode = onNode { hasTestTag("dialogConfirmButton") }
}
