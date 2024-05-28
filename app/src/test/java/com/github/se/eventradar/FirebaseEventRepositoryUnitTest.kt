package com.github.se.eventradar

import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.repository.event.FirebaseEventRepository
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.unmockkAll
import java.time.LocalDateTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class FirebaseEventRepositoryUnitTest {
  @RelaxedMockK lateinit var eventRef: CollectionReference
  @RelaxedMockK lateinit var mockDocumentSnapshot: DocumentSnapshot
  @RelaxedMockK lateinit var mockQuerySnapshot: QuerySnapshot
  @RelaxedMockK lateinit var mockDb: FirebaseFirestore

  private lateinit var firebaseEventRepository: FirebaseEventRepository

  @Before
  fun setUp() {
    eventRef = mockk()
    mockDocumentSnapshot = mockk()
    mockQuerySnapshot = mockk()

    mockDb = mockk()

    every { mockDb.collection("events") } returns eventRef

    firebaseEventRepository = FirebaseEventRepository(db = mockDb)
  }

  @After
  fun tearDown() {
    unmockkAll()
  }

  @Test
  fun `observeAllEvents emits Resource_Success with events on snapshot changes`() = runTest {
    val mockEventMap =
        mapOf(
            "name" to "Sample Event",
            "photo_url" to "http://example.com/photo.jpg",
            "start" to "2021-01-01T00:00",
            "end" to "2021-01-02T00:00",
            "location_lat" to 10.0,
            "location_lng" to 20.0,
            "location_name" to "Event Venue",
            "description" to "Description of the event",
            "ticket_name" to "General Admission",
            "ticket_price" to 100.0,
            "ticket_capacity" to 100L,
            "ticket_purchases" to 10L,
            "main_organiser" to "Organiser Name",
            "organisers_list" to listOf("Org1", "Org2"),
            "attendees_list" to listOf("User1", "User2"),
            "category" to "MUSIC")

    every { mockDocumentSnapshot.id } returns "event1"
    every { mockDocumentSnapshot.data } returns mockEventMap
    every { mockQuerySnapshot.documents } returns listOf(mockDocumentSnapshot)

    val slot = slot<EventListener<QuerySnapshot>>()
    val mockListenerRegistration = mockk<ListenerRegistration>()
    every { mockListenerRegistration.remove() } just Runs

    every { eventRef.addSnapshotListener(capture(slot)) } answers
        {
          slot.captured.onEvent(mockQuerySnapshot, null)
          mockListenerRegistration
        }
    val results = mutableListOf<Resource<List<Event>>>()
    val job = launch { firebaseEventRepository.observeAllEvents().collect { results.add(it) } }
    delay(500)

    assert(results.size == 1)
    assert(results.first() is Resource.Success)
    val events = (results.first() as Resource.Success<List<Event>>).data
    assert(1 == events.size)
    with(events.first()) {
      assert("Sample Event" == eventName)
      assert("http://example.com/photo.jpg" == eventPhoto)
      assert(LocalDateTime.parse("2021-01-01T00:00") == start)
      assert("Event Venue" == location.address)
    }

    job.cancel()
  }

  @Test
  fun `observeAllEvents emits Resource_Failure on snapshot listener error`() = runTest {
    val errorMessage = "Network error"
    val mockFirestoreException = mockk<FirebaseFirestoreException>()
    every { mockFirestoreException.message } returns errorMessage

    val slot = slot<EventListener<QuerySnapshot>>()
    val mockListenerRegistration = mockk<ListenerRegistration>()
    every { mockListenerRegistration.remove() } just Runs

    every { eventRef.addSnapshotListener(capture(slot)) } answers
        {
          slot.captured.onEvent(null, mockFirestoreException)
          mockListenerRegistration
        }

    val results = mutableListOf<Resource<List<Event>>>()
    val job = launch { firebaseEventRepository.observeAllEvents().collect { results.add(it) } }
    delay(500)

    assert(results.size == 1)
    assert(results.first() is Resource.Failure)
    val error = (results.first() as Resource.Failure).throwable.message
    assert(error == "Error listening to event updates: $errorMessage")

    job.cancel()
  }

  @Test
  fun `observeUpcomingEvents emits Resource_Success with relevant events`() = runTest {
    val userId = "user1"
    val mockEventMap =
        mapOf(
            "name" to "User Specific Event",
            "photo_url" to "http://example.com/photo.jpg",
            "start" to "2025-01-01T00:00",
            "end" to "2025-01-02T00:00",
            "location_lat" to 10.0,
            "location_lng" to 20.0,
            "location_name" to "Event Venue",
            "description" to "Description of the event",
            "ticket_name" to "General Admission",
            "ticket_price" to 100.0,
            "ticket_capacity" to 100L,
            "ticket_purchases" to 10L,
            "main_organiser" to "Organiser Name",
            "organisers_list" to listOf("Org1", "Org2"),
            "attendees_list" to listOf(userId),
            "category" to "MUSIC")

    every { mockDocumentSnapshot.id } returns "event2"
    every { mockDocumentSnapshot.data } returns mockEventMap
    every { mockQuerySnapshot.documents } returns listOf(mockDocumentSnapshot)

    val queryMock = mockk<Query>()
    every { eventRef.whereArrayContains("attendees_list", userId) } returns queryMock

    val slot = slot<EventListener<QuerySnapshot>>()
    val mockListenerRegistration = mockk<ListenerRegistration>()
    every { mockListenerRegistration.remove() } just Runs

    every { queryMock.addSnapshotListener(capture(slot)) } answers
        {
          slot.captured.onEvent(mockQuerySnapshot, null)
          mockListenerRegistration
        }

    val results = mutableListOf<Resource<List<Event>>>()
    val job = launch {
      firebaseEventRepository.observeUpcomingEvents(userId).collect { results.add(it) }
    }
    delay(500)

    assert(results.size == 1)
    assert(results.first() is Resource.Success)
    assert((results.first() as Resource.Success).data.any { it.eventName == "User Specific Event" })

    job.cancel()
  }

  @Test
  fun `observeUpcomingEvents emits Resource_Failure on query error`() = runTest {
    val userId = "user1"
    val errorMessage = "Query error"
    val mockFirestoreException = mockk<FirebaseFirestoreException>()
    every { mockFirestoreException.message } returns errorMessage

    val queryMock = mockk<Query>()
    every { eventRef.whereArrayContains("attendees_list", userId) } returns queryMock

    val slot = slot<EventListener<QuerySnapshot>>()
    val mockListenerRegistration = mockk<ListenerRegistration>()
    every { mockListenerRegistration.remove() } just Runs

    every { queryMock.addSnapshotListener(capture(slot)) } answers
        {
          slot.captured.onEvent(null, mockFirestoreException)
          mockListenerRegistration
        }

    val results = mutableListOf<Resource<List<Event>>>()
    val job = launch {
      firebaseEventRepository.observeUpcomingEvents(userId).collect { results.add(it) }
    }
    delay(500)

    assert(results.isNotEmpty())
    assert(results.first() is Resource.Failure)
    assert(
        "Error listening to upcoming event updates: $errorMessage" ==
            (results.first() as Resource.Failure).throwable.message)

    job.cancel()
  }
}
