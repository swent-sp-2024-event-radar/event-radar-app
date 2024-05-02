package com.github.se.eventradar

import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.model.event.EventTicket
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class EventTest {
    private lateinit var event: Event

    @Before
    fun setUp() {
        event = Event(
            eventName = "Test Event",
            eventPhoto = "Test Photo",
            start = LocalDateTime.now(),
            end = LocalDateTime.now(),
            location = Location(0.0, 0.0, "Test Location"),
            description = "Test Description",
            ticket = EventTicket("Test Ticket", 0.0, 1),
            mainOrganiser = "Test Organiser",
            organiserList = mutableListOf("Test Organiser"),
            attendeeList = mutableListOf("Test Attendee"),
            category = EventCategory.COMMUNITY,
            fireBaseID = "Test ID"
        )
    }

    @Test
    fun testEventName() {
        assertEquals("Test Event", event.eventName)
    }

    @Test
    fun testEventPhoto() {
        assertEquals("Test Photo", event.eventPhoto)
    }

    @Test
    fun testStart() {
        val expected = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
        val actual = event.start.truncatedTo(ChronoUnit.MILLIS)
        assertEquals(expected, actual)
    }

    @Test
    fun testEnd() {
        val expected = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
        val actual = event.end.truncatedTo(ChronoUnit.MILLIS)
        assertEquals(expected, actual)
    }

    @Test
    fun testLocation() {
        assertEquals(Location(0.0, 0.0, "Test Location"), event.location)
    }

    @Test
    fun testDescription() {
        assertEquals("Test Description", event.description)
    }

    @Test
    fun testTicket() {
        assertEquals(EventTicket("Test Ticket", 0.0, 1), event.ticket)
    }

    @Test
    fun testMainOrganiser() {
        assertEquals("Test Organiser", event.mainOrganiser)
    }

    @Test
    fun testOrganiserList() {
        assertEquals(mutableListOf("Test Organiser"), event.organiserList)
    }

    @Test
    fun testAttendeeList() {
        assertEquals(mutableListOf("Test Attendee"), event.attendeeList)
    }

    @Test
    fun testCategory() {
        assertEquals(EventCategory.COMMUNITY, event.category)
    }

    @Test
    fun testFireBaseID() {
        assertEquals("Test ID", event.fireBaseID)
    }

    @Test
    fun testToMap() {
        val expected = mapOf(
            "name" to "Test Event",
            "photo_url" to "Test Photo",
            "start" to event.start.toString(),
            "end" to event.end.toString(),
            "location_lat" to 0.0,
            "location_lng" to 0.0,
            "location_name" to "Test Location",
            "description" to "Test Description",
            "ticket_name" to "Test Ticket",
            "ticket_price" to 0.0,
            "ticket_quantity" to 1,
            "main_organiser" to "Test Organiser",
            "organisers_list" to listOf("Test Organiser"),
            "attendees_list" to listOf("Test Attendee"),
            "category" to "COMMUNITY"
        )
        assertEquals(expected, event.toMap())
    }

    @Test
    fun testEventConstructor() {
        val eventName = "Test Event"
        val eventPhoto = "Test Photo"
        val start = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
        val end = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
        val location = Location(0.0, 0.0, "Test Location")
        val description = "Test Description"
        val ticket = EventTicket("Test Ticket", 0.0, 1)
        val mainOrganiser = "Test Organiser"
        val organiserList = mutableListOf("Test Organiser")
        val attendeeList = mutableListOf("Test Attendee")
        val category = EventCategory.COMMUNITY
        val fireBaseID = "Test ID"

        val event = Event(
            eventName = eventName,
            eventPhoto = eventPhoto,
            start = start,
            end = end,
            location = location,
            description = description,
            ticket = ticket,
            mainOrganiser = mainOrganiser,
            organiserList = organiserList,
            attendeeList = attendeeList,
            category = category,
            fireBaseID = fireBaseID
        )

        assertEquals(eventName, event.eventName)
        assertEquals(eventPhoto, event.eventPhoto)
        assertEquals(start, event.start)
        assertEquals(end, event.end)
        assertEquals(location, event.location)
        assertEquals(description, event.description)
        assertEquals(ticket, event.ticket)
        assertEquals(mainOrganiser, event.mainOrganiser)
        assertEquals(organiserList, event.organiserList)
        assertEquals(attendeeList, event.attendeeList)
        assertEquals(category, event.category)
        assertEquals(fireBaseID, event.fireBaseID)
    }

    @Test
    fun testEventSecondaryConstructor() {
        val map = mapOf(
            "name" to "Test Event",
            "photo_url" to "Test Photo",
            "start" to LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS).toString(),
            "end" to LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS).toString(),
            "location_lat" to 0.0,
            "location_lng" to 0.0,
            "location_name" to "Test Location",
            "description" to "Test Description",
            "ticket_name" to "Test Ticket",
            "ticket_price" to 0L,
            "ticket_quantity" to 1L,
            "main_organiser" to "Test Organiser",
            "organisers_list" to listOf("Test Organiser"),
            "attendees_list" to listOf("Test Attendee"),
            "category" to "COMMUNITY"
        )
        val id = "Test ID"

        val event = Event(map, id)

        assertEquals("Test Event", event.eventName)
        assertEquals("Test Photo", event.eventPhoto)
        assertEquals(LocalDateTime.parse(map["start"] as String), event.start)
        assertEquals(LocalDateTime.parse(map["end"] as String), event.end)
        assertEquals(Location(0.0, 0.0, "Test Location"), event.location)
        assertEquals("Test Description", event.description)
        assertEquals(EventTicket("Test Ticket", 0.0, 1), event.ticket)
        assertEquals("Test Organiser", event.mainOrganiser)
        assertEquals(listOf("Test Organiser"), event.organiserList)
        assertEquals(listOf("Test Attendee"), event.attendeeList)
        assertEquals(EventCategory.COMMUNITY, event.category)
        assertEquals("Test ID", event.fireBaseID)
    }
}