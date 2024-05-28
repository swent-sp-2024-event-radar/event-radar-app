package com.github.se.eventradar.ui.qrCode

import android.Manifest
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.se.eventradar.viewmodel.qrCode.QrCodeAnalyser
import com.github.se.eventradar.viewmodel.qrCode.ScanTicketQrViewModel

@Composable
fun QrCodeScannerTicket(viewModel: ScanTicketQrViewModel = hiltViewModel()) {
    val analyser = viewModel.qrCodeAnalyser

    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current
    val cameraFutureProvider = remember { ProcessCameraProvider.getInstance(context) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { granted -> hasCameraPermission = granted })

    // only ask permission once
    LaunchedEffect(key1 = true) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

// Shared variables for camera configuration
    val preview = remember { Preview.Builder().build() }
    val selector = remember {
        CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
    }
    val imageAnalysis = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

//    fun reinitializeCamera(previewView: PreviewView) {
//        val cameraProvider = cameraFutureProvider.get()
//        cameraProvider.unbindAll()  // Unbind all use cases first
//
//        // Clear the previous surface provider
//        preview.setSurfaceProvider(null)
//
//        // Rebind the use cases
//        preview.setSurfaceProvider(previewView.surfaceProvider)
//        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context), analyser)
//
//        try {
//            cameraProvider.bindToLifecycle(
//                lifeCycleOwner,
//                selector,
//                preview,
//                imageAnalysis
//            )
//            Log.d("QrCodeScanner", "Camera use cases bound to lifecycle")
//        } catch (e: Exception) {
//            Log.e("QrCodeScanner", "Use case binding failed", e)
//        }
//    }



    //actual camera view
    Column(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            Spacer(modifier = Modifier.height(80.dp))
            AndroidView( // PreviewView !E for Composable hence need ot create AndroidView
                factory = { context ->
                    val previewView = PreviewView(context)
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                    imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context), analyser)
                    try {
                        cameraFutureProvider
                            .get()
                            .bindToLifecycle(
                                lifeCycleOwner, // only launched during current compose lifecycle
                                selector,
                                preview,
                                imageAnalysis
                            )
                    } catch (e: Exception) {
                        Log.e("QrCodeCameraFail", "Use case binding failed", e)
                    }
                    previewView
                },
                modifier = Modifier.weight(1.5f).aspectRatio(1f).padding(horizontal = 32.dp)
            )
//            LaunchedEffect(key1 = uiState.action) {
//                if (uiState.action == ScanTicketQrViewModel.Action.ScanTicket) {
//                    // Reinitialize the camera using the same PreviewView instance
//                    val previewView = PreviewView(context)
//                    reinitializeCamera(previewView)
//                    Log.d("QrCodeScanner", "Reinitializing camera on state change to ScanTicket")
//                }
//            }
//            LaunchedEffect(key1 = uiState.action) {
//                if (uiState.action == ScanTicketQrViewModel.Action.ScanTicket) {
//                    val cameraProvider = cameraFutureProvider.get()
//                    cameraProvider.unbindAll()  // Unbind all use cases first
//
//                    // Rebind the use cases
//                    val previewView = PreviewView(context)
//                    preview.setSurfaceProvider(previewView.surfaceProvider)
//                    imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context), analyser)
//
//                    try {
//                        cameraProvider.bindToLifecycle(
//                            lifeCycleOwner,
//                            selector,
//                            preview,
//                            imageAnalysis
//                        )
//                    } catch (e: Exception) {
//                        Log.e("QrCodeCameraFail", "Use case binding failed", e)
//                    }
//                }
//            }
        }
    }
}