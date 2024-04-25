package com.github.se.eventradar.qrCode

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat

class QrCodeScanCamera(private val onQrCodeScanned: (String) -> Unit) {

  @Composable
  fun QrCodeScanner() {

    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current
    val cameraFutureProvider = remember { ProcessCameraProvider.getInstance(context) }

    var hasCameraPermission by remember {
      mutableStateOf(
          ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) ==
              PackageManager.PERMISSION_GRANTED)
    }

    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { granted -> hasCameraPermission = granted })
    // only ask permission once
    LaunchedEffect(key1 = true) { launcher.launch(android.Manifest.permission.CAMERA) }
    Column(modifier = Modifier.fillMaxSize()) {
      if (hasCameraPermission) {
        AndroidView( // PreviewView !E for Composable hence need ot create AndroidView
            factory = { context ->
              val previewView = PreviewView(context)
              val preview = Preview.Builder().build()
              val selector =
                  CameraSelector.Builder()
                      .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                      .build()
              preview.setSurfaceProvider(previewView.surfaceProvider)
              val imageAnalysis =
                  ImageAnalysis.Builder()
                      .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                      .build()
              imageAnalysis.setAnalyzer(
                  ContextCompat.getMainExecutor(context), QrCodeScanConstraints(onQrCodeScanned))
              try {
                cameraFutureProvider
                    .get()
                    .bindToLifecycle(
                        lifeCycleOwner, // only launched during current compose lifecycle
                        selector,
                        preview,
                        imageAnalysis)
              } catch (e: Exception) {
                e.printStackTrace()
              }
              previewView
            },
            modifier = Modifier.weight(1f))
        //                Text(
        //                    text = code, )

      }
    }
  }
}
