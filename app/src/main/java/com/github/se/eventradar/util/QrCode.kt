package com.github.se.eventradar.util
import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream
import java.util.*


class QrCode {

    fun generateQRCode(content: String, width: Int = 512, height: Int = 512): Bitmap? {
         //Create a map of hints for the QR code generation
        val hints: MutableMap<EncodeHintType, Any> = EnumMap(EncodeHintType::class.java)
        hints[EncodeHintType.MARGIN] = 0 // Adjust margin as needed

        // Create a QRCodeWriter instance
        val writer = QRCodeWriter()

        // Generate the QR code matrix
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints)

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

    fun saveBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, path: String): Boolean {
        return try {
            val fileOutputStream = FileOutputStream(path)
            bitmap.compress(format, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


}