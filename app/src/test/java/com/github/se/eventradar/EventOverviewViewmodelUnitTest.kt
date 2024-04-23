package com.github.se.eventradar

import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventDetailsViewModel
import com.github.se.eventradar.model.event.EventTicket
import org.junit.Test
import java.time.LocalDateTime

class EventOverviewViewmodelUnitTest {
    @Test
    suspend fun testEventOverviewUiInitialisation() {
        EventDetailsViewModel().getEventData()
        assert(
            EventDetailsViewModel().uiState.value
                    ==
                    EventDetailsViewModel.EventUiState(
                        eventName = "Event Test",
                        eventPhoto = "ueuieib",
                        start = LocalDateTime.parse("2024-12-31-T23:00:00"),
                        end = LocalDateTime.parse("2025-01-01-T05:00:00"),
                        location = Location(38.92, 78.78, "Route Cantonale 51, 1024, Ecublens"),
                        description = "Come Party Hard like there is no tomorrow",
                        ticket = EventTicket("Standard", 0.0, 555),
                        contact = "jg@joytigoel.com",
                        category = EventCategory.COMMUNITY
                    )
        )
    }
}
