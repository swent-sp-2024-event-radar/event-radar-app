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

  val bottomNav: KNode = child { hasTestTag("bottomNavMenu") }
  private val chatScreenColumn: KNode = child { hasTestTag("chatScreenColumn") }
  val chatScreenMessagesList: KNode =
      chatScreenColumn.child { hasTestTag("chatScreenMessagesList") }

  private val receivedColumn: KNode = chatScreenMessagesList.child { hasTestTag("receivedColumn") }
  val receivedChatBubble: KNode = receivedColumn.child { hasTestTag("receivedChatBubble") }
  val receivedChatBubbleText: KNode =
      receivedChatBubble.child { hasTestTag("receivedChatBubbleText") }

  private val sentColumn: KNode = chatScreenMessagesList.child { hasTestTag("sentColumn") }
  val sentChatBubble: KNode = sentColumn.child { hasTestTag("sentChatBubble") }
  val sentChatBubbleText: KNode = sentChatBubble.child { hasTestTag("sentChatBubbleText") }

  val chatInput: KNode = chatScreenColumn.child { hasTestTag("chatInput") }
  private val chatInputField: KNode = chatInput.child { hasTestTag("chatInputField") }
  val chatInputPlaceholder: KNode = chatInputField.child { hasTestTag("chatInputPlaceholder") }
  private val chatInputSendButton: KNode =
      chatInputField.child { hasTestTag("chatInputSendButton") }
  val chatInputSendButtonIcon: KNode =
      chatInputSendButton.child { hasTestTag("chatInputSendButtonIcon") }
}
