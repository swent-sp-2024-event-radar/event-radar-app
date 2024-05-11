package com.github.se.eventradar.map

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventList
import com.github.se.eventradar.model.event.EventTicket
import com.github.se.eventradar.screens.HomeScreen
import com.github.se.eventradar.ui.home.HomeScreen
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.viewmodel.EventsOverviewUiState
import com.github.se.eventradar.viewmodel.EventsOverviewViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

private const val LAUNCH_TIMEOUT = 5000L
private const val PACKAGE = "com.github.se.eventradar"

@RunWith(AndroidJUnit4::class)
class MapTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @RelaxedMockK lateinit var mockEventsOverviewViewModel: EventsOverviewViewModel

  private val sampleEventList =
      MutableStateFlow(
          EventsOverviewUiState(
              eventList =
                  EventList(
                      listOf(
                          Event(
                              eventName = "Test Event",
                              eventPhoto = "",
                              start = LocalDateTime.now(),
                              end = LocalDateTime.now(),
                              location = Location(46.5188, 6.5593, "Test Location"),
                              description = "Test Description",
                              ticket = EventTicket("Test Ticket", 0.0, 1),
                              mainOrganiser = "1",
                              organiserSet = mutableSetOf("Test Organiser"),
                              attendeeSet = mutableSetOf("Test Attendee"),
                              category = EventCategory.COMMUNITY,
                              fireBaseID = "1")))))

  private lateinit var uiDevice: UiDevice

  @Before
  fun testSetup() {
    every { mockEventsOverviewViewModel.getEvents() } returns Unit
    every { mockEventsOverviewViewModel.getUpcomingEvents() } returns Unit
    every { mockEventsOverviewViewModel.uiState } returns sampleEventList
    composeTestRule.setContent { HomeScreen(mockEventsOverviewViewModel, mockNavActions) }

    uiDevice = UiDevice.getInstance(getInstrumentation())
  }

  @Test
  fun testMapViewNavigatesToEventDetailsOnClick() = run {
    onComposeScreen<HomeScreen>(composeTestRule) {
      sampleEventList.value =
          sampleEventList.value.copy(
              isFilterDialogOpen =
                  true) // TODO: update this to FALSE once search bar fixes are merged
      step("Click on view toggle fab") {
        viewToggleFab {
          assertIsDisplayed()
          performClick()
        }
      }

      // Update the UI state to reflect the change
      sampleEventList.value = sampleEventList.value.copy(viewList = false)
      step("Check if map is displayed") { map { assertIsDisplayed() } }

      val map = uiDevice.findObject(UiSelector().resourceId("map"))
      val eventMarker = uiDevice.findObject(UiSelector().resourceId("map").childSelector(UiSelector().instance(1)))
      eventMarker.click()
    }
  }
}
