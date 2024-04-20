package com.github.se.eventradar.model.repository.event

import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.getEventCategory
import com.github.se.eventradar.model.event.getEventTicket
import com.github.se.eventradar.model.getLocation
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class FirebaseEventRepository @Inject constructor() : IEventRepository {
  private val db: FirebaseFirestore = Firebase.firestore
  private val eventRef: CollectionReference = db.collection("events")

  override suspend fun getEvents(): Resource<List<Event>> {
    val resultDocument = eventRef.get().await()

    return try {
      val events =
          resultDocument.documents.map { document ->
            Event(
                eventName = document.data?.get("name") as String,
                eventPhoto = document.data?.get("photo_url") as String,
                start = getLocalDateTime(document.data?.get("start") as String),
                end = getLocalDateTime(document.data?.get("end") as String),
                location =
                    getLocation(
                        document.data?.get("location_name") as String,
                        document.data?.get("location_lat") as Double,
                        document.data?.get("location_lng") as Double,
                    ),
                description = document.data?.get("description") as String,
                ticket =
                    getEventTicket(
                        document.data?.get("ticket_name") as String,
                        document.data?.get("ticket_price") as Double,
                        document.data?.get("ticket_quantity") as Int,
                    ),
                contact = "", // TODO: replace with reference to host
                organiserList = getSetOfStrings(document.data?.get("organisers_list")),
                attendeeList = getSetOfStrings(document.data?.get("attendees_list")),
                category = getEventCategory(document.data?.get("category") as String),
                fireBaseID = document.id)
          }
      Resource.Success(events)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun getEvent(id: String): Resource<Event?> {
    val resultDocument = eventRef.document(id).get().await()

    return try {
      val event =
          Event(
              eventName = resultDocument.data?.get("name") as String,
              eventPhoto = resultDocument.data?.get("photo_url") as String,
              start = getLocalDateTime(resultDocument.data?.get("start") as String),
              end = getLocalDateTime(resultDocument.data?.get("end") as String),
              location =
                  getLocation(
                      resultDocument.data?.get("location_name") as String,
                      resultDocument.data?.get("location_lat") as Double,
                      resultDocument.data?.get("location_lng") as Double,
                  ),
              description = resultDocument.data?.get("description") as String,
              ticket =
                  getEventTicket(
                      resultDocument.data?.get("ticket_name") as String,
                      resultDocument.data?.get("ticket_price") as Double,
                      resultDocument.data?.get("ticket_quantity") as Int,
                  ),
              contact = "", // TODO: replace with reference to host
              organiserList = getSetOfStrings(resultDocument.data?.get("organisers_list")),
              attendeeList = getSetOfStrings(resultDocument.data?.get("attendees_list")),
              category = getEventCategory(resultDocument.data?.get("category") as String),
              fireBaseID = resultDocument.id)
      Resource.Success(event)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun addEvent(event: Event): Resource<Boolean> {

    return try {
      eventRef.add(event).await()
      Resource.Success(true)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun updateEvent(event: Event): Resource<Boolean> {
    val eventMap =
        hashMapOf(
            "name" to event.eventName,
            "photo_url" to event.eventPhoto,
            "start" to event.start.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
            "end" to event.end.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
            "location_name" to event.location.address,
            "location_lat" to event.location.latitude,
            "location_lng" to event.location.longitude,
            "description" to event.description,
            "ticket_name" to event.ticket.name,
            "ticket_price" to event.ticket.price,
            "ticket_quantity" to event.ticket.capacity,
            "organisers_list" to event.organiserList.toList(),
            "attendees_list" to event.attendeeList.toList(),
            "category" to event.category.name)

    return try {
      eventRef.document(event.fireBaseID).update(eventMap as Map<String, Any>).await()
      Resource.Success(true)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  override suspend fun deleteEvent(event: Event): Resource<Boolean> {
    return try {
      eventRef.document(event.fireBaseID).delete().await()
      Resource.Success(true)
    } catch (e: Exception) {
      Resource.Failure(e)
    }
  }

  companion object {
    fun getLocalDateTime(dateTime: String): LocalDateTime {
      val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
      return LocalDateTime.parse(dateTime, formatter)
    }

    fun getSetOfStrings(data: Any?): Set<String> {
      return when (data) {
        is List<*> -> data.filterIsInstance<String>().toSet()
        is String -> setOf(data)
        else -> emptySet()
      }
    }
  }
}
