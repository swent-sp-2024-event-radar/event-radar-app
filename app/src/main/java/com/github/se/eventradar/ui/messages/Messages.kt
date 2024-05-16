package com.github.se.eventradar.ui.messages

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.se.eventradar.ExcludeFromJacocoGeneratedReport
import com.github.se.eventradar.R
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.message.Message
import com.github.se.eventradar.model.message.MessageHistory
import com.github.se.eventradar.ui.BottomNavigationMenu
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.eventradar.ui.navigation.TopLevelDestination
import com.github.se.eventradar.viewmodel.MessagesUiState
import com.github.se.eventradar.viewmodel.MessagesViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun MessagesScreen(
    viewModel: MessagesViewModel = hiltViewModel(),
    navigationActions: NavigationActions
) {
  val uiState by viewModel.uiState.collectAsState()
  val context = LocalContext.current // only needed while the chat feature is not implemented

  MessagesScreenUi(
      uiState = uiState,
      onSelectedTabIndexChange = viewModel::onSelectedTabIndexChange,
      onSearchQueryChange = viewModel::onSearchQueryChange,
      onChatClicked = {
        Toast.makeText(context, "Chat feature is not yet implemented", Toast.LENGTH_SHORT).show()
      },
      onTabSelected = navigationActions::navigateTo,
      getUser = viewModel::getUser)
}

@Composable
fun MessagesScreenUi(
    uiState: MessagesUiState,
    onSelectedTabIndexChange: (Int) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onChatClicked: (MessageHistory) -> Unit,
    onTabSelected: (TopLevelDestination) -> Unit,
    getUser: (String) -> User
) {
  Scaffold(
      modifier = Modifier.testTag("messagesScreen"),
      topBar = {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 32.dp, start = 16.dp).testTag("logo"),
            verticalAlignment = Alignment.CenterVertically) {
              Image(
                  painter = painterResource(id = R.drawable.event_logo),
                  contentDescription = "Event Radar Logo",
                  modifier = Modifier.size(width = 186.dp, height = 50.dp))
            }
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelected = onTabSelected,
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = TOP_LEVEL_DESTINATIONS[1],
            modifier = Modifier.testTag("bottomNavMenu"))
      }) {
        Column(modifier = Modifier.padding(it).padding(top = 16.dp)) {
          TabRow(selectedTabIndex = uiState.selectedTabIndex, modifier = Modifier.testTag("tabs")) {
            Tab(
                selected = uiState.selectedTabIndex == 0,
                onClick = { onSelectedTabIndexChange(0) },
                modifier = Modifier.testTag("messagesTab")) {
                  Text(
                      text = "Messages",
                      style =
                          TextStyle(
                              fontSize = 19.sp,
                              lineHeight = 17.sp,
                              fontFamily = FontFamily(Font(R.font.roboto)),
                              fontWeight = FontWeight(500),
                              color = MaterialTheme.colorScheme.onPrimaryContainer,
                              textAlign = TextAlign.Center,
                              letterSpacing = 0.25.sp,
                          ),
                      modifier = Modifier.padding(bottom = 8.dp))
                }
            Tab(
                selected = uiState.selectedTabIndex == 1,
                onClick = { onSelectedTabIndexChange(1) },
                modifier = Modifier.testTag("contactsTab")) {
                  Text(
                      text = "Contacts",
                      style =
                          TextStyle(
                              fontSize = 19.sp,
                              lineHeight = 17.sp,
                              fontFamily = FontFamily(Font(R.font.roboto)),
                              fontWeight = FontWeight(500),
                              color = MaterialTheme.colorScheme.onPrimaryContainer,
                              textAlign = TextAlign.Center,
                              letterSpacing = 0.25.sp,
                          ),
                      modifier = Modifier.padding(bottom = 8.dp))
                }
          }
          if (uiState.selectedTabIndex == 0) {
            MessagesList(
                messageList = uiState.messageList,
                userId = uiState.userId!!,
                searchQuery = uiState.searchQuery,
                onChatClicked = onChatClicked,
                getUser = getUser,
                modifier = Modifier.testTag("messagesList"))
          } else {
            Toast.makeText(
                    LocalContext.current,
                    "Contacts feature is not yet implemented",
                    Toast.LENGTH_SHORT)
                .show()
          }
        }
      }
}

@Composable
fun MessagesList(
    messageList: List<MessageHistory>,
    searchQuery: String,
    userId: String,
    onChatClicked: (MessageHistory) -> Unit,
    getUser: (String) -> User,
    modifier: Modifier = Modifier
) {
  val filteredMessageList = messageList.filter { it.user1 == userId || it.user2 == userId }

  if (filteredMessageList.isEmpty()) {
    Text(
        text = stringResource(R.string.no_message_found_string),
        style =
            TextStyle(
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontFamily = FontFamily(Font(R.font.roboto)),
                fontWeight = FontWeight.Normal,
                color = Color(0xFF49454F),
                letterSpacing = 0.15.sp,
                textAlign = TextAlign.Center),
        modifier = Modifier.fillMaxSize().padding(top = 32.dp).testTag("noMessagesFound"))
  } else {
    LazyColumn(modifier = modifier.padding(top = 16.dp)) {
      items(filteredMessageList) { messageHistory ->
        val otherUser =
            if (userId == messageHistory.user1) messageHistory.user2 else messageHistory.user1
        val currentUserReadLatestMessage =
            if (userId == messageHistory.user1) messageHistory.user1ReadMostRecentMessage
            else messageHistory.user2ReadMostRecentMessage
        val recipient = getUser(otherUser)
        MessagePreviewItem(messageHistory, recipient, currentUserReadLatestMessage, onChatClicked)
        Divider()
      }
    }
  }
}

@Composable
fun MessagePreviewItem(
    messageHistory: MessageHistory,
    recipient: User,
    currentUserReadLatestMessage: Boolean,
    onChatClicked: (MessageHistory) -> Unit,
    modifier: Modifier = Modifier
) {
  val mostRecentMessage = messageHistory.messages.last { it.id == messageHistory.latestMessageId }

  Row(
      modifier =
          modifier
              .fillMaxWidth()
              .clickable { onChatClicked(messageHistory) }
              .padding(vertical = 8.dp)
              .testTag("messagePreviewItem"),
      horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model =
                ImageRequest.Builder(LocalContext.current).data(recipient.profilePicUrl).build(),
            placeholder = painterResource(id = R.drawable.placeholder),
            contentDescription = "Profile picture of ${recipient.firstName} ${recipient.lastName}",
            contentScale = ContentScale.Crop,
            modifier =
                Modifier.padding(start = 16.dp).clip(CircleShape).size(56.dp).testTag("profilePic"))
        Column(
            modifier =
                Modifier.padding(start = 16.dp).fillMaxWidth(.7f).testTag("messageContentColumn")) {
              Text(
                  text = "${recipient.firstName} ${recipient.lastName}",
                  style =
                      TextStyle(
                          fontSize = 16.sp,
                          lineHeight = 24.sp,
                          fontFamily = FontFamily(Font(R.font.roboto)),
                          fontWeight = FontWeight.Bold,
                          color = Color.Black,
                          letterSpacing = 0.5.sp,
                      ),
                  modifier = Modifier.testTag("recipientName"))
              Text(
                  text = mostRecentMessage.content,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  style =
                      TextStyle(
                          fontSize = 14.sp,
                          lineHeight = 20.sp,
                          fontFamily = FontFamily(Font(R.font.roboto)),
                          fontWeight =
                              if (currentUserReadLatestMessage) FontWeight.Normal
                              else FontWeight.Bold,
                          color = Color(0xFF49454F),
                          letterSpacing = 0.25.sp,
                      ),
                  modifier = Modifier.testTag("messageContent"))
            }
        Spacer(modifier = Modifier.weight(1f))

        val today = LocalDateTime.of(LocalDate.now(ZoneId.systemDefault()), LocalTime.MIN)
        val thisYear = today.minusYears(1)

        val pattern =
            when {
              mostRecentMessage.dateTimeSent.isAfter(today) -> "HH:mm"
              mostRecentMessage.dateTimeSent.isBefore(today) and
                  mostRecentMessage.dateTimeSent.isAfter(thisYear) -> "dd/MM"
              else -> "dd/MM/yy"
            }

        Text(
            text = mostRecentMessage.dateTimeSent.format(DateTimeFormatter.ofPattern(pattern)),
            modifier = Modifier.padding(end = 16.dp).testTag("messageTime"))
      }
}

@Preview(showSystemUi = true, showBackground = true)
@ExcludeFromJacocoGeneratedReport
@Composable
fun PreviewMessagesScreen() {
  val messageList =
      listOf(
          MessageHistory(
              user1 = "1",
              user2 = "2",
              user1ReadMostRecentMessage = true,
              user2ReadMostRecentMessage = false,
              messages =
                  mutableListOf(
                      Message(
                          sender = "1",
                          content = "Hello Hello Hello Hello Hello Hello Hello",
                          dateTimeSent = LocalDateTime.parse("2021-08-01T12:00:00"),
                          id = "1")),
              latestMessageId = "1",
          ),
          MessageHistory(
              user1 = "1",
              user2 = "3",
              user1ReadMostRecentMessage = false,
              user2ReadMostRecentMessage = false,
              messages =
                  mutableListOf(
                      Message(
                          sender = "3",
                          content = "Hello Hello Hello Hello Hello Hello Hello Hello",
                          dateTimeSent = LocalDateTime.parse("2024-04-27T12:00:00"),
                          id = "1"),
                      Message(
                          sender = "1",
                          content = "This is the most recent message",
                          dateTimeSent = LocalDateTime.parse("2024-04-29T16:00:00"),
                          id = "2")),
              latestMessageId = "2",
          ))
  MessagesScreenUi(
      uiState =
          MessagesUiState(
              userId = "1",
              messageList =
                  messageList.sortedByDescending {
                    it.messages.find { message -> message.id == it.latestMessageId }?.dateTimeSent
                  }),
      onSelectedTabIndexChange = {},
      onSearchQueryChange = {},
      onChatClicked = {},
      onTabSelected = {},
      getUser = {
        User(
            it,
            "10/10/2003",
            "test@test.com",
            "Test",
            it,
            "1234567890",
            "active",
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            "https://firebasestorage.googleapis.com/v0/b/event-radar-e6a76.appspot.com/o/Profile_Pictures%2FYJP3bYiaGFPqx64CT6kHOpwvXnv1?alt=media&token=5587f942-efc7-4cbf-920c-7f24a76d7ad1",
            "",
            "",
            "johndoe")
      })
}

@Preview(showSystemUi = true, showBackground = true)
@ExcludeFromJacocoGeneratedReport
@Composable
fun PreviewEmptyMessagesList() {
  MessagesList(
      messageList = emptyList(),
      searchQuery = "",
      userId = "1",
      onChatClicked = {},
      getUser = {
        User(
            it,
            "10/10/2003",
            "test@test.com",
            "John",
            "Doe",
            "1234567890",
            "active",
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            "https://firebasestorage.googleapis.com/v0/b/event-radar-e6a76.appspot.com/o/Profile_Pictures%2FYJP3bYiaGFPqx64CT6kHOpwvXnv1?alt=media&token=5587f942-efc7-4cbf-920c-7f24a76d7ad1",
            "",
            "",
            "johndoe")
      },
      modifier = Modifier)
}
