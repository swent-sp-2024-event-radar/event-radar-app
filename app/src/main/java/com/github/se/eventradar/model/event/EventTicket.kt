package com.github.se.eventradar.model.event

data class EventTicket(val name: String, val price: Double, val capacity: Int, val purchases: Int)

fun getEventTicket(name: String, price: Double, capacity: Int, purchases: Int): EventTicket {
  return EventTicket(name, price, capacity, purchases)
}
