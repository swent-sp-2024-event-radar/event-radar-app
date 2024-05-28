package com.github.se.eventradar.qrCode

import android.Manifest
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventTicket
import com.github.se.eventradar.model.repository.event.IEventRepository
import com.github.se.eventradar.model.repository.event.MockEventRepository
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.screens.QrCodeScanTicketUiScreen
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.qrCode.QrCodeTicketUi
import com.github.se.eventradar.viewmodel.EventUiState
import com.github.se.eventradar.viewmodel.qrCode.QrCodeAnalyser
import com.github.se.eventradar.viewmodel.qrCode.ScanTicketQrViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QrCodeScanTicketUiTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val mRuntimePermissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.CAMERA)

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @RelaxedMockK lateinit var mockViewModel: ScanTicketQrViewModel

  private lateinit var mDevice: UiDevice

  private lateinit var viewModel: ScanTicketQrViewModel
  private lateinit var userRepository: IUserRepository
  private lateinit var eventRepository: IEventRepository
  private lateinit var qrCodeAnalyser: QrCodeAnalyser
  private val myUID = "user1"

  private val myEventTabDetailsUiState =
      MutableStateFlow(
          ScanTicketQrViewModel.QrCodeScanTicketState(
              decodedResult = "",
              tabState = ScanTicketQrViewModel.Tab.MyEvent,
              action = ScanTicketQrViewModel.Action.ScanTicket,
              eventUiState =
                  EventUiState(
                      eventName = "Event 1",
                      eventPhoto = "",
                      start = LocalDateTime.now(),
                      end = LocalDateTime.now(),
                      location = Location(0.0, 0.0, "Test Location"),
                      description = "Test Description",
                      ticket = EventTicket("Test Ticket", 0.0, 100, 59),
                      mainOrganiser = "1",
                      category = EventCategory.COMMUNITY)))

  @Before
  fun testSetup() {
    MockKAnnotations.init(this)
    every { mockNavActions.navigateTo(any()) } just Runs
    userRepository = MockUserRepository()
    (userRepository as MockUserRepository).updateCurrentUserId(myUID)
    eventRepository = MockEventRepository()
    qrCodeAnalyser = mockk<QrCodeAnalyser>(relaxed = true)
    mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
  }

  @Test
  fun displaysAllComponentsCorrectly_CameraPermittedAlways(): Unit = run {
    onComposeScreen<QrCodeScanTicketUiScreen>(composeTestRule) {
      val viewModel = setupViewModelWithState(ScanTicketQrViewModel.Action.ScanTicket)
      composeTestRule.setContent { QrCodeTicketUi(viewModel, mockNavActions) }
      scanQrTab.performClick()
      logo.assertIsDisplayed()
      tabs.assertIsDisplayed()
      myQrTab.assertIsDisplayed()
      scanQrTab.assertIsDisplayed()
      qrScanner.assertIsDisplayed()
      bottomNavMenu.assertIsDisplayed()
    }
  }

  @Test
  fun displaysAllComponentsCorrectly_Approved(): Unit = run {
    val viewModel = setupViewModelWithState(ScanTicketQrViewModel.Action.ApproveEntry)
    onComposeScreen<QrCodeScanTicketUiScreen>(composeTestRule) {
      composeTestRule.setContent { QrCodeTicketUi(viewModel, mockNavActions) }
      scanQrTab.performClick()
      logo.assertIsDisplayed()
      tabs.assertIsDisplayed()
      myQrTab.assertIsDisplayed()
      scanQrTab.assertIsDisplayed()
      bottomNavMenu.assertIsDisplayed()
      approvedBox.assertIsDisplayed()
      approvedText.assertIsDisplayed()
      closeButton.assertIsDisplayed()
    }
  }

  @Test
  fun displaysAllComponentsCorrectly_Denied(): Unit = run {
    val viewModel = setupViewModelWithState(ScanTicketQrViewModel.Action.DenyEntry)
    onComposeScreen<QrCodeScanTicketUiScreen>(composeTestRule) {
      composeTestRule.setContent { QrCodeTicketUi(viewModel, mockNavActions) }
      scanQrTab.performClick()
      logo.assertIsDisplayed()
      tabs.assertIsDisplayed()
      myQrTab.assertIsDisplayed()
      bottomNavMenu.assertIsDisplayed()
      deniedBox.assertIsDisplayed()
      deniedText.assertIsDisplayed()
      closeButton.assertIsDisplayed()
    }
  }

  @Test
  fun displaysAllComponentsCorrectly_Error1(): Unit = run {
    val viewModel = setupViewModelWithState(ScanTicketQrViewModel.Action.FirebaseFetchError)
    onComposeScreen<QrCodeScanTicketUiScreen>(composeTestRule) {
      composeTestRule.setContent { QrCodeTicketUi(viewModel, mockNavActions) }
      scanQrTab.performClick()
      logo.assertIsDisplayed()
      tabs.assertIsDisplayed()
      myQrTab.assertIsDisplayed()
      scanQrTab.assertIsDisplayed()
      bottomNavMenu.assertIsDisplayed()
      errorBox.assertIsDisplayed()
      errorText.assertIsDisplayed()
      closeButton.assertIsDisplayed()
    }
  }

  @Test
  fun displaysAllComponentsCorrectly_Error2(): Unit = run {
    val viewModel = setupViewModelWithState(ScanTicketQrViewModel.Action.FirebaseUpdateError)
    onComposeScreen<QrCodeScanTicketUiScreen>(composeTestRule) {
      composeTestRule.setContent { QrCodeTicketUi(viewModel, mockNavActions) }
      scanQrTab.performClick()
      logo.assertIsDisplayed()
      tabs.assertIsDisplayed()
      myQrTab.assertIsDisplayed()
      scanQrTab.assertIsDisplayed()
      bottomNavMenu.assertIsDisplayed()
      errorBox.assertIsDisplayed()
      errorText.assertIsDisplayed()
      closeButton.assertIsDisplayed()
    }
  }

  @Test
  fun displaysAllComponentsCorrectly_Error3(): Unit = run {
    val viewModel = setupViewModelWithState(ScanTicketQrViewModel.Action.AnalyserError)
    onComposeScreen<QrCodeScanTicketUiScreen>(composeTestRule) {
      composeTestRule.setContent { QrCodeTicketUi(viewModel, mockNavActions) }
      scanQrTab.performClick()
      logo.assertIsDisplayed()
      tabs.assertIsDisplayed()
      myQrTab.assertIsDisplayed()
      scanQrTab.assertIsDisplayed()
      bottomNavMenu.assertIsDisplayed()
      errorBox.assertIsDisplayed()
      errorText.assertIsDisplayed()
      closeButton.assertIsDisplayed()
    }
  }

  @Test
  fun resetsTabState(): Unit = run {
    val viewModel = setupViewModelWithState(ScanTicketQrViewModel.Action.ScanTicket)
    onComposeScreen<QrCodeScanTicketUiScreen>(composeTestRule) {
      composeTestRule.setContent { QrCodeTicketUi(viewModel, mockNavActions) }
      scanQrTab.performClick()
      myQrTab.performClick()
      assertEquals(ScanTicketQrViewModel.Tab.MyEvent, viewModel.uiState.value.tabState)
    }
  }

  @Test
  fun screenDisplaysNavigationElementsCorrectly() = run {
    every { mockViewModel.uiState } returns myEventTabDetailsUiState
    onComposeScreen<QrCodeScanTicketUiScreen>(composeTestRule) {
      composeTestRule.setContent { QrCodeTicketUi(mockViewModel, mockNavActions) }
      bottomNavMenu.assertIsDisplayed()
      goBackButton.assertIsDisplayed()
    }
  }

  @Test
  fun screenDisplaysContentElementsCorrectly1() =
      //  Test(timeout = 45.seconds) {
      run {
        every { mockViewModel.uiState } returns myEventTabDetailsUiState
        //      val viewModel = setupViewModelMyEventTab()
        onComposeScreen<QrCodeScanTicketUiScreen>(composeTestRule) {
          composeTestRule.setContent { QrCodeTicketUi(mockViewModel, mockNavActions) }
          lazyEventDetails.assertIsDisplayed()
          eventTitle { assertIsDisplayed() }
          eventImage { assertIsDisplayed() }
          descriptionTitle { assertIsDisplayed() }
          descriptionContent {
            assertIsDisplayed()
            assertTextContains("Test Description")
          }
        }
      }

  @Test
  fun screenDisplaysContentElementsCorrectly2() =
      //  Test(timeout = 45.seconds) {
      run {
        //      val viewModel = setupViewModelMyEventTab()
        every { mockViewModel.uiState } returns myEventTabDetailsUiState
        onComposeScreen<QrCodeScanTicketUiScreen>(composeTestRule) {
          composeTestRule.setContent { QrCodeTicketUi(mockViewModel, mockNavActions) }
          distanceTitle { assertIsDisplayed() }
          distanceContent { assertIsDisplayed() }
        }
      }

  @Test
  fun screenDisplaysContentElementsCorrectly3() =
      //  Test(timeout = 45.seconds) {
      run {
        //      val viewModel = setupViewModelMyEventTab()
        every { mockViewModel.uiState } returns myEventTabDetailsUiState
        onComposeScreen<QrCodeScanTicketUiScreen>(composeTestRule) {
          composeTestRule.setContent { QrCodeTicketUi(mockViewModel, mockNavActions) }
          categoryTitle { assertIsDisplayed() }
          categoryContent {
            assertIsDisplayed()
            assertTextContains("Community")
          }
        }
      }

  @Test
  fun screenDisplaysContentElementsCorrectly4() =
      //  Test(timeout = 45.seconds) {
      run {
        //      val viewModel = setupViewModelMyEventTab()
        every { mockViewModel.uiState } returns myEventTabDetailsUiState
        onComposeScreen<QrCodeScanTicketUiScreen>(composeTestRule) {
          composeTestRule.setContent { QrCodeTicketUi(mockViewModel, mockNavActions) }
          dateTitle { assertIsDisplayed() }
          dateContent { assertIsDisplayed() }
        }
      }

  @Test
  fun screenDisplaysContentElementsCorrectly5() =
      //  Test(timeout = 45.seconds) {
      run {
        //      val viewModel = setupViewModelMyEventTab()
        every { mockViewModel.uiState } returns myEventTabDetailsUiState
        onComposeScreen<QrCodeScanTicketUiScreen>(composeTestRule) {
          composeTestRule.setContent { QrCodeTicketUi(mockViewModel, mockNavActions) }
          timeTitle { assertIsDisplayed() }
          timeContent { assertIsDisplayed() }
        }
      }

  @Test
  fun screenDisplaysContentElementsCorrectly6() =
      //  Test(timeout = 45.seconds) {
      run {
        //      val viewModel = setupViewModelMyEventTab()
        every { mockViewModel.uiState } returns myEventTabDetailsUiState
        onComposeScreen<QrCodeScanTicketUiScreen>(composeTestRule) {
          composeTestRule.setContent { QrCodeTicketUi(mockViewModel, mockNavActions) }
          ticketSoldTitle {
//            performScrollTo()
            assertIsDisplayed()
          }
          ticketSoldContent {
//            performScrollTo()
            assertIsDisplayed()
            assertTextContains("59 tickets sold")
          }
        }
      }

  @Test
  fun goBackButtonTriggersBackNavigation() = run {
    //    val viewModel = setupViewModelMyEventTab()
    every { mockViewModel.uiState } returns myEventTabDetailsUiState
    onComposeScreen<QrCodeScanTicketUiScreen>(composeTestRule) {
      composeTestRule.setContent { QrCodeTicketUi(mockViewModel, mockNavActions) }
      goBackButton {
        // arrange: verify the pre-conditions
        assertIsDisplayed()
        assertIsEnabled()
        // act: go back !
        performClick()
      }
    }
    // assert: the nav action has been called
    verify { mockNavActions.goBack() }
    confirmVerified(mockNavActions)
  }


  private fun setupViewModelWithState(action: ScanTicketQrViewModel.Action): ScanTicketQrViewModel {
    // Create the ViewModel with a specific state for testing
    return ScanTicketQrViewModel(userRepository, eventRepository, qrCodeAnalyser, "1").apply {
      changeAction(action) // Assuming there is a method to change actions
    }
  }
}
