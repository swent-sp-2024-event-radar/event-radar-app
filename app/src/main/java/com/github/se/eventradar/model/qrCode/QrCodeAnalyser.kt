package com.github.se.eventradar.model.qrCode

import android.graphics.ImageFormat
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.github.se.eventradar.model.Resource
import com.github.se.eventradar.model.repository.user.FirebaseUserRepository
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.nio.ByteBuffer

class QrCodeAnalyser(private val activityScope: CoroutineScope, private val friendOrTicket: Int) : ImageAnalysis.Analyzer {

  // list of supported Image Formats
  private val supportedImageFormats =
      listOf(ImageFormat.YUV_420_888, ImageFormat.YUV_422_888, ImageFormat.YUV_444_888)

  override fun analyze(image: ImageProxy) {

      // only want to scan if it is a QR Code
    if (image.format in supportedImageFormats) {
      val bytes = image.planes.first().buffer.toByteArray()

      // parameters to scan
      val source =
          PlanarYUVLuminanceSource(
              bytes, image.width, image.height, 0, 0, image.width, image.height, false)

      val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
      try {

        // result is the info encoded in to QR Code (String of userId in our case)
        val result =
            MultiFormatReader()
                .apply {
                  setHints(
                      mapOf(DecodeHintType.POSSIBLE_FORMATS to arrayListOf(BarcodeFormat.QR_CODE)))
                }
                .decode(binaryBitmap)

        if (friendOrTicket == 0) { // SCAN FRIEND
            activityScope.launch {
                when (val newUserFriend = FirebaseUserRepository().getUser(result.text)) {
                    is Resource.Success ->
                        if (newUserFriend.data!!.friendList.contains(myUID)) {
                            //Navigate to message screen
                        } else {
                            newUserFriend.data.friendList.add(myUID)
                            FirebaseUserRepository().updateUser(newUserFriend.data)
                        }

                    is Resource.Failure -> {
                        println("Failed to Fetch from Database")
                    }
                }
            }
        } 

      } catch (e: Exception) {
        e.printStackTrace()
      } finally { // close image once scanning process done
        image.close()
      }
    }
  }

  // method to return all bytes in a ByteArray
  private fun ByteBuffer.toByteArray(): ByteArray {
    rewind()
    return ByteArray(remaining()).also { get(it) }
  }
}
