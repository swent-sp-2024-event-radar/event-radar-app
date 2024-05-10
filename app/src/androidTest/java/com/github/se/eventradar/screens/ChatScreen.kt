package com.github.se.eventradar.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

public class ChatScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ChatScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("chatScreen") }) {

  val chatAppBar: KNode = child { hasTestTag("chatAppBar") }
  val chatAppBarTitle: KNode = chatAppBar.child { hasTestTag("chatAppBarTitle") }
  val chatAppBarTitleSurface: KNode = chatAppBarTitle.child { hasTestTag("chatAppBarTitleSurface") }
  val chatAppBarTitleImage: KNode =
      chatAppBarTitleSurface.child { hasTestTag("chatAppBarTitleImage") }
  val chatAppBarTitleColumn: KNode = chatAppBarTitle.child { hasTestTag("chatAppBarTitleColumn") }
  val chatAppBarTitleText: KNode = chatAppBarTitleColumn.child { hasTestTag("chatAppBarTitleText") }
  private val chatAppBarBackArrow: KNode = chatAppBar.child { hasTestTag("chatAppBarBackArrow") }
  val chatAppBarBackArrowIcon: KNode =
      chatAppBarBackArrow.child { hasTestTag("chatAppBarBackArrowIcon") }

  val bottomNav: KNode = child { hasTestTag("bottomNavMenu") }
  private val chatScreenColumn: KNode = child { hasTestTag("chatScreenColumn") }
  val chatScreenMessagesList: KNode =
      chatScreenColumn.child { hasTestTag("chatScreenMessagesList") }

  private val receivedColumn: KNode = chatScreenMessagesList.child { hasTestTag("receivedColumn") }
  private val receivedChatBubble: KNode = receivedColumn.child { hasTestTag("receivedChatBubble") }
  private val receivedChatBubbleText: KNode =
      receivedChatBubble.child { hasTestTag("receivedChatBubbleText") }
  val receivedMessageText: KNode =
      receivedChatBubbleText.child { hasTestTag("receivedMessageText") }

  private val sentColumn: KNode = chatScreenMessagesList.child { hasTestTag("sentColumn") }
  private val sentChatBubble: KNode = sentColumn.child { hasTestTag("sentChatBubble") }
  private val sentChatBubbleText: KNode = sentChatBubble.child { hasTestTag("sentChatBubbleText") }
  private val sentChatBubbleTimeRow: KNode =
      sentChatBubbleText.child { hasTestTag("sentChatBubbleTimeRow") }
  val messageTimeRowText: KNode = sentChatBubbleTimeRow.child { hasTestTag("messageTimeRowText") }
  val messageTimeRowIcon: KNode = sentChatBubbleTimeRow.child { hasTestTag("messageTimeRowIcon") }

  private val chatInput: KNode = chatScreenColumn.child { hasTestTag("chatInput") }
  val chatInputField: KNode = chatInput.child { hasTestTag("chatInputField") }
  val chatInputPlaceholder: KNode = chatInput.child { hasTestTag("chatInputPlaceholder") }
  private val chatInputTrailingIcon: KNode = chatInput.child { hasTestTag("chatInputTrailingIcon") }
  private val chatInputSendButton: KNode =
      chatInputTrailingIcon.child { hasTestTag("chatInputSendButton") }
  val chatInputSendButtonIcon: KNode =
      chatInputSendButton.child { hasTestTag("chatInputSendButtonIcon") }
  private val chatInputCameraButton: KNode =
      chatInputTrailingIcon.child { hasTestTag("chatInputCameraButton") }
  val chatInputCameraButtonIcon: KNode =
      chatInputCameraButton.child { hasTestTag("chatInputCameraButtonIcon") }
}
