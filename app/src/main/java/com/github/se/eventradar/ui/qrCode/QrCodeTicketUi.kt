package com.github.se.eventradar.ui.qrCode

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.github.se.eventradar.ExcludeFromJacocoGeneratedReport
import com.github.se.eventradar.R
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.repository.event.MockEventRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.ui.BottomNavigationMenu
import com.github.se.eventradar.ui.component.EventCategory
import com.github.se.eventradar.ui.component.EventComponentsStyle
import com.github.se.eventradar.ui.component.EventDate
import com.github.se.eventradar.ui.component.EventDescription
import com.github.se.eventradar.ui.component.EventLocation
import com.github.se.eventradar.ui.component.EventOrganisers
import com.github.se.eventradar.ui.component.EventTime
import com.github.se.eventradar.ui.component.EventTitle
import com.github.se.eventradar.ui.component.GoBackButton
import com.github.se.eventradar.ui.component.Logo
import com.github.se.eventradar.ui.component.TicketsSold
import com.github.se.eventradar.ui.messages.FriendPreviewItem
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.eventradar.viewmodel.qrCode.QrCodeAnalyser
import com.github.se.eventradar.viewmodel.qrCode.ScanTicketQrViewModel

@Composable
fun QrCodeTicketUi(
    viewModel: ScanTicketQrViewModel = hiltViewModel(),
    navigationActions: NavigationActions
) {

  val qrScanUiState = viewModel.uiState.collectAsStateWithLifecycle()

  ConstraintLayout(
      modifier = Modifier.fillMaxSize().testTag("qrCodeScannerScreen"),
  ) {
    val (backButton, tabs, title, bottomNav) = createRefs()
    GoBackButton(modifier = Modifier
          .wrapContentSize()
          .constrainAs(backButton) {
              top.linkTo(parent.top, margin = 32.dp)
              start.linkTo(parent.start, margin = 16.dp)
          }
    ) { navigationActions.goBack() }
    TabRow(
        selectedTabIndex = qrScanUiState.value.tabState.ordinal,
        modifier =
            Modifier.fillMaxWidth()
                .padding(top = 8.dp)
                .constrainAs(tabs) {
                  top.linkTo(backButton.bottom, margin = 16.dp)
                  start.linkTo(parent.start)
                  end.linkTo(parent.end)
                }
                .testTag("tabs"),
        contentColor = MaterialTheme.colorScheme.primary) {
          Tab(
              selected = qrScanUiState.value.tabState == ScanTicketQrViewModel.Tab.MyEvent,
              onClick = { viewModel.changeTabState(ScanTicketQrViewModel.Tab.MyEvent) },
              modifier = Modifier.testTag("My Event"),
          ) {
            Text(
                text = "My Event",
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
              selected = qrScanUiState.value.tabState == ScanTicketQrViewModel.Tab.ScanQr,
              onClick = { viewModel.changeTabState(ScanTicketQrViewModel.Tab.ScanQr) },
              modifier = Modifier.testTag("Scan QR Code")) {
                Text(
                    text = "Scan Ticket",
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

      val componentStyle =
          EventComponentsStyle(
              MaterialTheme.colorScheme.onSurface,
              MaterialTheme.colorScheme.onSurfaceVariant,
              MaterialTheme.colorScheme.onSurface,
          )

    if (qrScanUiState.value.tabState == ScanTicketQrViewModel.Tab.MyEvent) {
      val imageHeight = 191.dp

      val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

      val (lazyEventDetails) = createRefs()

        val imagePainter =
        if (qrScanUiState.value.eventUiState.eventPhoto == "") {
            painterResource(id = R.drawable.placeholder)
        } else {
            rememberAsyncImagePainter(qrScanUiState.value.eventUiState.eventPhoto)
        }

      Column(
          modifier =
              Modifier.fillMaxSize()
                  .verticalScroll(rememberScrollState())
                  .padding(horizontal = 16.dp)
                  .constrainAs(lazyEventDetails) {
                    top.linkTo(tabs.bottom, margin = 0.dp)
                    start.linkTo(parent.start, margin = 0.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                  }
                  .testTag("lazyEventDetails")) {

              Image(
                  painter = imagePainter,
                  contentDescription = "Event banner image",
                  modifier = Modifier.fillMaxWidth().height(imageHeight).testTag("eventImage"),
                  contentScale = ContentScale.FillWidth)

            Spacer(modifier = Modifier.height(8.dp))

              EventTitle(
                  modifier =
                      Modifier.fillMaxWidth()
                          .wrapContentWidth(
                              Alignment.CenterHorizontally), // .(Alignment.CenterHorizontally),
                  eventUiState = qrScanUiState.value.eventUiState,
                  style = componentStyle)



              TicketsSold(
                  modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally),
                  eventUiState = uiState.eventUiState,
                  style = componentStyle)


            Spacer(modifier = Modifier.height(8.dp))

              EventDescription(modifier = Modifier, qrScanUiState.value.eventUiState, componentStyle)


            Spacer(modifier = Modifier.height(16.dp))

              Row(modifier = Modifier.fillMaxWidth()) {
                EventLocation(modifier = Modifier.weight(2f), uiState.eventUiState, componentStyle)
                EventDate(modifier = Modifier.weight(1f), uiState.eventUiState, componentStyle)
              }

            Spacer(modifier = Modifier.height(8.dp))


              Row(modifier = Modifier.fillMaxWidth()) {
                EventCategory(modifier = Modifier.weight(2f), uiState.eventUiState, componentStyle)
                EventTime(modifier = Modifier.weight(1f), uiState.eventUiState, componentStyle)
              }


            Spacer(modifier = Modifier.height(8.dp))


              EventOrganisers(
                  modifier = Modifier, qrScanUiState.value.eventUiState, componentStyle)



                OrganisersList(
                    organisersList = qrScanUiState.value.eventUiState.organisersList,
                    onOrganiserClicked = { navigationActions.navController.navigate("${Route.PROFILE}/${it.userId}") },
                    modifier = Modifier.fillMaxWidth())

          }
    } else {
        EventTitle(
            modifier =
            Modifier.fillMaxWidth()
                .wrapContentWidth(
                    Alignment.CenterHorizontally)
                .constrainAs(title) {
                    top.linkTo(tabs.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            eventUiState = qrScanUiState.value.eventUiState,
            style = componentStyle)

      when (qrScanUiState.value.action) {
        ScanTicketQrViewModel.Action.ScanTicket -> {
          Column(modifier = Modifier.testTag("QrScanner")) {
            QrCodeScanner(viewModel.qrCodeAnalyser)
          }
        }
        ScanTicketQrViewModel.Action.ApproveEntry -> {
          println("ApprovedBox should now be displayed")
          EntryDialog(0, viewModel)
        }
        ScanTicketQrViewModel.Action.DenyEntry -> {
          EntryDialog(1, viewModel)
        }
        ScanTicketQrViewModel.Action.FirebaseUpdateError,
        ScanTicketQrViewModel.Action.FirebaseFetchError,
        ScanTicketQrViewModel.Action.AnalyserError -> {
          EntryDialog(2, viewModel)
        }
      }
    }
    BottomNavigationMenu(
        onTabSelected = { tab ->
          navigationActions.navigateTo(tab)
          viewModel.resetConditions()
        },
        tabList = TOP_LEVEL_DESTINATIONS,
        selectedItem = TOP_LEVEL_DESTINATIONS[3],
        modifier =
            Modifier.testTag("bottomNavMenu").constrainAs(bottomNav) {
              bottom.linkTo(parent.bottom)
              start.linkTo(parent.start)
              end.linkTo(parent.end)
            })
  }
}

@Composable
fun EntryDialog(edr: Int, viewModel: ScanTicketQrViewModel) {
  Dialog(onDismissRequest = { viewModel.changeAction(ScanTicketQrViewModel.Action.ScanTicket) }) {
    val boxColor =
        when (edr) {
          0 -> Color.Green
          1 -> Color.Red
          else -> Color.Yellow
        }

    Box(
        modifier =
            Modifier.size(400.dp) // Adjust the size here to make it larger
                .background(boxColor, RoundedCornerShape(8.dp))
                .padding(20.dp)
                .semantics {
                  testTag =
                      when (boxColor) {
                        Color.Green -> "ApprovedBox"
                        Color.Red -> "DeniedBox"
                        else -> "ErrorBox"
                      }
                },
        // Aligning content to the top right corner
    ) {
      Column(
          modifier = Modifier.fillMaxSize().padding(vertical = 16.dp),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally) {
            val textToShow =
                when (edr) {
                  0 -> "Entry Approved"
                  1 -> "Entry Denied"
                  else -> "Error, Please Retry"
                }
            Text(
                text = textToShow,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp),
                modifier =
                    Modifier.semantics {
                      testTag =
                          when (edr) {
                            0 -> "EntryApprovedText"
                            1 -> "EntryDeniedText"
                            else -> "ErrorText"
                          }
                    })
          }
      Box(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
        IconButton(
            onClick = { viewModel.changeAction(ScanTicketQrViewModel.Action.ScanTicket) },
            modifier =
                Modifier.semantics { testTag = "closeButton" } // Adding testTag to the IconButton
            ) {
              Icon(Icons.Default.Close, contentDescription = "Close")
            }
      }
    }
  }
}

@Composable
fun OrganisersList(
    organisersList: List<User>,
    onOrganiserClicked: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    if (organisersList.isEmpty()) {
        Text(
            text = stringResource(R.string.no_other_organisers),
            style =
            TextStyle(
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontFamily = FontFamily(Font(R.font.roboto)),
                fontWeight = FontWeight.Normal,
                color = Color(0xFF49454F),
                letterSpacing = 0.15.sp,
                textAlign = TextAlign.Center),
            modifier = Modifier.fillMaxSize().padding(top = 32.dp).testTag("noOrganisers"))
    } else {
        Column(modifier = modifier.padding(top = 16.dp)) {
            organisersList.forEach { organiser ->
                FriendPreviewItem(organiser, onOrganiserClicked)
                Divider()
            }
        }
    }
}

@ExcludeFromJacocoGeneratedReport
@Preview(showBackground = true)
@Composable
fun PreviewQrCodeTicketUi() {
  // Create a mock NavigationActions to pass into the function
  val userRepository = MockUserRepository()
  userRepository.updateCurrentUserId("user1")
  val eventRepository = MockEventRepository()
  val qrCodeAnalyser = QrCodeAnalyser()
  val viewModel = ScanTicketQrViewModel(userRepository, eventRepository, qrCodeAnalyser, "1")
  QrCodeTicketUi(viewModel, NavigationActions(rememberNavController()))
}

//
// @Preview(showBackground = true)
// @Composable
// fun PreviewQrCodeTicketGranted() {
//  // Create a mock NavigationActions to pass into the function
//  val userRepository = MockUserRepository()
//  (userRepository as MockUserRepository).updateCurrentUserId("user1")
//  val eventRepository = MockEventRepository()
//  val qrCodeAnalyser = QrCodeAnalyser()
//  val viewModel = ScanTicketQrViewModel(userRepository, eventRepository, qrCodeAnalyser, "1")
//  viewModel.changeTabState(ScanTicketQrViewModel.Tab.ScanQr)
//  viewModel.changeAction(ScanTicketQrViewModel.Action.ApproveEntry)
//  QrCodeTicketUi(viewModel, NavigationActions(rememberNavController()))
// }
//// s
// @Preview(showBackground = true)
// @Composable
// fun PreviewQrCodeTicketDeny() {
//  // Create a mock NavigationActions to pass into the function
//  val userRepository = MockUserRepository()
//  (userRepository as MockUserRepository).updateCurrentUserId("user1")
//  val eventRepository = MockEventRepository()
//  val qrCodeAnalyser = QrCodeAnalyser()
//  val viewModel = ScanTicketQrViewModel(userRepository, eventRepository, qrCodeAnalyser)
//  viewModel.changeAction(ScanTicketQrViewModel.Action.DenyEntry)
//  QrCodeTicketUi(viewModel, NavigationActions(rememberNavController()))
// }
//
// @Preview(showBackground = true)
// @Composable
// fun PreviewQrCodeTicketError() {
//  // Create a mock NavigationActions to pass into the function
//  val userRepository = MockUserRepository()
//  (userRepository as MockUserRepository).updateCurrentUserId("user1")
//  val eventRepository = MockEventRepository()
//  val qrCodeAnalyser = QrCodeAnalyser()
//  val viewModel = ScanTicketQrViewModel(userRepository, eventRepository, qrCodeAnalyser)
//  viewModel.changeAction(ScanTicketQrViewModel.Action.FirebaseUpdateError)
//  QrCodeTicketUi(viewModel, NavigationActions(rememberNavController()))
// }
