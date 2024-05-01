package com.github.se.eventradar.ui.qrcode

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.viewmodel.MyQrCodeViewModel

@Composable
fun MyQrCodeScreen(
    viewModel: MyQrCodeViewModel = hiltViewModel(),
    navigationActions: NavigationActions
) {
    val uiState by viewModel.uiState.collectAsState()

}