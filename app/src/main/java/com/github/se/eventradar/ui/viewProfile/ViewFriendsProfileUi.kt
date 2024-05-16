package com.github.se.eventradar.ui.viewProfile

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.github.se.eventradar.ExcludeFromJacocoGeneratedReport
import com.github.se.eventradar.R
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.ui.BottomNavigationMenu
import com.github.se.eventradar.ui.component.GoBackButton
import com.github.se.eventradar.ui.component.NameText
import com.github.se.eventradar.ui.component.StandardProfileInformationText
import com.github.se.eventradar.ui.component.UserProfileImage
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.eventradar.ui.navigation.getTopLevelDestination
import com.github.se.eventradar.viewmodel.ViewFriendsProfileViewModel

@Composable
fun ViewFriendsProfileUi(
    viewModel: ViewFriendsProfileViewModel,
    navigationActions: NavigationActions
) {
  val uiState by viewModel.uiState.collectAsState()
  val context = LocalContext.current
  Scaffold(
      modifier = Modifier.testTag("viewFriendsProfileScreen"),
      topBar = {
        // row and text profile Information
        Row(
            modifier = Modifier.padding(vertical = 32.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {
              GoBackButton(
                  modifier = Modifier.testTag("goBackButton"), { navigationActions.goBack() })
              Text(
                  "Profile Information",
                  modifier = Modifier.testTag("username"),
                  fontSize = 22.sp,
                  letterSpacing = 0.36.sp,
                  fontFamily = FontFamily.Default,
                  color = MaterialTheme.colorScheme.onBackground)
            }
      },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelected = navigationActions::navigateTo,
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = getTopLevelDestination(Route.MESSAGE),
            modifier = Modifier.testTag("bottomNavMenu"))
      },
      floatingActionButton = {
        FloatingActionButton(
            onClick = {
              // To Be Implemented
              Toast.makeText(context, "Chat not implemented yet", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.padding(bottom = 16.dp, end = 16.dp).testTag("chatButton"),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ) {
          Icon(
              painter = painterResource(id = R.drawable.chat_bubble),
              contentDescription = "Chat Button",
              modifier = Modifier.size(32.dp),
              tint = MaterialTheme.colorScheme.onPrimaryContainer,
          )
        }
      },
      content = {
        Column(
            modifier =
                Modifier.padding(it)
                    .padding(horizontal = 41.dp, vertical = 20.dp)
                    .fillMaxWidth()
                    .testTag("centeredViewProfileColumn"),
            verticalArrangement = Arrangement.Center, // Vertically center the content
            horizontalAlignment =
                Alignment.CenterHorizontally) { // Horizontally center the content) {
              UserProfileImage(
                  uiState.friendProfilePicLink,
                  uiState.friendName,
                  Modifier.size(150.dp)
                      .testTag("friendProfilePic")
                      .padding(10.dp)
                      .clip(RoundedCornerShape(10.dp)))
              NameText(name = uiState.friendName, modifier = Modifier.testTag("friendName"))
              StandardProfileInformationText(
                  text = "@${uiState.friendUserName}",
                  modifier = Modifier.testTag("friendUserName"))
              Column(
                  modifier = Modifier.fillMaxWidth().testTag("leftAlignedViewProfileColumn"),
                  horizontalAlignment = Alignment.Start) {
                    StandardProfileInformationText(
                        text = "Bio", modifier = Modifier.testTag("bioLabelText"))
                    StandardProfileInformationText(
                        text = uiState.bio, modifier = Modifier.testTag("bioInfoText"))
                  }
            }
      })
}

@Preview(showSystemUi = true, showBackground = true)
@ExcludeFromJacocoGeneratedReport
@Composable
fun PreviewEmptyViewFriendsProfile() {
  val userRepository = MockUserRepository()
  val viewModel = ViewFriendsProfileViewModel(userRepository, "friendId")
  ViewFriendsProfileUi(
      viewModel = viewModel, navigationActions = NavigationActions(rememberNavController()))
}
