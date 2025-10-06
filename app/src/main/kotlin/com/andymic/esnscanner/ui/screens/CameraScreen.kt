package com.andymic.esnscanner.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.andymic.esnscanner.ui.components.camera.CameraScanner
import com.andymic.esnscanner.ui.theme.ESNCyan
import com.andymic.esnscanner.ui.theme.ESNGreen
import com.andymic.esnscanner.ui.theme.ESNMagenta
import com.andymic.esnscanner.ui.theme.ESNOrange
import kotlinx.coroutines.flow.StateFlow
import rememberCameraScannerState

interface CameraViewModel<T> {
    val state: StateFlow<T>
    fun onScan(scannedString: String)
}

/**
 * A generic and reusable camera scanner layout.
 *
 * @param T The type of the UI state object.
 * @param viewModel The ViewModel that provides the UI state and handles scan events.
 * @param TopBox A composable lambda for the content to be displayed above the scanner view.
 * @param BottomBox A composable lambda for the content to be displayed below the scanner view.
 * @param modifier The modifier to be applied to the layout.
 */
@SuppressLint("SourceLockedOrientationActivity")
@Composable
fun <T> CameraScreen(
    viewModel: CameraViewModel<T>,
    TopBox: @Composable (uiState: T, modifier: Modifier) -> Unit,
    BottomBox: @Composable (uiState: T, modifier: Modifier) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.state.collectAsState()
    val cameraState = rememberCameraScannerState()
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
        Column(
            modifier = modifier.fillMaxSize(),
        ) {
            TopBox(
                uiState,
                Modifier
                    .fillMaxWidth()
                    .weight(4.5f)
            )

            Box(
                modifier = Modifier
                    .weight(4.5f)
                    .clipToBounds()
            ) {
                CameraScanner(
                    onBarcodesScanned = { barcodes ->
                        for (barcode in barcodes) {
                            val value = barcode.rawValue
                            if (value == null)
                                continue
                            viewModel.onScan(value)
                        }
                    },
                    state = cameraState,
                    context = LocalContext.current
                )
                FloatingActionButton(
                    onClick = { cameraState.toggleFlash() },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomEnd)
                        .fillMaxSize(0.15f)
                        .aspectRatio(1f),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = if (cameraState.isFlashOn) Icons.Filled.FlashOff else Icons.Filled.FlashOn,
                        contentDescription = "Scan QR Code"
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.85f)
                        .aspectRatio(1f)
                        .padding(16.dp)
                        .align(Alignment.Center)
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

            BottomBox(
                uiState,
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}