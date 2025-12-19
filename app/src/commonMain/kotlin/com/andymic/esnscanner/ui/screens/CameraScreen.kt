package com.andymic.esnscanner.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.andymic.esnscanner.ui.components.camera.CameraScanner
import com.andymic.esnscanner.ui.components.camera.rememberCameraScannerState
import com.andymic.esnscanner.ui.theme.ESNCyan
import com.andymic.esnscanner.ui.theme.ESNGreen
import com.andymic.esnscanner.ui.theme.ESNMagenta
import com.andymic.esnscanner.ui.theme.ESNOrange
import esnscanner.app.generated.resources.Res
import esnscanner.app.generated.resources.flash_off_filled
import esnscanner.app.generated.resources.flash_on_filled
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.vectorResource

interface CameraViewModel<T> {
    val state: StateFlow<T>
    fun onScan(scannedString: String)
}

@Composable
expect fun CameraPermissionHandler(
    onPermissionGranted: @Composable () -> Unit,
    onPermissionDenied: @Composable () -> Unit
)

/**
 * A generic and reusable camera scanner layout.
 *
 * @param T The type of the UI state object.
 * @param viewModel The ViewModel that provides the UI state and handles scan events.
 * @param TopBox A composable lambda for the content to be displayed above the scanner view.
 * @param BottomBox A composable lambda for the content to be displayed below the scanner view.
 * @param modifier The modifier to be applied to the layout.
 */
@Composable
fun <T> CameraScreen(
    viewModel: CameraViewModel<T>,
    TopBox: @Composable (uiState: T, modifier: Modifier) -> Unit,
    BottomBox: @Composable (uiState: T, modifier: Modifier) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.state.collectAsState()
    val cameraState = rememberCameraScannerState()

    CameraPermissionHandler(
        onPermissionDenied = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Camera permission is required.")
            }
        },
        onPermissionGranted = {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = modifier.fillMaxSize(),
                ) {
                    TopBox(uiState, Modifier.fillMaxWidth().weight(4.5f))

                    BoxWithConstraints(modifier = Modifier.weight(4.5f).clipToBounds()) {
                        CameraScanner(
                            onBarcodesScanned = { barcodes ->
                                for (barcode in barcodes) {
                                    viewModel.onScan(barcode)
                                }
                            },
                            state = cameraState,
                            modifier = Modifier.fillMaxSize()
                        )

                        val actionSize = min(maxWidth, maxHeight) * 0.15f
                        FloatingActionButton(
                            onClick = { cameraState.toggleFlash() },
                            modifier = Modifier
                                .padding(16.dp)
                                .size(actionSize)
                                .align(Alignment.BottomEnd),
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Icon(
                                imageVector = if (cameraState.isFlashOn) vectorResource(Res.drawable.flash_off_filled) else vectorResource(
                                    Res.drawable.flash_on_filled
                                ),
                                contentDescription = "Scan QR Code"
                            )
                        }

                        val boxSize = min(maxWidth, maxHeight) * 0.85f
                        Box(
                            modifier = Modifier
                                .size(boxSize)
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
    )
}