package com.github.se.eventradar.map

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MapViewModel : ViewModel() {
  private val db = Firebase.firestore
}
