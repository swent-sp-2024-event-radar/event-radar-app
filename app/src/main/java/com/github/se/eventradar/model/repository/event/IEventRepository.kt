package com.github.se.eventradar.model.repository.event

import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.event.Event
import kotlinx.coroutines.flow.Flow

interface IEventRepository {
  suspend fun getEvents(): Resource<List<Event>>

  suspend fun getEvent(id: String): Resource<Event?>

  suspend fun getUniqueEventId(): Resource<String>

  //  suspend fun cleanExpiredEvents(): Resource<Unit>

  suspend fun addEvent(event: Event): Resource<Unit>

  //  suspend fun addEventExpired(document: DocumentSnapshot): Resource<Unit>

  suspend fun updateEvent(event: Event): Resource<Unit>

  suspend fun deleteEvent(event: Event): Resource<Unit>

  //  suspend fun deleteEventByID(fireBaseID: String): Resource<Unit>

  suspend fun getEventsByIds(ids: List<String>): Resource<List<Event>>

  fun observeAllEvents(): Flow<Resource<List<Event>>>

  fun observeUpcomingEvents(userId: String): Flow<Resource<List<Event>>>
}
