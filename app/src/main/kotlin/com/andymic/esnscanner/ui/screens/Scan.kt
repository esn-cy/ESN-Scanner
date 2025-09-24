package com.andymic.esnscanner.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andymic.esnscanner.CameraScanner
import com.andymic.esnscanner.ScanViewModel
import com.andymic.esnscanner.ui.components.BottomStatusBox
import com.andymic.esnscanner.ui.components.TopInfoBox
import com.andymic.esnscanner.ui.theme.ESNCyan
import com.andymic.esnscanner.ui.theme.ESNGreen
import com.andymic.esnscanner.ui.theme.ESNMagenta
import com.andymic.esnscanner.ui.theme.ESNOrange

@SuppressLint("SourceLockedOrientationActivity")
@Composable
fun ScanScreen(viewModel: ScanViewModel = viewModel()) {
    val uiState by viewModel.scanState.collectAsState()
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )
    LaunchedEffect(key1 = true) {
        val activity = context as? Activity
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (!hasCameraPermission) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Camera permission is required.")
            }
            return
        }
        Column(modifier = Modifier.fillMaxSize()) {
            TopInfoBox(
                uiState = uiState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(4.5f)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(16.dp)
            )

            Box(
                modifier = Modifier
                    .weight(4.5f)
                    .aspectRatio(1f)
            ) {
                CameraScanner({ barcodes ->
                    for (barcode in barcodes) {
                        val value = barcode.rawValue
                        if (value == null)
                            continue
                        viewModel.scanRequest(value)
                    }
                }, LocalContext.current)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .border(
                            width = 4.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    ESNCyan,
                                    ESNMagenta,
                                    ESNGreen,
                                    ESNOrange
                                ),
                                start = Offset.Zero,
                                end = Offset.Infinite
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                )
            }

            BottomStatusBox(
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
                uiState
            )
        }
    }
}