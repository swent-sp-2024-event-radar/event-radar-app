package com.github.se.eventradar.qrCode

import android.Manifest
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import com.github.se.eventradar.model.repository.user.IUserRepository
import com.github.se.eventradar.model.repository.user.MockUserRepository
import com.github.se.eventradar.screens.QrCodeScanFriendUiScreen
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.qrCode.QrCodeScreen
import com.github.se.eventradar.viewmodel.qrCode.QrCodeAnalyser
import com.github.se.eventradar.viewmodel.qrCode.ScanFriendQrViewModel
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
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// TODO test when camera denied
// TODO fix navigation test

@RunWith(AndroidJUnit4::class)
class QrCodeScanFriendUiTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val mRuntimePermissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.CAMERA)

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  private lateinit var mDevice: UiDevice

  private lateinit var viewModel: ScanFriendQrViewModel
  private lateinit var userRepository: IUserRepository
  private lateinit var qrCodeAnalyser: QrCodeAnalyser
  private val myUID = "user1"

  @Before
  fun testSetup() {
    MockKAnnotations.init(this)
    every { mockNavActions.navigateTo(any()) } just Runs
    userRepository = MockUserRepository()
    (userRepository as MockUserRepository).updateCurrentUserId(myUID)
    qrCodeAnalyser = mockk<QrCodeAnalyser>(relaxed = true)
    viewModel = ScanFriendQrViewModel(userRepository, qrCodeAnalyser)
    composeTestRule.setContent { QrCodeScreen(viewModel, mockNavActions) }
    mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
  }

  @Test
  fun displaysAllComponentsCorrectly_CameraPermittedAlways(): Unit = run {
    onComposeScreen<QrCodeScanFriendUiScreen>(composeTestRule) {
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
  fun testTabInteraction() = run {
    onComposeScreen<QrCodeScanFriendUiScreen>(composeTestRule) {
      scanQrTab.performClick()
      // Assert that the ViewModel's active tab state has changed to ScanQR
      assertEquals(ScanFriendQrViewModel.Tab.ScanQR, viewModel.uiState.value.tabState)
    }
  }

  @Test
  fun switchesScreenWhenNavigatedToNextScreen() = run {
    onComposeScreen<QrCodeScanFriendUiScreen>(composeTestRule) {
      viewModel.changeAction(ScanFriendQrViewModel.Action.NavigateToNextScreen)
      composeTestRule.waitForIdle()
      verify { mockNavActions.navigateTo(any()) }
    }
  }
}
