package com.github.se.eventradar.hosting

import android.net.Uri
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.User
import com.github.se.eventradar.screens.CreateEventScreen
import com.github.se.eventradar.screens.HostingScreen
import com.github.se.eventradar.ui.hosting.CreateEventScreen
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.viewmodel.CreateEventUiState
import com.github.se.eventradar.viewmodel.CreateEventViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateEventTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()
  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)
  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions
    @RelaxedMockK lateinit var mockCreateEventViewModel: CreateEventViewModel

    val mockUser =
        User(
            userId = "1",
            birthDate = "01/01/2000",
            email = "test@test.com",
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "1234567890",
            accountStatus = "active",
            eventsAttendeeList = mutableListOf(),
            eventsHostList = mutableListOf(),
            friendsList = mutableListOf("2"),
            profilePicUrl = "",
            qrCodeUrl = "",
            bio = "",
            username = "john_doe")

    private val mockCreateEventUiState=
        MutableStateFlow(
            CreateEventUiState(
                eventName = "Annual Tech Conference",
                eventPhotoUri = Uri.parse("https://example.com/photo.jpg"),
                eventDescription = "A comprehensive tech conference.",
                eventCategory = "Technology",
                startDate = "2024-01-15",
                endDate = "2024-01-18",
                startTime = "10:00 AM",
                endTime = "5:00 PM",
                location = "Convention Center, Downtown",
                ticketName = "General Admission",
                ticketCapacity = "500",
                ticketPrice = "299.99",
                organiserList = listOf(mockUser),
                listOfLocations = listOf(Location(20.0, 20.0, "Location1")),
                hostFriendsList = listOf(),
            )
        )


    @Before
    fun testSetup() {
        every { mockCreateEventViewModel.addEvent() } returns Unit
        every { mockCreateEventViewModel.uiState } returns mockCreateEventUiState
        composeTestRule.setContent {
            CreateEventScreen(viewModel = mockCreateEventViewModel, navigationActions = mockNavActions)
        }
    }
    @Test
    fun screenDisplaysAllElementsCorrectly() = run {
        ComposeScreen.onComposeScreen<CreateEventScreen>(composeTestRule) {
            topBar { assertIsDisplayed() }
            goBackButton { assertIsDisplayed() }
            createEventText { assertIsDisplayed() }
            createEventScreenColumn { assertIsDisplayed() }
            eventImagePicker { assertIsDisplayed() }
            eventNameTextField { assertIsDisplayed() }
            eventDescriptionTextField { assertIsDisplayed() }
            datesRow { assertIsDisplayed() }
            startDateTextField { assertIsDisplayed() }
            endDateTextField { assertIsDisplayed() }
            timesRow { assertIsDisplayed() }
            startTimeTextField { assertIsDisplayed() }
            locationExposedDropDownMenuBox { assertIsDisplayed() }
            locationDropDownMenuTextField { assertIsDisplayed() }
            exposedDropDownMenuBox { assertIsDisplayed() }
            ticketNameDropDownMenu { assertIsDisplayed() }
            eventCategoryDropDown { assertIsDisplayed() }
            ticketQuantityTextField{ assertIsDisplayed() }
            ticketPriceTextField { assertIsDisplayed() }
            multiSelectExposedDropDownMenuBox { assertIsDisplayed() }
            organisersMultiDropDownMenuTextField { assertIsDisplayed() }
            publishEventButton { assertIsDisplayed() }
            /*
    val successDialogBox : KNode = createEventScreenColumn.child{hasTestTag("successDialogBox")}
    val successDisplayText : KNode = successDialogBox.child { hasTestTag("DisplayText") }
    val successDisplayTitle : KNode = successDialogBox.child { hasTestTag("DisplayTitle") }
    val successDialogConfirmButton : KNode = successDialogBox.child { hasTestTag("dialogConfirmButton") }
    val failureDialogBox : KNode = createEventScreenColumn.child{hasTestTag("failureDialogBox")}
    val failureDisplayText : KNode = successDialogBox.child { hasTestTag("DisplayText") }
    val failureDisplayTitle : KNode = successDialogBox.child { hasTestTag("DisplayTitle") }
    val failureDialogConfirmButton : KNode = successDialogBox.child { hasTestTag("dialogConfirmButton") }
             */
        }
    }
    @Test
    fun successDialogBoxDisplayedUponPublishEvent() = run {
        ComposeScreen.onComposeScreen<CreateEventScreen>(composeTestRule) {
            step("Click on publish event") {
                publishEventButton {
                    assertIsDisplayed()
                    performClick()
                }
            }
            mockCreateEventUiState.value = mockCreateEventUiState.value.copy(showAddEventSuccess = true)
            step("Check if success dialog box is displayed") {
                successDialogBox { assertIsDisplayed() }
                successDisplayText {assertIsDisplayed()}
                successDisplayTitle {assertIsDisplayed()}
                successDialogConfirmButton {assertIsDisplayed()}
            }
            //click on Confirm!
        }
    }


}
