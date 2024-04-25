package com.github.se.eventradar.model

sealed class Resource<out T> {
  data class Success<out T>(val data: T) : Resource<T>()

  data class Failure<out T>(val throwable: Throwable) : Resource<T>()
}
