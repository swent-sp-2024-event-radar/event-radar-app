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
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
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

  private lateinit var mDevice: UiDevice

  private lateinit var viewModel: ScanTicketQrViewModel
  private lateinit var userRepository: IUserRepository
  private lateinit var eventRepository: IEventRepository
  private lateinit var qrCodeAnalyser: QrCodeAnalyser
  private val myUID = "user1"

  private val mockEvent =
      Event(
          eventName = "Event 1",
          eventPhoto = "",
          start = LocalDateTime.now(),
          end = LocalDateTime.now(),
          location = Location(0.0, 0.0, "Test Location"),
          description = "Test Description",
          ticket = EventTicket("Test Ticket", 0.0, 100, 59),
          mainOrganiser = "1",
          organiserList = mutableListOf("Test Organiser"),
          attendeeList = mutableListOf("user1", "user2", "user3"),
          category = EventCategory.COMMUNITY,
          fireBaseID = "1")

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
    val viewModel = setupViewModelMyEventTab()
    onComposeScreen<QrCodeScanTicketUiScreen>(composeTestRule) {
      composeTestRule.setContent { QrCodeTicketUi(viewModel, mockNavActions) }
      bottomNavMenu.assertIsDisplayed()
      goBackButton.assertIsDisplayed()
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun screenDisplaysContentElementsCorrectly() =
      runTest(timeout = 45.seconds) {
        // Your test code here {
        val viewModel = setupViewModelMyEventTab()
        onComposeScreen<QrCodeScanTicketUiScreen>(composeTestRule) {
          composeTestRule.setContent { QrCodeTicketUi(viewModel, mockNavActions) }
          advanceUntilIdle()
          eventTitle { assertIsDisplayed() }
          eventImage { assertIsDisplayed() }
          descriptionTitle { assertIsDisplayed() }
          descriptionContent {
            assertIsDisplayed()
            assertTextContains("Test Description")
          }
          distanceTitle { assertIsDisplayed() }
          distanceContent { assertIsDisplayed() }
          categoryTitle { assertIsDisplayed() }
          categoryContent {
            assertIsDisplayed()
            assertTextContains("Community")
          }
          dateTimeTitle { assertIsDisplayed() }
          dateTimeStartContent { assertIsDisplayed() }
          dateTimeEndContent { assertIsDisplayed() }
          ticketSoldTitle.assertIsDisplayed()
          //          ticketSoldContent {
          //            assertIsDisplayed()
          //            assertTextContains("59 tickets sold")
          //          }
          //          }
        }
      }

  @Test
  fun goBackButtonTriggersBackNavigation() = run {
    val viewModel = setupViewModelMyEventTab()
    onComposeScreen<QrCodeScanTicketUiScreen>(composeTestRule) {
      composeTestRule.setContent { QrCodeTicketUi(viewModel, mockNavActions) }
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

  private fun setupViewModelMyEventTab(): ScanTicketQrViewModel = runBlocking {
    eventRepository.addEvent(mockEvent)
    // Create the ViewModel with a specific state for testing
    return@runBlocking ScanTicketQrViewModel(userRepository, eventRepository, qrCodeAnalyser, "1")
        .apply {
          changeTabState(
              ScanTicketQrViewModel.Tab.MyEvent) // Assuming there is a method to change actions
        }
  }
}