package com.github.se.eventradar.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class ViewFriendsProfileScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ViewFriendsProfileScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("viewFriendsProfileScreen") }) {

  val chatButton: KNode = onNode { hasTestTag("chatButton") }
  val goBackButton: KNode = onNode { hasTestTag("goBackButton") }
  val bottomNav: KNode = onNode { hasTestTag("bottomNavMenu") }
  val centeredViewProfileColumn: KNode = onNode { hasTestTag("centeredViewProfileColumn") }
  val friendProfilePic: KNode = centeredViewProfileColumn.child { hasTestTag("friendProfilePic") }
  val friendName: KNode = centeredViewProfileColumn.child { hasTestTag("friendName") }
  val friendUserName: KNode = centeredViewProfileColumn.child { hasTestTag("friendUserName") }
  val leftAlignedViewProfileColumn: KNode =
      centeredViewProfileColumn.child { hasTestTag("leftAlignedViewProfileColumn") }
  val bioLabelText: KNode = leftAlignedViewProfileColumn.child { hasTestTag("bioLabelText") }
  val bioInfoText: KNode = leftAlignedViewProfileColumn.child { hasTestTag("bioInfoText") }
}
