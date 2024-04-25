//package com.github.se.eventradar
//import org.junit.Rule
//import org.junit.Test
//import androidx.activity.ComponentActivity
//import androidx.navigation.compose.rememberNavController
//import com.github.se.eventradar.ui.qrCode.QrCodeScreen
//import junit.framework.TestCase
//
//
//@RunWith(AndroidJUnit4::class)
//class   QrCodeScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
//
//    @get:Rule val composeTestRule = createComposeRule()
//
//    @RunWith(AndroidJUnit4::class)
//
//    @get:Rule val composeTestRule = createComposeRule()
//
//   fun testTagsAreDisplayed() {
//        composeTestRule.setContent {
//            QrCodeScreen(NavigationActions(rememberNavController()))
//        }
//
//        // Assert that all testTag elements are displayed
//        composeTestRule.onNodeWithTag("homeScreen").assertIsDisplayed()
//        composeTestRule.onNodeWithTag("logo").assertIsDisplayed()
//        composeTestRule.onNodeWithTag("tabs").assertIsDisplayed()
//        composeTestRule.onNodeWithTag("My QR Code").assertIsDisplayed()
//        composeTestRule.onNodeWithTag("Scan QR Code").assertIsDisplayed()
//        composeTestRule.onNodeWithTag("bottomNavMenu").assertIsDisplayed()
//    }
//
//}