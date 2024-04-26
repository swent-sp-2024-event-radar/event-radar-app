package com.github.se.eventradar.qrCode

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.screens.QrCodeScanFriendUiScreen
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.qrCode.QrCodeScreen
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QrCodeScanFriendUiTest: TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val mockkRule = MockKRule(this)

    @RelaxedMockK
    lateinit var mockNavActions: NavigationActions

    @Before
    fun testSetup() {
//        MockKAnnotations.init(this)
        composeTestRule.setContent { QrCodeScreen(mockNavActions) }
    }

    @Test
    fun qrCodeScanFriendUi_displaysAllComponentsCorrectly(): Unit =
        run {
            onComposeScreen<QrCodeScanFriendUiScreen>(composeTestRule) {
                try {
                    logo.assertIsDisplayed()
                } catch (e: AssertionError) {
                    println("Failed to find Logo: ${e.message}")
                    throw e  // Re-throw to fail the test
                }

                try {
                    tabs.assertIsDisplayed()
                } catch (e: AssertionError) {
                    println("Failed to find Tabs: ${e.message}")
                    throw e  // Re-throw to fail the test
                }

                try {
                    myQrTab.assertIsDisplayed()
                } catch (e: AssertionError) {
                    println("Failed to find My QR Tab: ${e.message}")
                    throw e  // Re-throw to fail the test
                }

                try {
                    scanQrTab.assertIsDisplayed()
                } catch (e: AssertionError) {
                    println("Failed to find Scan QR Tab: ${e.message}")
                    throw e  // Re-throw to fail the test
                }

                try {
                    qrScanner.assertIsDisplayed()
                } catch (e: AssertionError) {
                    println("Failed to find QR Scanner: ${e.message}")
                    throw e  // Re-throw to fail the test
                }

                try {
                    bottomNavMenu.assertIsDisplayed()
                } catch (e: AssertionError) {
                    println("Failed to find Bottom Navigation Menu: ${e.message}")
                    throw e  // Re-throw to fail the test
                }
            }
        }
}


//                logo { assertIsDisplayed() }
//                tabs { assertIsDisplayed() }
//                myQrTab { assertIsDisplayed() }
//                scanQrTab { assertIsDisplayed() }
//                qrScanner { assertIsDisplayed() }
//                bottomNavMenu { assertIsDisplayed() }
//            }
//        }
//}

//@Test
//fun qrCodeScanFriendUi_displaysAllComponentsCorrectly() {
//    composeTestRule.setContent {
//        QrCodeScreen(mockNavActions) // Assuming a fake implementation for NavigationActions
//    }
//
//    QrCodeScanFriendUiScreen(composeTestRule).apply {
//        logo.assertIsDisplayed()
//        tabs.assertIsDisplayed()
//        myQrTab.assertIsDisplayed()
//        scanQrTab.assertIsDisplayed()
//        qrScanner.assertIsDisplayed()
//        bottomNavMenu.assertIsDisplayed()
//    }
//}
//}

