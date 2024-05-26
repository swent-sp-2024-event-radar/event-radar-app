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

  // MY EvENt TAB - ALL GOOD
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
  val goBackButton: KNode = onNode { hasTestTag("goBackButton") }
  val eventImage: KNode = onNode { hasTestTag("eventImage") }
  val ticketSoldTitle: KNode = onNode { hasTestTag("ticketSoldTitle") }
  val ticketSoldContent: KNode = onNode { hasTestTag("ticketSoldContent") }
}
