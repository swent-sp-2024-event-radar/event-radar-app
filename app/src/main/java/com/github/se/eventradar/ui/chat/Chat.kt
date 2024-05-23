package com.github.se.eventradar.ui.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.eventradar.ExcludeFromJacocoGeneratedReport
import com.github.se.eventradar.R
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.message.Message
import com.github.se.eventradar.model.message.MessageHistory
import com.github.se.eventradar.ui.BottomNavigationMenu
import com.github.se.eventradar.ui.component.ProfilePic
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.eventradar.ui.navigation.TopLevelDestination
import com.github.se.eventradar.viewmodel.ChatUiState
import com.github.se.eventradar.viewmodel.ChatViewModel
import java.time.LocalDateTime

@Composable
fun ChatScreen(viewModel: ChatViewModel, navigationActions: NavigationActions) {
  val uiState by viewModel.uiState.collectAsState()

  ChatScreenUi(
      uiState = uiState,
      onBackArrowClick = navigationActions::goBack,
      onTabSelected = navigationActions::navigateTo,
      onViewProfileClick = navigationActions.navController::navigate,
      onMessageChange = viewModel::onMessageBarInputChange,
      onMessageSend = viewModel::onMessageSend)
}

@Composable
fun ChatScreenUi(
    uiState: ChatUiState,
    onBackArrowClick: () -> Unit,
    onTabSelected: (TopLevelDestination) -> Unit,
    onViewProfileClick: (String) -> Unit,
    onMessageChange: (String) -> Unit,
    onMessageSend: () -> Unit,
) {
  val messages = uiState.messageHistory.messages

  val opponentName = uiState.opponentProfile.firstName
  val opponentSurname = uiState.opponentProfile.lastName
  val opponentPictureUrl = uiState.opponentProfile.profilePicUrl
  val opponentUserId = uiState.opponentProfile.userId
  val scrollState = rememberLazyListState(initialFirstVisibleItemIndex = messages.size)

  LaunchedEffect(key1 = messages.size) {
    if (messages.isNotEmpty()) {
      scrollState.animateScrollToItem(index = messages.size - 1)
    }
  }

  Scaffold(
      modifier = Modifier.testTag("chatScreen"),
      topBar = {
        ChatAppBar(
            opponentName = opponentName,
            opponentSurname = opponentSurname,
            pictureUrl = opponentPictureUrl,
            onUserNameClick = { onViewProfileClick("${Route.PROFILE}/${opponentUserId}") },
            onBackArrowClick = onBackArrowClick,
        )
      },
  ) {
        Column(
            modifier =
                Modifier.padding(it)
                    .fillMaxSize()
                    .focusable()
                    .wrapContentHeight()
                    .imePadding()
                    .testTag("chatScreenColumn")) {
              LazyColumn(
                  modifier = Modifier.weight(1f).fillMaxWidth().testTag("chatScreenMessagesList"),
                  state = scrollState) {
                    items(messages) { message ->
                      when (message.sender == uiState.opponentProfile.userId) {
                        true -> {
                          ReceivedMessageRow(
                              text = message.content,
                          )
                        }
                        false -> {
                          SentMessageRow(
                              text = message.content,
                          )
                        }
                      }
                    }
                  }
              ChatInput(
                  uiState = uiState,
                  onMessageChange = onMessageChange,
                  onMessageSend = onMessageSend,
              )
            }
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatAppBar(
    opponentName: String,
    opponentSurname: String,
    pictureUrl: String,
    onUserNameClick: (() -> Unit)? = null,
    onBackArrowClick: (() -> Unit)? = null,
) {
  TopAppBar(
      title = {
        Row(modifier = Modifier.testTag("chatAppBarTitle")) {
          ProfilePic(
              profilePicUrl = pictureUrl,
              firstName = opponentName,
              lastName = opponentSurname,
              modifier = Modifier.testTag("chatAppBarTitleImage"))
          Column(
              modifier =
                  Modifier.clickable { onUserNameClick?.invoke() }.testTag("chatAppBarTitleColumn"),
              verticalArrangement = Arrangement.Center) {
                Text(
                    text = "$opponentName $opponentSurname",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier =
                        Modifier.padding(top = 16.dp, start = 8.dp).testTag("chatAppBarTitleText"))
              }
        }
      },
      modifier = Modifier.height(72.dp).fillMaxWidth().padding(top = 8.dp).testTag("chatAppBar"),
      navigationIcon = {
        IconButton(
            onClick = { onBackArrowClick?.invoke() },
            modifier = Modifier.testTag("chatAppBarBackArrow")) {
              Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                  contentDescription = "Localized description",
                  modifier = Modifier.padding(top = 8.dp).testTag("chatAppBarBackArrowIcon"))
            }
      })
}

@Composable
fun ChatInput(uiState: ChatUiState, onMessageChange: (String) -> Unit, onMessageSend: () -> Unit) {
  Row(
      modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("chatInput"),
      verticalAlignment = Alignment.Bottom) {
        TextField(
            modifier =
                Modifier.clip(MaterialTheme.shapes.extraLarge)
                    .weight(1f)
                    .focusable(true)
                    .testTag("chatInputField"),
            value = uiState.messageBarInput,
            onValueChange = { onMessageChange(it) },
            colors =
                TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent),
            placeholder = {
              Text(
                  text = stringResource(R.string.message_bar_placeholder),
                  modifier = Modifier.testTag("chatInputPlaceholder"))
            },
            trailingIcon = {
              IconButton(
                  onClick = { onMessageSend() },
                  modifier = Modifier.testTag("chatInputSendButton")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        modifier = Modifier.testTag("chatInputSendButtonIcon"))
                  }
            })
      }
}

@Preview(showBackground = true, showSystemUi = true)
@ExcludeFromJacocoGeneratedReport
@Composable
fun ChatScreenPreview() {
  val sampleUiState =
      ChatUiState(
          userId = "1",
          messageHistory =
              MessageHistory(
                  user1 = "Default sender",
                  user2 = "Default recipient",
                  latestMessageId = "DefaultId",
                  user1ReadMostRecentMessage = false,
                  user2ReadMostRecentMessage = false,
                  messages =
                      mutableListOf(
                          Message(
                              sender = "1",
                              content = "Test Message 1",
                              dateTimeSent = LocalDateTime.now(),
                              id = "1"),
                          Message(
                              sender = "2",
                              content = "Test Message 2",
                              dateTimeSent = LocalDateTime.now(),
                              id = "2"),
                          Message(
                              sender = "1",
                              content =
                                  "Test Message 3 Test Message 3 Test Message 3 Test Message 3 Test Message 3 Test Message 3",
                              dateTimeSent = LocalDateTime.now(),
                              id = "3"),
                          Message(
                              sender = "2",
                              content =
                                  "Test Message 4 Test Message 4 Test Message 4 Test Message 4 Test Message 4 Test Message 4",
                              dateTimeSent = LocalDateTime.now(),
                              id = "4"),
                          Message(
                              sender = "1",
                              content = "Test Message 5",
                              dateTimeSent = LocalDateTime.now(),
                              id = "5"),
                          Message(
                              sender = "1",
                              content = "Test Message 6",
                              dateTimeSent = LocalDateTime.now(),
                              id = "6"),
                          Message(
                              sender = "1",
                              content = "Test Message 7",
                              dateTimeSent = LocalDateTime.now(),
                              id = "7"))),
          opponentProfile =
              User(
                  userId = "2",
                  birthDate = "01/01/2000",
                  email = "",
                  firstName = "Test",
                  lastName = "2",
                  phoneNumber = "",
                  accountStatus = "active",
                  eventsAttendeeList = mutableListOf(),
                  eventsHostList = mutableListOf(),
                  friendsList = mutableListOf(),
                  profilePicUrl =
                      "https://firebasestorage.googleapis.com/v0/b/event-radar-e6a76.appspot.com/o/Profile_Pictures%2Fplaceholder.png?alt=media&token=ba4b4efb-ff45-4617-b60f-3789e8fb75b6",
                  qrCodeUrl = "",
                  bio = "",
                  username = "Test2"))

  ChatScreenUi(
      uiState = sampleUiState,
      onBackArrowClick = {},
      onTabSelected = {},
      onMessageChange = {},
      onViewProfileClick = {},
      onMessageSend = {})
}
