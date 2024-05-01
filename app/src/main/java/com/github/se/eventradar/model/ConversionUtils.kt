package com.github.se.eventradar.model

object ConversionUtils {
  fun convertToMutableSetOfStrings(data: Any?): MutableSet<String> {
    return when (data) {
      is List<*> -> data.filterIsInstance<String>().toMutableSet()
      is MutableSet<*> -> data.filterIsInstance<String>().toMutableSet()
      is String -> mutableSetOf(data)
      else -> mutableSetOf()
    }
  }
}
