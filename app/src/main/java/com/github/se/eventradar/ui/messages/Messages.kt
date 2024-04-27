package com.github.se.eventradar.ui.messages

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import coil.compose.rememberImagePainter
import com.github.se.eventradar.R
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.message.Message
import com.github.se.eventradar.model.message.MessageHistory
import com.github.se.eventradar.ui.BottomNavigationMenu
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.eventradar.ui.navigation.TopLevelDestination
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun MessagesScreen(
    viewModel: MessagesViewModel = hiltViewModel(),
    userId: String,
    navigationActions: NavigationActions
) {
  val uiState by viewModel.uiState.collectAsState()

  viewModel.getMessages(userId)

  MessagesScreenUi(
      userId = userId,
      uiState = uiState,
      onSelectedTabIndexChange = viewModel::onSelectedTabIndexChange,
      onSearchQueryChange = viewModel::onSearchQueryChange,
      onChatClicked = { navigationActions.navController.navigate("message/${it.id}") },
      onTabSelected = navigationActions::navigateTo,
      getUser = viewModel::getUser)
}

@Composable
fun MessagesScreenUi(
    userId: String,
    uiState: MessagesUiState,
    onSelectedTabIndexChange: (Int) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onChatClicked: (MessageHistory) -> Unit,
    onTabSelected: (TopLevelDestination) -> Unit,
    getUser: (String) -> User
) {
  Scaffold(
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
            selectedItem = TOP_LEVEL_DESTINATIONS[2],
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
                userId = userId,
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
  LazyColumn(modifier = modifier.padding(top = 16.dp)) {
    items(messageList) { messageHistory ->
      val toUser =
          getUser(
              if (messageHistory.user1 != userId) messageHistory.user1 else messageHistory.user2)
      MessagePreviewItem(messageHistory, userId, toUser, onChatClicked)
      Divider()
    }
  }
}

@Composable
fun MessagePreviewItem(
    messageHistory: MessageHistory,
    userId: String,
    toUser: User,
    onChatClicked: (MessageHistory) -> Unit,
    modifier: Modifier = Modifier
) {
  val mostRecentMessage = messageHistory.messages.last { it.id == messageHistory.latestMessageId }
  
  Row(modifier = modifier.clickable { onChatClicked(messageHistory) }, horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
    Image(
        painter = rememberImagePainter(data = Uri.parse(toUser.profilePicUrl)),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(56.dp).padding(start = 16.dp).clip(CircleShape))
    Column(modifier = Modifier.padding(start = 16.dp)) {
      Text(
          text = "${toUser.firstName} ${toUser.lastName}",
          style =
              TextStyle(
                  fontSize = 16.sp,
                  lineHeight = 24.sp,
                  fontFamily = FontFamily(Font(R.font.roboto)),
                  fontWeight = FontWeight.Bold,
                  color = Color.Black,
                  letterSpacing = 0.5.sp,
              ))
      Text(
          text = mostRecentMessage.content,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          style =
              TextStyle(
                  fontSize = 14.sp,
                  lineHeight = 20.sp,
                  fontFamily = FontFamily(Font(R.font.roboto)),
                  fontWeight =
                      if (mostRecentMessage.isRead) FontWeight.Normal else FontWeight.Bold,
                  color = Color(0xFF49454F),
                  letterSpacing = 0.25.sp,
              ))
    }
    Spacer(modifier = Modifier.weight(1f))
    
    val today = LocalDateTime.of(LocalDate.now(ZoneId.systemDefault()), LocalTime.MIN)
    val thisYear = today.minusYears(1)
    
    val pattern = when {
      mostRecentMessage.dateTimeSent.isAfter(today) -> "HH:mm"
      mostRecentMessage.dateTimeSent.isBefore(today) and mostRecentMessage.dateTimeSent.isAfter(thisYear) -> "MM/dd"
      else -> "MM/dd/yyyy"
    }
    
    Text(
        text = mostRecentMessage.dateTimeSent.format(DateTimeFormatter.ofPattern(pattern)),
        modifier = Modifier.padding(end = 16.dp))
  }
}

@Preview
@Composable
fun PreviewMessagesScreen() {
  MessagesScreenUi(
      userId = "1",
      uiState =
          MessagesUiState(
              messageList =
                  listOf(
                      MessageHistory(
                          user1 = "1",
                          user2 = "2",
                          messages =
                              mutableListOf(
                                  Message(
                                      sender = "1",
                                      content = "Hello Hello Hello Hello Hello",
                                      dateTimeSent = LocalDateTime.parse("2021-08-01T12:00:00"),
                                      isRead = false,
                                      id = "1")),
                          latestMessageId = "1",
                      ))),
      onSelectedTabIndexChange = {},
      onSearchQueryChange = {},
      onChatClicked = {},
      onTabSelected = {},
      getUser = {
        User(
            "2",
            0,
            "test@test.com",
            "John",
            "Doe",
            "1234567890",
            "active",
            emptyList(),
            emptyList(),
          "content://com.google.android.apps.docs.storage/document/acc%3D1%3Bdoc%3Dencoded%3D_UfMfUb7G-_gMA2naQlf9EvwC7BF37dTn3wqEbCsPCFqL25u15za15OI19GK4g%3D",
          "",
            "johndoe")
      })
}
