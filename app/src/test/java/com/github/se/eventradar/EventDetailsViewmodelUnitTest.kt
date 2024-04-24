package com.github.se.eventradar

import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventDetailsViewModel
import com.github.se.eventradar.model.event.EventTicket
import java.time.LocalDateTime
import org.junit.Test

class EventDetailsViewmodelUnitTest {
  @Test
  suspend fun testEventDetailsUiInitialisation() {
    EventDetailsViewModel("pSjt3r3O8TddpWxRhGK5").getEventData()
    assert(
        EventDetailsViewModel().uiState.value ==
            EventDetailsViewModel.EventUiState(
                eventName = "Event Test",
                eventPhoto = "hd3idd3bui",
                start = LocalDateTime.parse("2024-12-31-T23:00:00"),
                end = LocalDateTime.parse("2025-01-01-T05:00:00"),
                location = Location(38.92, 78.78, "Chemin de la colline 11, 1024, ecublens"),
                description = "Come Party Hard like there is no tomorrow",
                ticket = EventTicket("Standard", 0.0, 555),
                contact = "jg@joytigoel.com",
                category = EventCategory.COMMUNITY))
  }
}
