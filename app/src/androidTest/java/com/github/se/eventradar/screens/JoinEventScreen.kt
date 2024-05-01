package com.github.se.eventradar.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

public class JoinEventScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<JoinEventScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("eventDetailsScreen") }) {

  val buyButton: KNode = onNode { hasTestTag("buyButton") }
  val goBackButton: KNode = onNode { hasTestTag("goBackButton") }
  val bottomNav: KNode = onNode { hasTestTag("bottomNavMenu") }

  val eventTitle: KNode = onNode { hasTestTag("eventTitle") }
  val ticketsTitle: KNode = onNode { hasTestTag("ticketsTitle") }
  val ticketCard: KNode = onNode { hasTestTag("ticketCard") }
  val ticketName: KNode = ticketCard.child { hasTestTag("ticketName") }
  val ticketPrice: KNode = ticketCard.child { hasTestTag("ticketPrice") }
}
