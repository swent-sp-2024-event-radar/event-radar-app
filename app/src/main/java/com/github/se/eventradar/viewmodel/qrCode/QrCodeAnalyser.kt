package com.github.se.eventradar.viewmodel.qrCode

import android.graphics.ImageFormat
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer
import javax.inject.Inject

class QrCodeAnalyser @Inject constructor() : ImageAnalysis.Analyzer {

  // list of supported Image Formats
  private val supportedImageFormats =
      listOf(ImageFormat.YUV_420_888, ImageFormat.YUV_422_888, ImageFormat.YUV_444_888)

  var onDecoded: ((String?) -> Unit)? = null
    private var activeAnalysis = true

    fun changeAnalysisState(boolean: Boolean) {
        activeAnalysis = boolean
    }

  override fun analyze(image: ImageProxy) {
//      if (!activeAnalysis) {
//          image.close()
//          return
//      }

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
        onDecoded?.invoke(result.toString())
      } catch (e: Exception) {

        Log.d("QrCodeAnalyser", "Error decoding QR Code: ${e.message}")
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
