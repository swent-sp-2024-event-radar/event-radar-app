package com.github.se.eventradar.qrCode

import android.graphics.Bitmap
import android.graphics.Color
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayOutputStream
import java.util.*

class QrCodeGenerator {

  fun qrCodeProcess(db: FirebaseFirestore, userId: String) {
    uploadBitmapToFirestore(generateQRCode(userId), userId, db)
  }

  private fun generateQRCode(userId: String, width: Int = 512, height: Int = 512): Bitmap {
    // Create a map of hints for the QR code generation
    val hints: MutableMap<EncodeHintType, Any> = EnumMap(EncodeHintType::class.java)
    hints[EncodeHintType.MARGIN] = 0 // Adjust margin as needed

    // Create a QRCodeWriter instance
    val writer = QRCodeWriter()

    // Generate the QR code matrix
    val bitMatrix = writer.encode(userId, BarcodeFormat.QR_CODE, width, height, hints)

    // Convert the QR code matrix to pixel data
    val pixels = IntArray(width * height)
    for (y in 0 until height) {
      val offset = y * width
      for (x in 0 until width) {
        pixels[offset + x] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
      }
    }
    //  Create a Bitmap from the pixel data
    return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
      setPixels(pixels, 0, width, 0, 0, width, height)
    }
  }

  private fun uploadBitmapToFirestore(bitmap: Bitmap, userId: String, db: FirebaseFirestore) {

    // Reference to the "USER" collection and specific document
    val userDocRef = db.collection("users").document(userId)

    // Convert Bitmap to byte array
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val byteArray = outputStream.toByteArray()

    // Upload image to Firebase Storage
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference.child("images/${userId}.png")
    val uploadTask = storageRef.putBytes(byteArray)

    // Add onCompleteListener to handle upload completion
    uploadTask.addOnCompleteListener { task ->
      if (task.isSuccessful) {
        // Get the download URL of the uploaded image
        storageRef.downloadUrl.addOnSuccessListener { uri ->
          // Update document in Firestore with the image URL
          userDocRef
              .update("QrCode", uri.toString())
              .addOnSuccessListener {
                // Image URL added to Firestore successfully
                println("Image uploaded and URL added to Firestore.")
              }
              .addOnFailureListener { e ->
                // Error updating document
                println("Error updating document: $e")
              }
        }
      } else {
        // Error uploading image to Firebase Storage
        println("Error uploading image: ${task.exception}")
      }
    }
  }
}
