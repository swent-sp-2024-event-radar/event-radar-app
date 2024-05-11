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
  val profilePic: KNode = onNode { hasContentDescription("Profile picture of", substring = true) }
  val recipientName: KNode = onNode { hasText("Test 2") }
  val messageContent: KNode = onNode { hasText("Test Message") }
  val messageTime: KNode = onNode { hasText("01/01/21") }
  
  val friendsList: KNode = child { hasTestTag("friendsList") }
  val friendPreviewItem: KNode = friendsList.child { hasTestTag("friendPreviewItem") }
  val friendProfilePic: KNode = onNode { hasContentDescription("Profile picture of", substring = true) }
  val friendName: KNode = onNode { hasText("Test", substring = true) }
  val friendPhoneNumber: KNode = onNode { hasText("TestPhone", substring = true) }
}
