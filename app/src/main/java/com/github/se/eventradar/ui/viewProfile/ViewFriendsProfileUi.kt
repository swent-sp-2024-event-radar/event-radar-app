package com.github.se.eventradar.ui.viewProfile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.se.eventradar.R
import com.github.se.eventradar.ui.component.GoBackButton
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.viewmodel.ViewFriendsProfileViewModel

@Composable
fun ViewFriendsProfileUi(
    viewModel: ViewFriendsProfileViewModel,
    navigationActions: NavigationActions
) {
  val uiState by viewModel.uiState.collectAsState()
  Scaffold(
      topBar = {
          //row and text profile Information
        GoBackButton(modifier = Modifier.testTag("GoBackButton"), { navigationActions.goBack() })
      },
      content = {
        Column(modifier = Modifier.padding(it).padding(top = 16.dp),
            verticalArrangement = Arrangement.Center, // Vertically center the content
            horizontalAlignment = Alignment.CenterHorizontally){ // Horizontally center the content) {
          AsyncImage(
              model =
                  ImageRequest.Builder(LocalContext.current)
                      .data(uiState.friendProfilePicLink)
                      .build(),
              placeholder = painterResource(id = R.drawable.placeholder),
              contentDescription = "Profile picture of ${uiState.friendName}",
              contentScale = ContentScale.Crop,
              modifier = Modifier.padding(start = 16.dp).size(56.dp).testTag("friendProfilePic"))
        }
          Text(
              "@${uiState.friendName}",
              modifier = Modifier.testTag("username"),
              fontSize = 36.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.36.sp,
              fontFamily = FontFamily.Default,
              color = MaterialTheme.colorScheme.onBackground)
          Text(
              "@${uiState.friendUserName}",
              modifier = Modifier.testTag("username"),
              fontSize = 25.sp,
              letterSpacing = 0.36.sp,
              fontFamily = FontFamily.Default,
              color = MaterialTheme.colorScheme.onBackground)
          Text(
              "Bio",
              modifier = Modifier.testTag("username"),
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.36.sp,
              fontFamily = FontFamily.Default,
              color = MaterialTheme.colorScheme.onBackground)
          Text(
              "@${uiState.bio}",
              modifier = Modifier.testTag("username"),
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.36.sp,
              fontFamily = FontFamily.Default,
              color = MaterialTheme.colorScheme.onBackground)
      })
}
