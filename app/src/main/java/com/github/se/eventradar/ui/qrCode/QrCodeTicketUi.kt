package com.github.se.eventradar.ui.qrCode

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberImagePainter
import com.github.se.eventradar.R
import com.github.se.eventradar.ui.BottomNavigationMenu
import com.github.se.eventradar.ui.component.EventCategory
import com.github.se.eventradar.ui.component.EventComponentsStyle
import com.github.se.eventradar.ui.component.EventDate
import com.github.se.eventradar.ui.component.EventDescription
import com.github.se.eventradar.ui.component.EventDistance
import com.github.se.eventradar.ui.component.EventTime
import com.github.se.eventradar.ui.component.EventTitle
import com.github.se.eventradar.ui.component.GoBackButton
import com.github.se.eventradar.ui.component.Logo
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.eventradar.viewmodel.qrCode.ScanTicketQrViewModel

@Composable
fun QrCodeTicketUi(
    viewModel: ScanTicketQrViewModel = hiltViewModel(),
    navigationActions: NavigationActions
) {

  val qrScanUiState = viewModel.uiState.collectAsStateWithLifecycle()

  val context = LocalContext.current

  ConstraintLayout(
      modifier = Modifier.fillMaxSize().testTag("qrCodeScannerScreen"),
  ) {
    val (logo, tabs, bottomNav) = createRefs()
    Logo(
        modifier =
            Modifier.fillMaxWidth()
                .fillMaxWidth()
                .constrainAs(logo) {
                  top.linkTo(parent.top, margin = 32.dp)
                  start.linkTo(parent.start, margin = 16.dp)
                }
                .testTag("logo"),
    )
    TabRow(
        selectedTabIndex = qrScanUiState.value.tabState.ordinal,
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
              onClick = {
                viewModel.changeTabState(ScanTicketQrViewModel.Tab.ScanQr)
              }, // selectedTabIndex = 1
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

    if (qrScanUiState.value.tabState == ScanTicketQrViewModel.Tab.MyEvent) {
      val widthPadding = 34.dp
      val imageHeight = 191.dp

      val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

      val componentStyle =
          EventComponentsStyle(
              MaterialTheme.colorScheme.onSurface,
              MaterialTheme.colorScheme.onSurfaceVariant,
              MaterialTheme.colorScheme.onSurface,
          )

      val (
          image,
          backButton,
          title,
          description,
          distance,
          category,
          date,
          time,
          ticketSold,
          lazyEventDetails) =
          createRefs()

      // TODO uncomment when image are implemented
      // val imagePainter: Painter = rememberImagePainter(eventUiState.eventPhoto)
      val imagePainter: Painter = rememberImagePainter(R.drawable.placeholderbig)

      LazyColumn(
          modifier =
              Modifier.fillMaxSize()
                  .padding(horizontal = 16.dp)
                  .constrainAs(lazyEventDetails) {
                    top.linkTo(tabs.bottom, margin = 0.dp)
                    start.linkTo(parent.start, margin = 0.dp)
                    end.linkTo(parent.end, margin = 0.dp)
                  }
                  .testTag("lazyEventDetails")) {
            item {
              Image(
                  painter = imagePainter,
                  contentDescription = "Event banner image",
                  modifier =
                      Modifier.fillMaxWidth()
                          .height(imageHeight)
                          .constrainAs(image) {
                            top.linkTo(tabs.bottom, margin = 0.dp)
                            start.linkTo(parent.start, margin = 0.dp)
                          }
                          .testTag("eventImage"),
                  contentScale = ContentScale.FillWidth)
            }
            item {
              GoBackButton(
                  modifier =
                      Modifier.wrapContentSize().constrainAs(backButton) {
                        top.linkTo(image.bottom, margin = 8.dp)
                        start.linkTo(image.start, margin = 4.dp)
                      }) {
                    navigationActions.goBack()
                  }
            }
            item {
              EventTitle(
                  modifier =
                      Modifier.constrainAs(title) {
                        top.linkTo(image.bottom, margin = 32.dp)
                        start.linkTo(image.start)
                        end.linkTo(image.end)
                      },
                  eventUiState = uiState.eventUiState,
                  style = componentStyle)
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }
            item {
              EventDescription(
                  modifier =
                      Modifier
                          // .padding(start = widthPadding, end = widthPadding)
                          .constrainAs(description) {
                            top.linkTo(title.bottom, margin = 32.dp)
                            start.linkTo(parent.start, margin = widthPadding)
                          },
                  eventUiState = uiState.eventUiState,
                  componentStyle)
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }
            item {
              EventDistance(
                  modifier =
                      Modifier.constrainAs(distance) {
                        top.linkTo(description.bottom, margin = 32.dp)
                        start.linkTo(parent.start, margin = widthPadding)
                      },
                  eventUiState = uiState.eventUiState,
                  componentStyle)
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }
            item {
              EventDate(
                  modifier =
                      Modifier.constrainAs(date) {
                        top.linkTo(distance.bottom, margin = 32.dp)
                        start.linkTo(parent.start, margin = widthPadding)
                      },
                  eventUiState = uiState.eventUiState,
                  componentStyle)
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }

            item {
              EventTime(
                  modifier =
                      Modifier.constrainAs(time) {
                        top.linkTo(date.bottom, margin = 32.dp)
                        start.linkTo(parent.start, margin = widthPadding)
                      },
                  eventUiState = uiState.eventUiState,
                  componentStyle)
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }
            item {
              EventCategory(
                  modifier =
                      Modifier.constrainAs(category) {
                        top.linkTo(time.bottom, margin = 32.dp)
                        start.linkTo(parent.start, margin = widthPadding)
                      },
                  eventUiState = uiState.eventUiState,
                  componentStyle)
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }

            item {
              Column(
                  modifier =
                      Modifier.constrainAs(ticketSold) {
                        top.linkTo(category.bottom, margin = 32.dp)
                        start.linkTo(parent.start, margin = widthPadding)
                      }) {
                    Text(
                        text = "Tickets Sold",
                        style = componentStyle.fieldTitleStyle,
                        color = componentStyle.fieldTitleColor,
                        modifier = Modifier.testTag("ticketSoldTitle"))
                    Text(
                        text = "${uiState.eventUiState.ticket.purchases} tickets sold",
                        // TO SOLD
                        style = componentStyle.contentStyle,
                        color = componentStyle.contentColor,
                        modifier = Modifier.testTag("ticketSoldContent"))
                  }
            }
          }
    } else {
      Toast.makeText(context, "Scan Ticket Not implemented yet", Toast.LENGTH_SHORT).show()
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

// @Preview(showBackground = true)
// @Composable
// fun PreviewQrCodeTicketUi() {
//  // Create a mock NavigationActions to pass into the function
//  val userRepository = MockUserRepository()
//  (userRepository as MockUserRepository).updateCurrentUserId("user1")
//  val eventRepository = MockEventRepository()
//  val qrCodeAnalyser = QrCodeAnalyser()
//  val viewModel = ScanTicketQrViewModel(userRepository, eventRepository, qrCodeAnalyser, "1")
//  QrCodeTicketUi(viewModel, NavigationActions(rememberNavController()))
// }
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
