package com.github.se.eventradar.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class QrCodeScanFriendUiScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<QrCodeScanFriendUiScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("qrCodeScannerScreen") }) {
  val logo: KNode = child { hasTestTag("logo") }
  val tabs: KNode = child { hasTestTag("tabs") }
  val myQrTab: KNode = tabs.child { hasTestTag("My QR Code") }
  val scanQrTab: KNode = tabs.child { hasTestTag("Scan QR Code") }
  val qrScanner: KNode = child { hasTestTag("QrScanner") }
    val myQrScreen : KNode = child { hasTestTag("myQrCodeScreen")}
  val bottomNavMenu: KNode = child { hasTestTag("bottomNavMenu") }
    val myQrCodeImage : KNode = myQrScreen.child { hasTestTag("myQrCodeImage")}
    val username : KNode = myQrScreen.child { hasTestTag("username")}
}
