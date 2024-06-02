package com.github.se.eventradar.viewmodel.user

import com.github.se.eventradar.viewmodel.CountryCode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun isValidPhoneNumber(phoneNumber: String, countryCode: CountryCode): Boolean {
  return phoneNumber.length == countryCode.numberLength
}

fun isValidDate(date: String): Boolean {
  val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
  format.isLenient = false
  return try {
    val parsedDate = format.parse(date)
    val currentDate = Date()
    val calendar = Calendar.getInstance()
    calendar.time = currentDate
    calendar.add(Calendar.YEAR, -130) // set limit to 130 years ago
    val pastDateLimit = calendar.time
    // return true if date not null and date is between today and past limit
    (parsedDate != null && parsedDate.before(currentDate) && parsedDate.after(pastDateLimit))
  } catch (e: Exception) {
    false
  }
}
