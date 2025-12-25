package org.esncy.esnscanner.ui.components.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import esnscanner.app.generated.resources.Res
import esnscanner.app.generated.resources.download
import esnscanner.app.generated.resources.lock_closed
import esnscanner.app.generated.resources.lock_open
import esnscanner.app.generated.resources.qr_code_scanner
import esnscanner.app.generated.resources.upload
import kotlinx.serialization.json.Json
import org.esncy.esnscanner.models.SectionData
import org.esncy.esnscanner.models.SectionDataUIState
import org.esncy.esnscanner.models.SectionDataViewModel
import org.esncy.esnscanner.ui.components.camera.CameraScanner
import org.esncy.esnscanner.ui.components.camera.rememberCameraScannerState
import org.esncy.esnscanner.ui.screens.CameraPermissionHandler
import org.jetbrains.compose.resources.vectorResource
import qrgenerator.qrkitpainter.QrKitBrush
import qrgenerator.qrkitpainter.QrKitColors
import qrgenerator.qrkitpainter.rememberQrKitPainter
import qrgenerator.qrkitpainter.solidBrush

@Composable
fun SectionDataFields(
    modifier: Modifier = Modifier,
    onSave: (String, String, String, String) -> Unit,
    viewModel: SectionDataViewModel
) {
    val uiState by viewModel.state.collectAsState()

    val sectionName = rememberTextFieldState("")
    val sectionCode = rememberTextFieldState("")
    val sectionDomain = rememberTextFieldState("")
    val spreadsheetID = rememberTextFieldState("")

    var isDataLocked by remember { mutableStateOf(true) }

    var isScanning by remember { mutableStateOf(false) }
    var isReceiving by remember { mutableStateOf(true) }
    val cameraState = rememberCameraScannerState()

    LaunchedEffect(uiState) {
        if (uiState is SectionDataUIState.Success) {
            val data = (uiState as SectionDataUIState.Success).result

            if (isDataLocked) {
                sectionName.setTextAndPlaceCursorAtEnd(data.localSectionName)
                sectionCode.setTextAndPlaceCursorAtEnd(data.localSectionCode)
                sectionDomain.setTextAndPlaceCursorAtEnd(data.localSectionDomain)
                spreadsheetID.setTextAndPlaceCursorAtEnd(data.spreadsheetID)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(0.dp),
            horizontalArrangement = Arrangement.Absolute.Right
        ) {
            IconButton(
                modifier = Modifier.clip(CircleShape)
                    .background(if (isScanning) MaterialTheme.colorScheme.surfaceContainerHighest else Color.Transparent),
                onClick = {
                    isScanning = !isScanning
                }
            ) {
                Icon(vectorResource(Res.drawable.qr_code_scanner), "Configuration Scan")
            }
            IconButton(
                onClick = {
                    if (!isDataLocked) {
                        onSave(
                            sectionName.text.toString(),
                            sectionCode.text.toString(),
                            sectionDomain.text.toString(),
                            spreadsheetID.text.toString()
                        )
                    }
                    isDataLocked = !isDataLocked
                }
            ) {
                if (isDataLocked)
                    Icon(vectorResource(Res.drawable.lock_closed), "Data Locked")
                else
                    Icon(vectorResource(Res.drawable.lock_open), "Data Unlocked")
            }
        }

        if (isScanning) {
            CameraPermissionHandler(
                onPermissionDenied = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Camera permission is required.")
                    }
                },
                onPermissionGranted = {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clipToBounds(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isReceiving) {
                            CameraScanner(
                                onBarcodesScanned = { barcodes ->
                                    for (barcode in barcodes) {
                                        try {
                                            val receivedConfig =
                                                Json.decodeFromString<SectionData>(barcode)
                                            viewModel.updateData(
                                                receivedConfig.localSectionName,
                                                receivedConfig.localSectionCode,
                                                receivedConfig.localSectionDomain,
                                                receivedConfig.spreadsheetID
                                            )
                                        } catch (_: Exception) {
                                            continue
                                        }
                                        isScanning = false
                                        return@CameraScanner
                                    }
                                },
                                state = cameraState,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                            )
                        } else {
                            val currentConfig = (uiState as? SectionDataUIState.Success)?.result
                            if (currentConfig == null) {
                                Text("Failed to load configuration.")
                            } else {
                                val qrContent = Json.encodeToString(currentConfig)
                                val color = MaterialTheme.colorScheme.onSurface
                                val painter = rememberQrKitPainter(qrContent) {
                                    colors = QrKitColors(
                                        darkBrush = QrKitBrush.solidBrush(color)
                                    )
                                }
                                Image(
                                    painter = painter,
                                    contentDescription = "Configuration QR Code",
                                    modifier = Modifier.align(Alignment.Center)

                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp)
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(100)
                                ),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            IconToggleButton(
                                checked = isReceiving,
                                onCheckedChange = { isReceiving = it },
                            ) {
                                Icon(vectorResource(Res.drawable.download), "Download")
                            }
                            IconToggleButton(
                                checked = !isReceiving,
                                onCheckedChange = { isReceiving = !it }
                            ) {
                                Icon(vectorResource(Res.drawable.upload), "Upload")
                            }
                        }
                    }
                }
            )
        } else {
            OutlinedTextField(
                state = sectionName,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isDataLocked,
                label = { Text("Section Name") }
            )
            OutlinedTextField(
                state = sectionCode,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isDataLocked,
                label = { Text("Section Code") }
            )
            OutlinedTextField(
                state = sectionDomain,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isDataLocked,
                label = { Text("Section Domain") }
            )
            OutlinedTextField(
                state = spreadsheetID,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isDataLocked,
                label = { Text("Spreadsheet ID") }
            )
        }

        if (uiState is SectionDataUIState.Error) {
            Text(
                text = "Error saving: ${(uiState as SectionDataUIState.Error).error?.message}",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}