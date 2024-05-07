package com.github.se.eventradar.qrCode

import android.Manifest
import android.util.Log
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.time.delay
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.milliseconds

@RunWith(AndroidJUnit4::class)
class QrCodeScanTicketUiTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule
  val composeTestRule = createComposeRule()

  @get:Rule
  val mRuntimePermissionRule: GrantPermissionRule =
    GrantPermissionRule.grant(Manifest.permission.CAMERA)

  @get:Rule
  val mockkRule = MockKRule(this)

  @RelaxedMockK
  lateinit var mockNavActions: NavigationActions

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
      approvedBox.assertIsDisplayed()
    }
  }

  private fun setupViewModelWithState(action: ScanTicketQrViewModel.Action): ScanTicketQrViewModel {
    // Create the ViewModel with a specific state for testing
    return ScanTicketQrViewModel(userRepository, eventRepository, qrCodeAnalyser).apply {
      changeAction(action)  // Assuming there is a method to change actions
    }
  }

  @Test
  fun displaysAllComponentsCorrectly_x(): Unit = run {
    val viewModel = setupViewModelWithState(ScanTicketQrViewModel.Action.ApproveEntry)
    onComposeScreen<QrCodeScanTicketUiScreen>(composeTestRule) {
      composeTestRule.setContent { QrCodeTicketUi(viewModel, mockNavActions) }
      scanQrTab.performClick()
      logo.assertIsDisplayed()
      tabs.assertIsDisplayed()
      myQrTab.assertIsDisplayed()
      scanQrTab.assertIsDisplayed()
//      qrScanner.assertIsDisplayed()
      bottomNavMenu.assertIsDisplayed()
    }
  }
}
//
//  @Test
//  fun displaysAllComponentsCorrectly_EntryApproved(): Unit = run {
//    onComposeScreen<QrCodeScanTicketUiScreen>(composeTestRule) {
//      viewModel = ScanTicketQrViewModel(userRepository, eventRepository, qrCodeAnalyser)
//      composeTestRule.setContent { QrCodeTicketUi(viewModel, mockNavActions) }
//      scanQrTab.performClick()
//      logo.assertIsDisplayed()
//      tabs.assertIsDisplayed()
//      myQrTab.assertIsDisplayed()
//      scanQrTab.assertIsDisplayed()
//      qrScanner.assertIsDisplayed()
//      bottomNavMenu.assertIsDisplayed()
//    }
//  }

//  @Test
//  fun displaysAllComponentsCorrectly_ApproveEntry(): Unit = run {
//    onComposeScreen<QrCodeScanTicketUiScreen>(composeTestRule) {
//      viewModel = ScanTicketQrViewModel(userRepository, eventRepository, qrCodeAnalyser)
//      composeTestRule.setContent { QrCodeTicketUi(viewModel, mockNavActions) }
//      scanQrTab.performClick()
//      logo.assertIsDisplayed()
//      tabs.assertIsDisplayed()
//      myQrTab.assertIsDisplayed()
//      scanQrTab.assertIsDisplayed()
//      qrScanner.assertIsDisplayed()
//      bottomNavMenu.assertIsDisplayed()
//    }
//  }

//  @Test
//  fun displaysAllComponentsCorrectly_ApproveEntry(): Unit = run {
////    val mockViewModel = mockk<ScanTicketQrViewModel>()
////    every { mockViewModel.uiState } returns MutableStateFlow(
////      ScanTicketQrViewModel.QrCodeScanTicketState(
////        decodedResult = "uuu",
////        action = ScanTicketQrViewModel.Action.ApproveEntry, // Set the action as needed for the test
////        tabState = ScanTicketQrViewModel.Tab.MyEvent
////      )
////    )
//    val mockViewModel = ScanTicketQrViewModel(userRepository, eventRepository, qrCodeAnalyser)
//    // Set up the UI with the mocked ViewModel
//    composeTestRule.setContent {
//      QrCodeTicketUi(viewModel = mockViewModel, navigationActions = mockNavActions) }
//
//    try {
//        logo.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        Log.d("Failed at logo visibility check", e.message.toString())
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//
//      try {
//        tabs.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        Log.d("Failed at tabs visibility check", e.message.toString())
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        myQrTab.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        Log.d("Failed at myQrTab visibility check", e.message.toString())
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        scanQrTab.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        Log.d("Failed at scanQrTab visibility check", e.message.toString())
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//  }
//
//}

//  @Test
//  fun displaysAllComponentsCorrectly_Access(): Unit = run {
//    onComposeScreen<QrCodeScanTicketUiScreen>(composeTestRule) {
//      val mockViewModel = mockk<ScanTicketQrViewModel>()
//      every { mockViewModel.uiState } returns MutableStateFlow(
//        // Set up the initial state you need for the test
//        ScanTicketQrViewModel.QrCodeScanTicketState(action = ScanTicketQrViewModel.Action.ApproveEntry)
//      )
////
////      viewModel = ScanTicketQrViewModel(userRepository, eventRepository, qrCodeAnalyser)
//
//
//
//      composeTestRule.setContent { QrCodeTicketUi(viewModel = mockViewModel, navigationActions = mockNavActions)}
//
//      scanQrTab.performClick()
////      while(viewModel.uiState.value.action != ScanTicketQrViewModel.Action.ApproveEntry) {
////        //wait
////      }
//
//      println("1st assetion passed")
//
//      try {
//        logo.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        Log.d("Failed at logo visibility check", e.message.toString())
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//
//      try {
//        tabs.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        Log.d("Failed at tabs visibility check", e.message.toString())
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        myQrTab.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        Log.d("Failed at myQrTab visibility check", e.message.toString())
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        scanQrTab.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        Log.d("Failed at scanQrTab visibility check", e.message.toString())
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//
////      composeTestRule.waitUntil { viewModel.uiState.value.action == ScanTicketQrViewModel.Action.ApproveEntry }
//
//      try {
//        approvedBox.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        Log.d("Failed at  approvedBox visibility check", e.message.toString())
//        throw e  // Re-throw to ensure the test fails as expected
//      }
////      try {
////        approvedText.assertIsDisplayed()
////      } catch (e: AssertionError) {
////        Log.d("Failed at approvedText visibility check", e.message.toString())
////        throw e  // Re-throw to ensure the test fails as expected
////      }
////      try {
////        closeButton.assertIsDisplayed()
////      } catch (e: AssertionError) {
////        Log.d("Failed at closeButton visibility check", e.message.toString())
////        throw e  // Re-throw to ensure the test fails as expected
////      }
//      try {
//        bottomNavMenu.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        Log.d("Failed at  bottomNavMenu visibility check", e.message.toString())
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//    }
//  }
//}





//      viewModel = ScanTicketQrViewModel(userRepository, eventRepository, qrCodeAnalyser)
//      QrCodeTicketUi(navigationActions = rem)
////      viewModel.changeAction(ScanTicketQrViewModel.Action.ApproveEntry)
////      composeTestRule.waitUntil {
////        viewModel.uiState.value.action == ScanTicketQrViewModel.Action.ApproveEntry
//      }
//      composeTestRule.waitForIdle()
//      composeTestRule.setContent { QrCodeTicketUi(viewModel, mockNavActions) }


//  @Test
//  fun displaysAllComponentsCorrectly_Deny(): Unit = run {
//    onComposeScreen<QrCodeScanTicketUiScreen>(composeTestRule) {
//      viewModel.changeAction(ScanTicketQrViewModel.Action.DenyEntry)
//      while(viewModel.uiState.value.action != ScanTicketQrViewModel.Action.DenyEntry) {
//        //wait
//      }
//      scanQrTab.performClick()
//      try {
//        logo.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at logo visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        tabs.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at tabs visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        myQrTab.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at myQrTab visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        scanQrTab.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at scanQrTab visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        deniedBox.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at  deniedBox visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        deniedText.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at deniedText visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        closeButton.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at closeButton visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        bottomNavMenu.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at  bottomNavMenuvisibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//    }
//  }
//
//  @Test
//  fun displaysAllComponentsCorrectly_Error1(): Unit = run {
//    onComposeScreen<QrCodeScanTicketUiScreen>(composeTestRule) {
//      viewModel.changeAction(ScanTicketQrViewModel.Action.FirebaseFetchError)
//      while(viewModel.uiState.value.action != ScanTicketQrViewModel.Action.FirebaseFetchError) {
//        //wait
//      }
//      scanQrTab.performClick()
//      try {
//        logo.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at logo visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        tabs.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at tabs visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        myQrTab.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at myQrTab visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        scanQrTab.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at scanQrTab visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        errorBox.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at  errorBox visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        errorText.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at errorText visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        closeButton.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at closeButton visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        bottomNavMenu.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at  bottomNavMenuvisibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//    }
//  }
//
//  @Test
//  fun displaysAllComponentsCorrectly_Error2(): Unit = run {
//    onComposeScreen<QrCodeScanTicketUiScreen>(composeTestRule) {
//      viewModel.changeAction(ScanTicketQrViewModel.Action.FirebaseUpdateError)
//      while(viewModel.uiState.value.action != ScanTicketQrViewModel.Action.FirebaseUpdateError) {
//        //wait
//      }
//      try {
//        logo.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at logo visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        tabs.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at tabs visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        myQrTab.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at myQrTab visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        scanQrTab.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at scanQrTab visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        errorBox.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at  errorBox visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        errorText.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at errorText visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        closeButton.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at closeButton visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        bottomNavMenu.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at  bottomNavMenuvisibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//    }
//  }
//
//  @Test
//  fun displaysAllComponentsCorrectly_Error3(): Unit = run {
//    onComposeScreen<QrCodeScanTicketUiScreen>(composeTestRule) {
//      viewModel.changeAction(ScanTicketQrViewModel.Action.AnalyserError)
//      while(viewModel.uiState.value.action != ScanTicketQrViewModel.Action.AnalyserError) {
//        //wait
//      }
//      scanQrTab.performClick()
//      try {
//        logo.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at logo visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        tabs.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at tabs visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        myQrTab.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at myQrTab visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        scanQrTab.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at scanQrTab visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        errorBox.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at  errorBox visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        errorText.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at errorText visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        closeButton.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at closeButton visibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//      try {
//        bottomNavMenu.assertIsDisplayed()
//      } catch (e: AssertionError) {
//        println("Failed at  bottomNavMenuvisibility check: ${e.message}")
//        throw e  // Re-throw to ensure the test fails as expected
//      }
//    }
//  }

//  suspend fun callChangeActionOnMainThread(action: ScanTicketQrViewModel.Action) {
//    withContext(Dispatchers.Main) {
//      viewModel.changeAction(action)
//    }
//  }

//  suspend fun delay() {
//    withContext(Dispatchers.Main) {
//      delay(1000.milliseconds.toLongMilliseconds())
//    }
//  }


