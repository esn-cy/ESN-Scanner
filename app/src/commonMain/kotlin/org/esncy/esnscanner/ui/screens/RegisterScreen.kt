package org.esncy.esnscanner.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.serialization.json.Json
import org.esncy.esnscanner.models.SectionDataUIState
import org.esncy.esnscanner.models.SectionDataViewModel
import qrgenerator.qrkitpainter.QrKitBrush
import qrgenerator.qrkitpainter.QrKitColors
import qrgenerator.qrkitpainter.rememberQrKitPainter
import qrgenerator.qrkitpainter.solidBrush

@Composable
fun RegisterScreen(
    sectionDataViewModel: SectionDataViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val uiState by sectionDataViewModel.state.collectAsState()

        val currentConfig = (uiState as? SectionDataUIState.Success)?.result
        if (currentConfig == null) {
            Text("Failed to load configuration.")
        } else {
            val qrContent =
                Json.encodeToString("https://" + currentConfig.localSectionDomain + "/memberships/apply")
            val color = MaterialTheme.colorScheme.onSurface
            val painter = rememberQrKitPainter(qrContent) {
                colors = QrKitColors(
                    darkBrush = QrKitBrush.solidBrush(color)
                )
            }
            Image(
                painter = painter,
                contentDescription = "Registration QR Code",
                modifier = Modifier
                    .fillMaxSize(0.85f)
            )
        }
    }
}