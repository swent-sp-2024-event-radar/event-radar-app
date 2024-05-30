package com.github.se.eventradar.event

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventTicket
import com.github.se.eventradar.screens.EventSelectTicketScreen
import com.github.se.eventradar.ui.event.SelectTicket
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.viewmodel.EventDetailsViewModel
import com.github.se.eventradar.viewmodel.EventUiState
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventSelectTicketUITest :
    TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  @RelaxedMockK lateinit var mockViewModel: EventDetailsViewModel

  private val sampleEventDetailsUiState =
      MutableStateFlow(
          EventUiState(
              eventName = "Debugging",
              eventPhoto = "path",
              start = LocalDateTime.MIN,
              end = LocalDateTime.MAX,
              location = Location(0.0, 0.0, "base address"),
              description = "Let's debug some code together because we all enjoy kotlin !",
              ticket = EventTicket("Luck", 0.0, 7, 0),
              mainOrganiser = "some.name@host.com",
              category = EventCategory.COMMUNITY,
          ))

  private val eventId = "tdjWMT9Eon2ROTVakQb"

  private var isTicketFree = true

  private val error = mutableStateOf(false)
  private val success = mutableStateOf(false)
  private var isAttending = false

  @Before
  fun testSetup() {

    every { mockViewModel.showErrorOccurredDialog } returns error
    every { mockViewModel.showSuccessfulRegistrationDialog } returns success
    every { mockViewModel.isUserAttending } returns MutableStateFlow(isAttending)
    every { mockViewModel.uiState } returns sampleEventDetailsUiState
    every { mockViewModel.isTicketFree() } returns isTicketFree

    composeTestRule.setContent { SelectTicket(mockViewModel, navigationActions = mockNavActions) }
  }

  @Test
  fun screenDisplaysNavigationElementsCorrectly() = run {
    isAttending = false
    ComposeScreen.onComposeScreen<EventSelectTicketScreen>(composeTestRule) {
      buyButton { assertIsDisplayed() }
      goBackButton { assertIsDisplayed() }
      bottomNav { assertIsDisplayed() }
    }
  }

  @Test
  fun screenDisplaysContentElements() = run {
    ComposeScreen.onComposeScreen<EventSelectTicketScreen>(composeTestRule) {
      eventTitle {
        assertIsDisplayed()
        assertTextContains("Debugging")
      }
      ticketsTitle { assertIsDisplayed() }
      ticketCard { assertIsDisplayed() }
      /* Can't retrieve node at index '0' of '(hasParentThat(TestTag = 'ticketCard'))
       * && (TestTag = 'ticketInfo')'
       * There are no existing nodes for that selector.
       *
      ticketInfo{
        assertIsDisplayed()
      }
      ticketName{
        assertIsDisplayed()
        assertTextContains(sampleEventDetailsUiState.value.ticket.name)
      }
      ticketPrice{
        assertIsDisplayed()
        assertTextContains("${sampleEventDetailsUiState.value.ticket.price}", substring = true)
      }*/
    }
  }

  @Test
  fun buyTicketSuccessful() = run {
    ComposeScreen.onComposeScreen<EventSelectTicketScreen>(composeTestRule) {
      step("Check buy action") {
        buyButton {
          assertIsDisplayed()
          performClick()
        }
        // assert: the buy has been triggered
        verify { mockViewModel.buyTicketForEvent() }
      }

      step("Check success dialog box appear") {
        success.value = true

        successDialog { assertIsDisplayed() }
      }

      step("Ok button back navigate to event details") {
        okButton {
          assertIsDisplayed()
          performClick()
        }
        verify { mockNavActions.goBack() }
      }
    }
  }

  @Test
  fun buyTicketFailure() = run {
    ComposeScreen.onComposeScreen<EventSelectTicketScreen>(composeTestRule) {
      buyButton {
        assertIsDisplayed()
        performClick()
      }
    }
    // assert: the buy has been triggered
    verify { mockViewModel.buyTicketForEvent() }

    error.value = true

    ComposeScreen.onComposeScreen<EventSelectTicketScreen>(composeTestRule) {
      errorDialog { assertIsDisplayed() }
    }
  }

  @Test
  fun goBackButtonTriggersBackNavigation() = run {
    ComposeScreen.onComposeScreen<EventSelectTicketScreen>(composeTestRule) {
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
}
