package org.esncy.esnscanner.ui.components.camera

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun CameraScanner(
    onBarcodesScanned: (List<String>) -> Unit,
    state: CameraScannerState,
    modifier: Modifier = Modifier
)