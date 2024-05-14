package com.github.se.eventradar.model

object ConversionUtils {
  fun convertToMutableListOfStrings(data: Any?): MutableList<String> {
    return when (data) {
      is List<*> -> data.filterIsInstance<String>().toMutableList()
      is MutableList<*> -> data.filterIsInstance<String>().toMutableList()
      is String -> mutableListOf(data)
      else -> mutableListOf()
    }
  }
}