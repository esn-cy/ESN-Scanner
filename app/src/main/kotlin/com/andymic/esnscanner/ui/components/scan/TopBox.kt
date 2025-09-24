package com.andymic.esnscanner.ui.components.scan

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.andymic.esnscanner.models.ScanUIState
import com.andymic.esnscanner.ui.theme.ESNCyan
import com.andymic.esnscanner.ui.theme.ESNDarkBlue

@Composable
fun ScanTopBox(uiState: ScanUIState, modifier: Modifier) {
    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.6f))
            .padding(16.dp)
    ) {
        if (uiState is ScanUIState.Success) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.5f)
                        .align(Alignment.CenterVertically)
                        .aspectRatio(2f / 3f)
                        .background(Color.Gray)
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
                            color = if (uiState.result.profileImageURL == "UNKNOWN") Color.Yellow else Color.Green,
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
        if (value.contains("UNKNOWN") || value == "N/A" || value == "NOT REGISTERED" || value.contains(
                "INCONSISTENT"
            )
        ) Color.Yellow else if (value == "EXPIRED") Color.Red else Color.White
    Column {
        Text(text = label, color = Color.LightGray, fontSize = 12.sp)
        Text(text = value, color = textColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}