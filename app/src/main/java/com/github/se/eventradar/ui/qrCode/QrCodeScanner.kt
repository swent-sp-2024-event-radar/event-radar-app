package com.github.se.eventradar.ui.qrCode

import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.github.se.eventradar.viewmodel.qrCode.QrCodeAnalyser

@Composable
fun QrCodeScanner(analyser: QrCodeAnalyser) {

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
      Spacer(modifier = Modifier.height(80.dp))
      AndroidView( // PreviewView !E for Composable hence need ot create AndroidView
          factory = { context ->
            val previewView = PreviewView(context)
            val preview = Preview.Builder().build()
            val selector =
                CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
            preview.setSurfaceProvider(previewView.surfaceProvider)
            val imageAnalysis =
                ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context), analyser)

            try {
              cameraFutureProvider
                  .get()
                  .bindToLifecycle(
                      lifeCycleOwner, // only launched during current compose lifecycle
                      selector,
                      preview,
                      imageAnalysis)
            } catch (e: Exception) {
              Log.e("QrCodeCameraFail", "Use case binding failed", e)
            }
            previewView
          },
          modifier = Modifier.weight(1.5f).aspectRatio(1f).padding(horizontal = 32.dp))
    }
  }
}