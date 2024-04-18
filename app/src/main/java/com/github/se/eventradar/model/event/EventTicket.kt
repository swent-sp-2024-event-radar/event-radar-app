package com.github.se.eventradar.model.event

data class EventTicket(
    val name: String,
    val price: Double,
    val quantity: Int
)

fun getEventTicket(name: String, price: Double, quantity: Int): EventTicket {
    return EventTicket(name, price, quantity)
}