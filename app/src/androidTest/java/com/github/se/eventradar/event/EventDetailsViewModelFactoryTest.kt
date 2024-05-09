package com.github.se.eventradar.event

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.eventradar.model.event.EventDetailsViewModel
import com.github.se.eventradar.model.repository.event.IEventRepository
import com.github.se.eventradar.model.repository.event.MockEventRepository
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/*@HiltAndroidTest*/
@RunWith(AndroidJUnit4::class)
class EventDetailsViewModelFactoryTest :
    TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  /*@get:Rule(order = 0)
  val rule = HiltAndroidRule(this)*/

  @get:Rule(order = 1) val composeTestRule = createComposeRule()

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  private val eventId = "tdjWMT9Eon2ROTVakQb"

  /*@Inject
  lateinit var eventRepository: IEventRepository

  private lateinit var viewModel : EventDetailsViewModel

  @get:Rule val mockkRule = MockKRule(this)*/

  private val eventRepository: IEventRepository = MockEventRepository()
  @RelaxedMockK lateinit var factory: EventDetailsViewModel.Factory

  private lateinit var viewModel: EventDetailsViewModel

  @Before
  fun setup() {
    every { factory.create(any()) } answers { EventDetailsViewModel(eventRepository, firstArg()) }
  }

  @Test
  fun testViewModelCreation() {
    viewModel = factory.create("testEventId")
    verify { factory.create("testEventId") }
  }
}
