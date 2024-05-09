package com.github.se.eventradar.model.repository.event

import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.event.Event
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseEventRepository : IEventRepository {
  private val db: FirebaseFirestore = Firebase.firestore
  private val eventRef: CollectionReference = db.collection("events")

  override suspend fun getEvents(): Resource<List<Event>> {
    val resultDocument = eventRef.get().await()

    return try {
      val events = resultDocument.documents.map { document -> Event(document.data!!, document.id) }
      Resource.Success(events)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun getEvent(id: String): Resource<Event?> {
    val resultDocument = eventRef.document(id).get().await()

    return try {
      val event = Event(resultDocument.data!!, resultDocument.id)
      Resource.Success(event)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun addEvent(event: Event): Resource<Unit> {

    return try {
      eventRef.add(event).await()
      Resource.Success(Unit)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun updateEvent(event: Event): Resource<Unit> {
    val eventMap = event.toMap()

    return try {
      eventRef.document(event.fireBaseID).update(eventMap as Map<String, Any>).await()
      Resource.Success(Unit)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun deleteEvent(event: Event): Resource<Unit> {
    return try {
      eventRef.document(event.fireBaseID).delete().await()
      Resource.Success(Unit)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun getEventsByIds(ids: List<String>): Resource<List<Event>> {
    return try {
      val events = mutableListOf<Event>()
      for (id in ids) {
        val document = eventRef.document(id).get().await()
        if (document.exists()) {
          events.add(Event(document.data!!, document.id))
        } else {
          return Resource.Failure(Exception("Event with id $id is missing"))
        }
      }
      Resource.Success(events)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override fun observeEvents(): Flow<Resource<List<Event>>> = callbackFlow { // to be tested
    val listener =
        eventRef.addSnapshotListener { snapshot, error ->
          if (error != null) {
            trySend(
                Resource.Failure(Exception("Error listening to event updates: ${error.message}")))
            return@addSnapshotListener
          }
          val events = snapshot?.documents?.mapNotNull { Event(it.data!!, it.id) }
          trySend(Resource.Success(events ?: listOf()))
        }
    awaitClose { listener.remove() }
  }
}
