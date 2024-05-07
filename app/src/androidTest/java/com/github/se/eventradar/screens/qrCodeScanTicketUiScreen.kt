package com.github.se.eventradar.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class QrCodeScanTicketUiScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<QrCodeScanTicketUiScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("qrCodeScannerScreen") }) {
  val logo: KNode = child { hasTestTag("logo") }
  val tabs: KNode = child { hasTestTag("tabs") }
  val myQrTab: KNode = tabs.child { hasTestTag("My Event") }
  val scanQrTab: KNode = tabs.child { hasTestTag("Scan QR Code") }
  val qrScanner: KNode = child { hasTestTag("QrScanner") }
  val bottomNavMenu: KNode = child { hasTestTag("bottomNavMenu") }
  val approvedBox: KNode = child { hasTestTag("ApprovedBox") }
  val deniedBox: KNode = child { hasTestTag("DeniedBox") }
  val errorBox: KNode = child { hasTestTag("ErrorBox") }
  val closeButton: KNode = child { hasTestTag("closeButton") }
  val approvedText: KNode = child { hasTestTag("EntryApprovedText") }
  val deniedText: KNode = child { hasTestTag("EntryDeniedText") }
  val errorText: KNode = child { hasTestTag("ErrorText") }
}
