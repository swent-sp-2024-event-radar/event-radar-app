package com.github.se.eventradar.model.event

data class EventTicket(val name: String, val price: Double, val capacity: Int)

fun getEventTicket(name: String, price: Double, capacity: Int): EventTicket {
  return EventTicket(name, price, capacity)
}
