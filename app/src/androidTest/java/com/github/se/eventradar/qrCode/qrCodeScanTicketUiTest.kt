package com.github.se.eventradar.qrCode

import android.Manifest
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
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
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.mockk
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
      viewModel = ScanTicketQrViewModel(userRepository, eventRepository, qrCodeAnalyser)
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

  private fun setupViewModelWithState(action: ScanTicketQrViewModel.Action): ScanTicketQrViewModel {
    // Create the ViewModel with a specific state for testing
    return ScanTicketQrViewModel(userRepository, eventRepository, qrCodeAnalyser).apply {
      changeAction(action) // Assuming there is a method to change actions
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
}
