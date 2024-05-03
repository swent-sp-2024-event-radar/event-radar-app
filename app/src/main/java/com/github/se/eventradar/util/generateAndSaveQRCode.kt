package com.github.se.eventradar.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import java.io.File
import java.io.FileOutputStream

object QRCodeUtil {
    // Utility function to generate and save QR Code
    fun generateAndSaveQRCode(context: Context, data: String): Uri {
        val bitMatrix = MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 200, 200)
        val bitmap = Bitmap.createBitmap(bitMatrix.width, bitMatrix.height, Bitmap.Config.RGB_565)
        for (x in 0 until bitMatrix.width) {
            for (y in 0 until bitMatrix.height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }

        val file = File(context.cacheDir, "$data.png")
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()
        return Uri.fromFile(file)
    }
}