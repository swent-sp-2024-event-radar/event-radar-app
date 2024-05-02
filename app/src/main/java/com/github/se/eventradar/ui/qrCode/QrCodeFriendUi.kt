package com.github.se.eventradar.ui.qrCode

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.se.eventradar.R
import com.github.se.eventradar.ui.BottomNavigationMenu
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.eventradar.ui.navigation.TopLevelDestination
import com.github.se.eventradar.viewmodel.MyQrCodeViewModel
import com.github.se.eventradar.viewmodel.qrCode.QrCodeFriendViewModel

@Composable
fun QrCodeScreen(
    myQrCodeViewModel: MyQrCodeViewModel = hiltViewModel(),
    viewModel: QrCodeFriendViewModel = hiltViewModel(),
    navigationActions: NavigationActions
) {
  val navigateState by viewModel.action.collectAsState()
  val activeTabState by viewModel.tabState.collectAsState()

  // React to changes in navigation state
  LaunchedEffect(navigateState) {
    when (navigateState) {
      QrCodeFriendViewModel.Action.NavigateToNextScreen -> {
        //          navigationActions.navigate({Route.MESSAGE})
        navigationActions.navigateTo(
            TopLevelDestination( // TODO
                route = Route.MESSAGE,
                icon = R.drawable.chat_bubble,
                textId = R.string.message_chats,
            )) // Adjust according to your actual navigation logic
        viewModel.resetNavigationEvent() // Reset the navigation event in the ViewModel to prevent
        // repeated navigations
      }
      else -> Unit // Do nothing if the state is None or any other non-navigational state
    }
  }
  //  var selectedTabIndex by remember { mutableIntStateOf(0) }
  val context = LocalContext.current

  ConstraintLayout(
      modifier = Modifier.fillMaxSize().testTag("qrCodeScannerScreen"),
  ) {
    val (logo, tabs, myqrcode, bottomNav) = createRefs()
    Row(
        modifier =
            Modifier.fillMaxWidth()
                .fillMaxWidth()
                .constrainAs(logo) {
                  top.linkTo(parent.top, margin = 32.dp)
                  start.linkTo(parent.start, margin = 16.dp)
                }
                .testTag("logo"),
        verticalAlignment = Alignment.CenterVertically) {
          Image(
              painter = painterResource(id = R.drawable.event_logo),
              contentDescription = "Event Radar Logo",
              modifier = Modifier.size(width = 186.dp, height = 50.dp))
        }
    TabRow(
        //
        selectedTabIndex = activeTabState.ordinal,
        modifier =
            Modifier.fillMaxWidth()
                .padding(top = 8.dp)
                .constrainAs(tabs) {
                  top.linkTo(logo.bottom, margin = 16.dp)
                  start.linkTo(parent.start)
                  end.linkTo(parent.end)
                }
                .testTag("tabs"),
        contentColor = MaterialTheme.colorScheme.primary) {
          Tab(
              selected = activeTabState == QrCodeFriendViewModel.TAB.MyQR,
              onClick = { viewModel.changeTabState(QrCodeFriendViewModel.TAB.MyQR) },
              modifier = Modifier.testTag("My QR Code"),
          ) {
            Text(
                text = "My QR Code",
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
              selected = activeTabState == QrCodeFriendViewModel.TAB.ScanQR,
              onClick = {
                viewModel.changeTabState(QrCodeFriendViewModel.TAB.ScanQR)
              }, // selectedTabIndex = 1
              modifier = Modifier.testTag("Scan QR Code")) {
                Text(
                    text = "Scan QR Code",
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

    if (activeTabState == QrCodeFriendViewModel.TAB.MyQR) {

      MyQrCodeComposable(
          myQrCodeViewModel,
          modifier =
              Modifier.testTag("myQrCodeScreen").constrainAs(myqrcode) {
                top.linkTo(tabs.bottom, margin = 32.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
              })
    } else {
      Column(modifier = Modifier.testTag("QrScanner")) {
        QrCodeCamera().QrCodeScanner(analyser = viewModel.qrCodeAnalyser)
      }
    }
    BottomNavigationMenu(
        onTabSelected = { tab -> navigationActions.navigateTo(tab) },
        tabList = TOP_LEVEL_DESTINATIONS,
        selectedItem = TOP_LEVEL_DESTINATIONS[0],
        modifier =
            Modifier.testTag("bottomNavMenu").constrainAs(bottomNav) {
              bottom.linkTo(parent.bottom)
              start.linkTo(parent.start)
              end.linkTo(parent.end)
            })
  }
}

@Composable
fun MyQrCodeComposable(viewModel: MyQrCodeViewModel, modifier: Modifier) {
  val uiState by viewModel.uiState.collectAsState()
  Column(
      modifier = modifier,
      verticalArrangement = Arrangement.Center, // Vertically center the content
      horizontalAlignment = Alignment.CenterHorizontally // Horizontally center the content
      ) {
        Text(
            "@username {}", // uiState.qrCode
            modifier = Modifier.testTag("username"),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.36.sp,
            fontFamily = FontFamily.Default,
            color = MaterialTheme.colorScheme.onBackground // Set the color to black,
            )
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://cdn.britannica.com/17/155017-050-9AC96FC8/Example-QR-code.jpg") //uiState.qrCode (only works for jpg)
                .crossfade(true)
                .build(),
            //error = painterResource(R.drawable.qr_code), // should be a error indicative
            //placeholder = painterResource(R.drawable.placeholder), // should be loading image
            contentDescription = stringResource(R.string.my_qr_code),
            modifier = Modifier.size(width = 300.dp, height = 300.dp).testTag("QrCodeImage"))
      }
}

// private val dummyQrCodeScanned: (String) -> Unit = { qrCode ->
//  Log.d("QRCodeScanner", "QR Code Scanned: $qrCode")
//  // You can perform any additional logic here for testing
// }

// @androidx.compose.ui.tooling.preview.Preview
// @Composable
// fun QrcodeScanTest() {
//    val mockEventRepo = MockEventRepository()
//    val mockUserRepo = MockUserRepository()
//    val mockQrCodeAnalyser = QrCodeAnalyser()
//  QrCodeScreen( MyQrCodeViewModel(mockUserRepo), QrCodeFriendViewModel(mockUserRepo,
// mockQrCodeAnalyser), NavigationActions(rememberNavController()))
// }
