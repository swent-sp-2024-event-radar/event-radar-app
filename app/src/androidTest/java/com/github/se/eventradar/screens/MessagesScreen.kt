package com.github.se.eventradar.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

public class MessagesScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<MessagesScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("messagesScreen") }) {

  val bottomNav: KNode = child { hasTestTag("bottomNavMenu") }
  val logo: KNode = child { hasTestTag("logo") }
  val tabs: KNode = child { hasTestTag("tabs") }
  val messagesTab: KNode = tabs.child { hasTestTag("messagesTab") }
  val contactsTab: KNode = tabs.child { hasTestTag("contactsTab") }

  val messagesList: KNode = child { hasTestTag("messagesList") }
  val messagePreviewItem: KNode = messagesList.child { hasTestTag("messagePreviewItem") }
  val messageContentColumn: KNode = messagePreviewItem.child { hasTestTag("messageContentColumn") }
  val profilePic: KNode = messagePreviewItem.child { hasTestTag("profilePic") }
  val recipientName: KNode = child { hasTestTag("recipientName") }
  val messageContent: KNode = child { hasTestTag("messageContent") }
  val messageTime: KNode = child { hasTestTag("messageTime") }
}
