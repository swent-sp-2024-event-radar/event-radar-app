package com.github.se.eventradar.hosting

import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.User
import com.github.se.eventradar.screens.CreateEventScreen
import com.github.se.eventradar.ui.hosting.CreateEventScreen
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.viewmodel.CreateEventUiState
import com.github.se.eventradar.viewmodel.CreateEventViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
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

  private val mockCreateEventUiState =
      MutableStateFlow(
          CreateEventUiState(
              eventName = "Annual Tech Conference",
              eventPhotoUri = Uri.parse("https://example.com/photo.jpg"),
              eventDescription = "A comprehensive tech conference.",
              eventCategory = "Technology",
              startDate = "2024-01-15",
              endDate = "3000-01-01",
              startTime = "10:00 AM",
              endTime = "5:00 PM",
              location = "",
              ticketName = "General Admission",
              ticketCapacity = "500",
              ticketPrice = "0.0",
              organiserList = listOf(mockUser),
              listOfLocations = listOf(Location(20.0, 20.0, "Location1")),
              hostFriendsList = listOf(),
          ))

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
      endTimeTextField { assertIsDisplayed() }
      locationExposedDropDownMenuBox { assertIsDisplayed() }
      locationDropDownMenuTextField { assertIsDisplayed() }
      exposedDropDownMenuBox { assertIsDisplayed() }
      ticketNameDropDownMenuTextField { assertIsDisplayed() }
      eventCategoryDropDown { assertIsDisplayed() }
      ticketQuantityTextField { assertIsDisplayed() }
      ticketPriceTextField { assertIsDisplayed() }
      multiSelectExposedDropDownMenuBox { assertIsDisplayed() }
      organisersMultiDropDownMenuTextField { assertIsDisplayed() }
      publishEventButton { assertIsDisplayed() }
    }
  }

  @Test
  fun successfulPressOnMultiSelectExposedDropDownMenuBox() = run {
    ComposeScreen.onComposeScreen<CreateEventScreen>(composeTestRule) {
      step("Click on Organisers Drop Down Menu") {
        organisersMultiDropDownMenuTextField {
          assertIsDisplayed()
          performClick()
        }
      }
      step("Check if Organisers Drop Down Menu is displayed") {
        composeTestRule
            .onNodeWithTag("multiSelectExposedDropdownMenu", useUnmergedTree = true)
            .assertIsDisplayed()
      }
      step("Click on Organisers Drop Down Toggle Icon") {
        composeTestRule
            .onNodeWithTag("multiSelectDropDownToggleIcon", useUnmergedTree = true)
            .performClick()
      }
      step("Check if Organisers Drop Down is hidden") {
        composeTestRule
            .onNodeWithTag("multiSelectExposedDropdownMenu", useUnmergedTree = true)
            .assertIsNotDisplayed()
      }
    }
  }

  @Test
  fun successfulPressOnLocationDropDownInputMenu() = run {
    ComposeScreen.onComposeScreen<CreateEventScreen>(composeTestRule) {
      step("Click on Location text field") {
        locationDropDownMenuTextField {
          assertIsDisplayed()
          performClick()
        }
      }
      step("Check if Location Drop Down Menu is displayed") {
        composeTestRule
            .onNodeWithTag("locationExposedDropDownMenu", useUnmergedTree = true)
            .assertIsDisplayed()
      }
      step("Click on location text field toggle icon") {
        composeTestRule
            .onNodeWithTag("locationDropDownMenuToggleIcon", useUnmergedTree = true)
            .performClick()
      }
      step("Check if Location Drop Down Menu is hidden") {
        composeTestRule
            .onNodeWithTag("locationExposedDropDownMenu", useUnmergedTree = true)
            .assertIsNotDisplayed()
      }
    }
  }

  @Test
  fun successfulPressOnDropDownInputMenuTicketName() = run {
    ComposeScreen.onComposeScreen<CreateEventScreen>(composeTestRule) {
      step("Click on ticket name text field") {
        ticketNameDropDownMenuTextField {
          performScrollTo()
          assertIsDisplayed()
          performClick()
        }
      }
      step("Check if ticketName drop down menu is displayed") {
        composeTestRule
            .onNodeWithTag("exposedDropDownMenu", useUnmergedTree = true)
            .assertIsDisplayed()
      }
      step("Click on ticket name toggle icon") {
        composeTestRule.onNodeWithTag("ticketNameToggleIcon", useUnmergedTree = true).performClick()
      }
      step("Check if ticketName drop down menu is hidden") {
        composeTestRule
            .onNodeWithTag("exposedDropDownMenu", useUnmergedTree = true)
            .assertIsNotDisplayed()
      }
    }
  }

  @Test
  fun successfulPressOnDropDownInputMenuEventCategory() = run {
    ComposeScreen.onComposeScreen<CreateEventScreen>(composeTestRule) {
      step("Click on Event Category Drop Down Menu") {
        eventCategoryDropDown {
          performScrollTo()
          assertIsDisplayed()
          performClick()
        }
      }
      step("Check if Event Category Drop Down Menu is displayed") {
        composeTestRule
            .onNodeWithTag("exposedDropDownMenu", useUnmergedTree = true)
            .assertIsDisplayed()
      }

      step("Click on Event Category Toggle Icon") {
        composeTestRule
            .onNodeWithTag("eventCategoryToggleIcon", useUnmergedTree = true)
            .performClick()
      }
      step("Check if Event Category Drop Down Menu is hidden") {
        composeTestRule
            .onNodeWithTag("exposedDropDownMenu", useUnmergedTree = true)
            .assertIsNotDisplayed()
      }
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
      composeTestRule.waitForIdle()
      step("Check if success dialog box is displayed") {
        composeTestRule
            .onNodeWithTag("successDialogBox", useUnmergedTree = true)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag("DisplayText", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("DisplayTitle", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule
            .onNodeWithTag("dialogConfirmButton", useUnmergedTree = true)
            .assertIsDisplayed()
      }
      step("Click on confirm") {
        composeTestRule.onNodeWithTag("dialogConfirmButton", useUnmergedTree = true).performClick()
      }
      verify(exactly = 1) { mockCreateEventViewModel.resetStateAndSetAddEventSuccess(false) }
      verify(exactly = 1) { mockNavActions.goBack() }
      confirmVerified(mockNavActions)
    }
  }

  @Test
  fun failureDialogBoxDisplayedUponFailedPublishEvent() = run {
    ComposeScreen.onComposeScreen<CreateEventScreen>(composeTestRule) {
      step("Click on publish event") {
        publishEventButton {
          assertIsDisplayed()
          performClick()
        }
      }
      mockCreateEventUiState.value = mockCreateEventUiState.value.copy(showAddEventFailure = true)

      composeTestRule.waitForIdle()
      step("Check if failure dialog box is displayed") {
        composeTestRule
            .onNodeWithTag("failureDialogBox", useUnmergedTree = true)
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag("DisplayText", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("DisplayTitle", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule
            .onNodeWithTag("dialogConfirmButton", useUnmergedTree = true)
            .assertIsDisplayed()
      }
      step("Click on confirm") {
        composeTestRule.onNodeWithTag("dialogConfirmButton", useUnmergedTree = true).performClick()
      }
      verify(exactly = 1) { mockCreateEventViewModel.resetStateAndSetAddEventFailure(false) }
      verify(exactly = 1) { mockNavActions.goBack() }
      confirmVerified(mockNavActions)
    }
  }
}
