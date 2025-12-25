package org.esncy.esnscanner.ui.components.scan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import org.esncy.esnscanner.models.ScanUIState
import org.esncy.esnscanner.ui.theme.ESNCyan
import org.esncy.esnscanner.ui.theme.ESNDarkBlue
import org.esncy.esnscanner.ui.theme.statusColorScheme

@Composable
fun ScanTopBox(uiState: ScanUIState, modifier: Modifier) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp)
    ) {
        if (uiState is ScanUIState.Success) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.5f)
                        .align(Alignment.CenterVertically)
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                )
                {
                    Image(
                        painter = rememberAsyncImagePainter(uiState.result.profileImageURL),
                        contentDescription = "Cardholder Picture",
                        contentScale = ContentScale.Crop
                    )
                    if (uiState.result.profileImageURL == "UNKNOWN" || uiState.result.profileImageURL == "ESN Cyprus Pass") {
                        Text(
                            text = if (uiState.result.profileImageURL == "UNKNOWN") "?" else "\u2713",
                            color =
                                if (uiState.result.profileImageURL == "UNKNOWN")
                                    MaterialTheme.statusColorScheme.warning
                                else
                                    MaterialTheme.statusColorScheme.success,
                            fontWeight = FontWeight.Bold,
                            fontSize = 64.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceEvenly) {
                    InfoRow("Name:", uiState.result.fullName)
                    InfoRow("Country:", uiState.result.nationality)
                    InfoRow("Status:", uiState.result.cardStatus)
                    InfoRow("Section:", uiState.result.issuingSection)
                    InfoRow("Expires:", uiState.result.expirationDate)
                    InfoRow("Last Scan:", uiState.result.lastScanDate)
                }
            }
        }
        if (uiState is ScanUIState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize(0.25f)
                    .align(Alignment.Center),
                color = ESNCyan,
                trackColor = ESNDarkBlue,
                strokeWidth = 6.dp
            )
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    val textColor =
        if (
            value.contains("UNKNOWN") ||
            value == "NOT REGISTERED" ||
            value.contains("INCONSISTENT")
        )
            MaterialTheme.statusColorScheme.warning
        else if (value == "EXPIRED")
            MaterialTheme.colorScheme.error
        else
            MaterialTheme.colorScheme.onSurface
    Column {
        Text(text = label, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
        Text(text = value, color = textColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}