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

  val errorDialog: KNode = onNode { hasTestTag("buyingTicketErrorDialog") }
  val successDialog: KNode = onNode { hasTestTag("buyingTicketSuccessDialog") }
  val okButton: KNode =
      KNode(semanticsProvider) {
        hasAnyAncestor((androidx.compose.ui.test.hasTestTag("buyingTicketSuccessDialog")))
        hasTestTag("dialogConfirmButton")
      }

  val eventTitle: KNode = onNode { hasTestTag("eventTitle") }
  val ticketsTitle: KNode = onNode { hasTestTag("ticketsTitle") }
  val ticketCard: KNode = onNode { hasTestTag("ticketCard") }
  val ticketInfo: KNode =
      KNode(semanticsProvider) {
        hasAnyAncestor((androidx.compose.ui.test.hasTestTag("ticketCard")))
        hasTestTag("ticketInfo")
      }
  val ticketName: KNode =
      KNode(semanticsProvider) {
        hasAnyAncestor((androidx.compose.ui.test.hasTestTag("ticketInfo")))
        hasTestTag("ticketName")
      }
  val ticketPrice: KNode =
      KNode(semanticsProvider) {
        hasAnyAncestor((androidx.compose.ui.test.hasTestTag("ticketInfo")))
        hasTestTag("ticketPrice")
      }
}
