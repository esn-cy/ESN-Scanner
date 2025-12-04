package com.andymic.esnscanner.ui.components.home

import android.Manifest
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.andymic.esnscanner.data.SectionData
import com.andymic.esnscanner.models.SectionDataUIState
import com.andymic.esnscanner.models.SectionDataViewModel
import com.andymic.esnscanner.ui.components.camera.CameraScanner
import com.andymic.esnscanner.ui.components.camera.rememberCameraScannerState
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SectionDataFields(
    modifier: Modifier = Modifier,
    onSave: (String, String, String, String) -> Unit,
    viewModel: SectionDataViewModel
) {
    val uiState = viewModel.state.collectAsState()
    val context = LocalContext.current

    val sectionName = rememberTextFieldState("")
    val sectionCode = rememberTextFieldState("")
    val sectionDomain = rememberTextFieldState("")
    val spreadsheetID = rememberTextFieldState("")

    var isDataLocked by remember { mutableStateOf(true) }

    var isScanning by remember { mutableStateOf(false) }
    var isReceiving by remember { mutableStateOf(true) }
    val cameraState = rememberCameraScannerState()

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

    LaunchedEffect(uiState) {
        if (uiState.value is SectionDataUIState.Success) {
            if (isDataLocked) {
                sectionName.setTextAndPlaceCursorAtEnd((uiState.value as SectionDataUIState.Success).result.localSectionName)
                sectionCode.setTextAndPlaceCursorAtEnd((uiState.value as SectionDataUIState.Success).result.localSectionCode)
                sectionDomain.setTextAndPlaceCursorAtEnd((uiState.value as SectionDataUIState.Success).result.localSectionDomain)
                spreadsheetID.setTextAndPlaceCursorAtEnd((uiState.value as SectionDataUIState.Success).result.spreadsheetID)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            horizontalArrangement = Arrangement.Absolute.Right
        ) {
            IconButton(
                onClick = {
                    isScanning = !isScanning
                }
            ) {
                Icon(Icons.Default.QrCodeScanner, "Configuration Scan")
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
                    Icon(Icons.Default.Lock, "Data Locked")
                else
                    Icon(Icons.Default.LockOpen, "Data Unlocked")
            }
        }

        if (isScanning) {
            if (!hasCameraPermission) {
                Text("Camera permission is required.")
                return
            }
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
                                val readValue = barcode.rawValue ?: continue
                                try {
                                    val receivedConfig =
                                        Json.decodeFromString<SectionData.SectionData>(readValue)
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
                        context = context,
                        modifier = Modifier.clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    val currentConfig = (uiState.value as? SectionDataUIState.Success)?.result
                    if (currentConfig == null) {
                        Text("Failed to load configuration.")
                    } else {
                        val qrContent = Json.encodeToString(currentConfig)
                        val bits = QRCodeWriter().encode(qrContent, BarcodeFormat.QR_CODE, 512, 512)
                        val image = createBitmap(512, 512, Bitmap.Config.RGB_565).also {
                            for (x in 0 until 512) {
                                for (y in 0 until 512) {
                                    it[x, y] = if (bits[x, y]) Color.BLACK else Color.WHITE
                                }
                            }
                        }
                        Image(
                            bitmap = image.asImageBitmap(),
                            contentDescription = "Configuration QR Code",
                            modifier = Modifier.align(Alignment.Center)

                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                ) {
                    ToggleButton(
                        checked = isReceiving,
                        onCheckedChange = { isReceiving = it },
                    ) {
                        Text("Receive")
                    }
                    ToggleButton(
                        checked = !isReceiving,
                        onCheckedChange = { isReceiving = !it }
                    ) {
                        Text("Share")
                    }
                }
            }
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

        if (uiState.value is SectionDataUIState.Error) {
            Text(
                text = "Error saving: ${(uiState.value as SectionDataUIState.Error).error?.message}",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}