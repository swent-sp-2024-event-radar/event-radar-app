package com.github.se.eventradar.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class EventSelectTicketScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<EventSelectTicketScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("joinEventScreen") }) {

  val buyButton: KNode = onNode { hasTestTag("buyButton") }
  val goBackButton: KNode = onNode { hasTestTag("goBackButton") }
  val bottomNav: KNode = onNode { hasTestTag("bottomNavMenu") }

  val eventTitle: KNode = onNode { hasTestTag("eventTitle") }
  val ticketsTitle: KNode = onNode { hasTestTag("ticketsTitle") }
  val ticketCard: KNode = onNode { hasTestTag("ticketCard") }
  val ticketInfo: KNode = ticketCard.child { hasTestTag("ticketInfo") }
  val ticketName: KNode = ticketInfo.child { hasTestTag("ticketName") }
  val ticketPrice: KNode = ticketInfo.child { hasTestTag("ticketPrice") }
}
