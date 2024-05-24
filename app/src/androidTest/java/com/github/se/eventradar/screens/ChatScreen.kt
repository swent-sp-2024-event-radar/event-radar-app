package com.github.se.eventradar.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

public class ChatScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ChatScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("chatScreen") }) {

  val chatAppBar: KNode = child { hasTestTag("chatAppBar") }
  val chatAppBarTitle: KNode = chatAppBar.child { hasTestTag("chatAppBarTitle") }
  val chatAppBarTitleImage: KNode = chatAppBarTitle.child { hasTestTag("chatAppBarTitleImage") }
  val chatAppBarTitleColumn: KNode = chatAppBarTitle.child { hasTestTag("chatAppBarTitleColumn") }
  val chatAppBarBackArrow: KNode = chatAppBar.child { hasTestTag("chatAppBarBackArrow") }

  private val chatScreenColumn: KNode = child { hasTestTag("chatScreenColumn") }
  val chatScreenMessagesList: KNode =
      chatScreenColumn.child { hasTestTag("chatScreenMessagesList") }
  val bottomNav: KNode = child { hasTestTag("bottomNavMenu") }

  val chatInput: KNode = chatScreenColumn.child { hasTestTag("chatInput") }
  val chatInputField: KNode = chatInput.child { hasTestTag("chatInputField") }
  val chatInputSendButton: KNode = chatInputField.child { hasTestTag("chatInputSendButton") }
  val chatInputPlaceholder: KNode = onNode { hasText("Type here…") }
  val chatInputSendButtonIcon: KNode = onNode { hasContentDescription("Send", substring = true) }

  private val receivedColumn: KNode = chatScreenMessagesList.child { hasTestTag("receivedColumn") }
  val receivedChatBubble: KNode = receivedColumn.child { hasTestTag("receivedChatBubble") }
  val receivedChatBubbleText: KNode = onNode { hasText("Test Message 2") }

  private val sentColumn: KNode = chatScreenMessagesList.child { hasTestTag("sentColumn") }
  val sentChatBubble: KNode = sentColumn.child { hasTestTag("sentChatBubble") }
  val sentChatBubbleText: KNode = onNode { hasText("Test Message 1") }
}
