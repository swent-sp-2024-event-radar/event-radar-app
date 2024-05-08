package com.github.se.eventradar.ui.chat

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.message.Message
import com.github.se.eventradar.model.message.MessageHistory
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import com.github.se.eventradar.ui.navigation.TopLevelDestination
import com.github.se.eventradar.viewmodel.ChatUiState
import com.github.se.eventradar.viewmodel.ChatViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.github.se.eventradar.ExcludeFromJacocoGeneratedReport
import com.github.se.eventradar.R
import com.github.se.eventradar.model.repository.event.MockEventRepository
import com.github.se.eventradar.model.repository.message.MockMessageRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.ui.home.HomeScreen
import com.github.se.eventradar.viewmodel.EventsOverviewViewModel
import java.util.Locale

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    navigationActions: NavigationActions
) {
    val uiState by viewModel.uiState.collectAsState()

    // TO DO: Implement get messages between two users in VM
    //    viewModel.getMessages(senderId, opponentId)

    // TO DO: Implement changed function results
    ChatScreenUi(
        uiState = uiState,
        viewModel = viewModel,
        navigationActions = navigationActions,
//        onSelectedTabIndexChange = viewModel::onSelectedTabIndexChange,
//        onSearchQueryChange = viewModel::onSearchQueryChange,
//        onChatClicked = {
//            Toast.makeText(context, "Chat feature is not yet implemented", Toast.LENGTH_SHORT).show()
//        },
//        onTabSelected = navigationActions::navigateTo,
//        getUser = viewModel::getUser
    )
}

@Composable
fun ChatScreenUi(
    uiState: ChatUiState,
    viewModel: ChatViewModel = hiltViewModel(),
    navigationActions: NavigationActions,
//    onSelectedTabIndexChange: (Int) -> Unit,
//    onSearchQueryChange: (String) -> Unit,
//    onChatClicked: (MessageHistory) -> Unit,
//    onTabSelected: (TopLevelDestination) -> Unit,
//    getUser: (String) -> User
) {
    val messages = uiState.messageHistory.messages

    // TO DO: Implement load opponent in VM
    LaunchedEffect(key1 = Unit) {
//        viewModel.loadOpponentProfileFromFirebase(uiState.opponentId!!)
    }
    val opponentName = uiState.opponentProfile.firstName
    val opponentSurname = uiState.opponentProfile.lastName
    val opponentPictureUrl = uiState.opponentProfile.profilePicUrl

    val scrollState = rememberLazyListState(initialFirstVisibleItemIndex = messages.size)
    val messagesLoadedFirstTime = uiState.messagesLoadedFirstTime
    val messageInserted = uiState.messageInserted
    LaunchedEffect(key1 = messagesLoadedFirstTime, messages, messageInserted) {
        if (messages.isNotEmpty()) {
            scrollState.scrollToItem(
                index = messages.size - 1
            )
        }
    }

    val imePaddingValues = PaddingValues()
    val imeBottomPadding = imePaddingValues.calculateBottomPadding().value.toInt()
    LaunchedEffect(key1 = imeBottomPadding) {
        if (messages.isNotEmpty()) {
            scrollState.scrollToItem(
                index = messages.size - 1
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .focusable()
            .wrapContentHeight()
            .imePadding()
    ) {
        val context = LocalContext.current // only needed until view profile is implemented

        ChatAppBar(
            title = "$opponentName $opponentSurname",
            pictureUrl = opponentPictureUrl,
            onUserNameClick = {
                Toast.makeText(context, "User Profile Display to be implemented", Toast.LENGTH_SHORT).show()
            },
            onBackArrowClick = { navigationActions.navController.navigate(Route.MESSAGE) },
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = scrollState
        ) {
            items(messages) { message ->
                val sdf = remember {
                    java.text.SimpleDateFormat("hh:mm", Locale.ROOT)
                }

                when (message.sender == uiState.opponentId){
                    true -> {
                        ReceivedMessageRow(
                            text = message.content,
                            messageTime = sdf.format(message.dateTimeSent),
                        )
                    }
                    false ->{
                        SentMessageRow(
                            text = message.content,
                            messageTime = sdf.format(message.dateTimeSent),
                            messageRead = true,
                        )
                    }
                }
            }
        }
        ChatInput(
            uiState = uiState,
            onMessageChange = { viewModel.onMessageBarInputChange(it) },
//            onMessageSend = { viewModel.onMessageSend() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatAppBar(
    title: String = "Title",
    pictureUrl: String? = null,
    onUserNameClick: (() -> Unit)? = null,
    onBackArrowClick: (() -> Unit)? = null,
) {
    SmallTopAppBar(
        modifier = Modifier.statusBarsPadding(),
        title = {
            Row {
                Surface(
                    modifier = Modifier.size(50.dp),
                    shape = CircleShape,
                ) {
                    if (pictureUrl != null) {
                        Image(
                            // TO DO: Insert image from database, for now it's a person icon
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(1f))
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(1f))
                    }
                }
                Column(
                    modifier = Modifier
                        .clickable {
                            onUserNameClick?.invoke()
                        },
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = { onBackArrowClick?.invoke() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Localized description"
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInput(
    modifier: Modifier = Modifier,
    uiState: ChatUiState,
    onMessageChange: (String) -> Unit,
//    onMessageSend: () -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        TextField(
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraLarge)
                .weight(1f)
                .focusable(true),
            value = uiState.messageBarInput,
            onValueChange = { onMessageChange(it) },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            placeholder = {
                Text(text = stringResource(R.string.message_bar_placeholder))
            },
            trailingIcon = {
                Row() {
                    IconButton(
//                        onClick = { onMessageSend() }
                        onClick = { null }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send")
                    }
                    IconButton(
                        onClick = {
                            Toast.makeText(
                                context,
                                "Insert image not available yet",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.photo_camera),
                            contentDescription = "Camera")
                    }
                }

            }

        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@ExcludeFromJacocoGeneratedReport
@Composable
fun ChatScreenPreview() {
    val mockMessageRepo = MockMessageRepository()
    val mockUserRepo = MockUserRepository()
    ChatScreen(
        ChatViewModel(mockMessageRepo, mockUserRepo),
        NavigationActions(rememberNavController()))
}
