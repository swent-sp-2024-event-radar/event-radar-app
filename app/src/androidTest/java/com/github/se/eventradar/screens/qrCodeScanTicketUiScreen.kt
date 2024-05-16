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
  val goBackButton: KNode = onNode { hasTestTag("goBackButton") }
  val eventImage: KNode = onNode { hasTestTag("eventImage") }

  // Text fields
  val eventTitle: KNode = child { hasTestTag("eventTitle") }
  val descriptionTitle: KNode = child { hasTestTag("descriptionTitle") }
  val descriptionContent: KNode = child { hasTestTag("descriptionContent") }
  val distanceTitle: KNode = child { hasTestTag("distanceTitle") }
  val distanceContent: KNode = child { hasTestTag("distanceContent") }
  val dateTitle: KNode = child { hasTestTag("dateTitle") }
  val dateTimeTitle: KNode = child { hasTestTag("timeTitle") }
  val dateTimeStartContent: KNode = child { hasTestTag("timeStartContent") }
  val dateTimeEndContent: KNode = child { hasTestTag("timeEndContent") }
  val dateContent: KNode = child { hasTestTag("dateContent") }
  val categoryTitle: KNode = child { hasTestTag("categoryTitle") }
  val categoryContent: KNode = child { hasTestTag("categoryContent") }
  val ticketSoldTitle: KNode = child { hasTestTag("ticketSoldTitle") }
  val ticketSoldContent: KNode = child { hasTestTag("ticketSoldContent") }
}
