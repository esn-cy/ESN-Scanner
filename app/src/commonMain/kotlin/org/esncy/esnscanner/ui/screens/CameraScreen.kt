package org.esncy.esnscanner.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import esnscanner.app.generated.resources.Res
import esnscanner.app.generated.resources.flash_off_filled
import esnscanner.app.generated.resources.flash_on_filled
import esnscanner.app.generated.resources.report_outlined
import kotlinx.coroutines.flow.StateFlow
import org.esncy.esnscanner.models.StatusUIState
import org.esncy.esnscanner.models.StatusViewModel
import org.esncy.esnscanner.ui.components.camera.CameraScanner
import org.esncy.esnscanner.ui.components.camera.rememberCameraScannerState
import org.esncy.esnscanner.ui.theme.ESNCyan
import org.esncy.esnscanner.ui.theme.ESNGreen
import org.esncy.esnscanner.ui.theme.ESNMagenta
import org.esncy.esnscanner.ui.theme.ESNOrange
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
@OptIn(ExperimentalMaterial3Api::class)
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

                    if (uiState is StatusUIState.AwaitingInput) {
                        val currentState = uiState as StatusUIState.AwaitingInput
                        AlertDialog(
                            icon = {
                                Icon(
                                    vectorResource(Res.drawable.report_outlined),
                                    "Alert",
                                    Modifier.size(48.dp),
                                    MaterialTheme.colorScheme.error
                                )
                            },
                            title = {
                                Text(
                                    text = currentState.title,
                                    style = MaterialTheme.typography.headlineMedium,
                                    textAlign = TextAlign.Center
                                )
                            },
                            text = {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = currentState.body,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Do you want to proceed?",
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = "(Note: This is not reversible)",
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            },
                            onDismissRequest = { (viewModel as StatusViewModel).reset() },
                            confirmButton = {
                                TextButton({
                                    (viewModel as StatusViewModel).updateStatus(
                                        currentState.identifier,
                                        currentState.lastScan
                                    )
                                }) { Text("Confirm") }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { (viewModel as StatusViewModel).reset() }
                                ) {
                                    Text("Dismiss")
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}