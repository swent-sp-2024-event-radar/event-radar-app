package com.github.se.eventradar

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventDetailsViewModel
import com.github.se.eventradar.model.event.EventTicket
import com.github.se.eventradar.model.repository.event.IEventRepository
import com.github.se.eventradar.ui.MainActivity
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@OptIn(ExperimentalCoroutinesApi::class)
class UITest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule var hiltRule = HiltAndroidRule(this)

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  private val mockEvent =
      Event(
          eventName = "Event 1",
          eventPhoto = "",
          start = LocalDateTime.now(),
          end = LocalDateTime.now(),
          location = Location(0.0, 0.0, "Test Location"),
          description = "Test Description",
          ticket = EventTicket("Test Ticket", 0.0, 1),
          mainOrganiser = "1",
          organiserList = mutableListOf("Test Organiser"),
          attendeeList = mutableListOf("Test Attendee"),
          category = EventCategory.COMMUNITY,
          fireBaseID = "1")

  @Inject lateinit var eventRepository: IEventRepository

  private lateinit var viewModel: EventDetailsViewModel


  @Before
  fun testSetup() {
    hiltRule.inject()
  }

  @Test
  fun eventDetailsViewModelCorrectlyInstanced() = runTest {
    composeTestRule.clearAndSetContent {
      viewModel = EventDetailsViewModel.create(eventId = mockEvent.fireBaseID)
      assert(mockEvent.fireBaseID == viewModel.eventId)
    }
  }
}

// Solution from here :
// https://stackoverflow.com/questions/73191692/test-errormyactivity-has-already-set-content-if-you-have-populated-the-activit

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.clearAndSetContent(
    content: @Composable () -> Unit
) {
  (this.activity.findViewById<ViewGroup>(android.R.id.content)?.getChildAt(0) as? ComposeView)
      ?.setContent(content) ?: this.setContent(content)
}
