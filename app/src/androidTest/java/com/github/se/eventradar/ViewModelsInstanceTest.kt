package com.github.se.eventradar

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.github.se.eventradar.component.clearAndSetContent
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventTicket
import com.github.se.eventradar.ui.MainActivity
import com.github.se.eventradar.viewmodel.ChatViewModel
import com.github.se.eventradar.viewmodel.EventDetailsViewModel
import com.github.se.eventradar.viewmodel.ProfileViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.junit4.MockKRule
import java.time.LocalDateTime
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ViewModelInstanceTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule var hiltRule = HiltAndroidRule(this)

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  private val mockEvent =
      Event(
          eventName = "Event 1",
          eventPhoto = "",
          start = LocalDateTime.now(),
          end = LocalDateTime.now(),
          location = Location(0.0, 0.0, "Test Location"),
          description = "Test Description",
          ticket = EventTicket("Test Ticket", 0.0, 1, 0),
          mainOrganiser = "1",
          organiserList = mutableListOf("Test Organiser"),
          attendeeList = mutableListOf("Test Attendee"),
          category = EventCategory.COMMUNITY,
          fireBaseID = "1")

  private val mockFriendId = "2"
  private val mockOpponentId = "3"

  private lateinit var eventDetailsViewModel: EventDetailsViewModel
  private lateinit var chatViewModel: ChatViewModel
  private lateinit var viewFriendsProfileViewModel: ProfileViewModel

  @Before
  fun testSetup() {
    hiltRule.inject()
    composeTestRule.clearAndSetContent<MainActivity> {
      eventDetailsViewModel = EventDetailsViewModel.create(eventId = mockEvent.fireBaseID)
      chatViewModel = ChatViewModel.create(opponentId = mockOpponentId)
      viewFriendsProfileViewModel = ProfileViewModel.create(userId = mockFriendId)
    }
  }

  @Test
  fun eventDetailsViewModelCorrectlyInstanced() = runTest {
    composeTestRule.runOnIdle {
      assertThat(eventDetailsViewModel.eventId, `is`(equalTo(mockEvent.fireBaseID)))
    }
  }

  @Test
  fun chatViewModelCorrectlyInstanced() = runTest {
    composeTestRule.runOnIdle {
      assertThat(chatViewModel.opponentId, `is`(equalTo(mockOpponentId)))
    }
  }

  @Test
  fun viewFriendsProfileViewModelCorrectlyInstanced() = runTest {
    composeTestRule.runOnIdle {
      assertThat(viewFriendsProfileViewModel.userId, `is`(equalTo(mockFriendId)))
    }
  }
}
