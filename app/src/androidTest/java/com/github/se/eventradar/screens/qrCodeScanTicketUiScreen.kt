package com.github.se.eventradar.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class QrCodeScanTicketUiScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<QrCodeScanTicketUiScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("qrCodeScannerScreen") }) {

  // BOTH TABS
  val logo: KNode = child { hasTestTag("logo") }
  val tabs: KNode = child { hasTestTag("tabs") }
  val myQrTab: KNode = tabs.child { hasTestTag("My Event") }
  val scanQrTab: KNode = tabs.child { hasTestTag("Scan QR Code") }
  val bottomNavMenu: KNode = child { hasTestTag("bottomNavMenu") }

  // QR SCANNING TAB
  val qrScanner: KNode = child { hasTestTag("QrScanner") }
  val approvedBox: KNode = onNode { hasTestTag("ApprovedBox") }
  val deniedBox: KNode = onNode { hasTestTag("DeniedBox") }
  val errorBox: KNode = onNode { hasTestTag("ErrorBox") }
  val closeButton: KNode = onNode { hasTestTag("closeButton") }
  val approvedText: KNode = onNode { hasTestTag("EntryApprovedText") }
  val deniedText: KNode = onNode { hasTestTag("EntryDeniedText") }
  val errorText: KNode = onNode { hasTestTag("ErrorText") }

  // MY EvENt TAB
  val lazyEventDetails: KNode = child { hasTestTag("lazyEventDetails") }
  val goBackButton: KNode = lazyEventDetails.child { hasTestTag("goBackButton") }
  val eventImage: KNode = lazyEventDetails.child { hasTestTag("eventImage") }
  val eventTitle: KNode = lazyEventDetails.child { hasTestTag("eventTitle") }
  val descriptionTitle: KNode = lazyEventDetails.child { hasTestTag("descriptionTitle") }
  val descriptionContent: KNode = lazyEventDetails.child { hasTestTag("descriptionContent") }
  val distanceTitle: KNode = lazyEventDetails.child { hasTestTag("distanceTitle") }
  val distanceContent: KNode = lazyEventDetails.child { hasTestTag("distanceContent") }
  val dateTitle: KNode = lazyEventDetails.child { hasTestTag("dateTitle") }
  val dateContent: KNode = lazyEventDetails.child { hasTestTag("dateContent") }
  val timeTitle: KNode = lazyEventDetails.child { hasTestTag("timeTitle") }
  val timeContent: KNode = lazyEventDetails.child { hasTestTag("timeContent") }
  val categoryTitle: KNode = lazyEventDetails.child { hasTestTag("categoryTitle") }
  val categoryContent: KNode = lazyEventDetails.child { hasTestTag("categoryContent") }
  val ticketSoldContent: KNode = lazyEventDetails.child { hasTestTag("ticketSoldContent") }
}
