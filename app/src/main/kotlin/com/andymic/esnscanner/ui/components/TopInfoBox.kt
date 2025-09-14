package com.andymic.esnscanner.ui.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.andymic.esnscanner.ScanUIState

@Composable
fun TopInfoBox(uiState: ScanUIState, modifier: Modifier) {
    Box(modifier = modifier) {
        if (uiState is ScanUIState.Success) {
            Row(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = rememberAsyncImagePainter(uiState.result.profileImageURL),
                    contentDescription = "Cardholder Picture",
                    modifier = Modifier
                        .fillMaxHeight(0.5f)
                        .align(Alignment.CenterVertically)
                        .aspectRatio(2f / 3f)
                        .background(Color.Gray),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceEvenly) {
                    InfoRow("Name:", uiState.result.name.trim() + " " + uiState.result.surname.trim())
                    InfoRow("Country:", uiState.result.homeCountry)
                    InfoRow("Status:", uiState.result.cardStatus)
                    InfoRow("Section:", uiState.result.issuingSection)
                    InfoRow("Expires:", uiState.result.expirationDate)
                    InfoRow("Last Scan:", uiState.result.lastScanDate)
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    val textColor =
        if (value.contains("UNKNOWN")) Color.Yellow else if (value == "Expired") Color.Red else Color.White
    Column {
        Text(text = label, color = Color.LightGray, fontSize = 12.sp)
        Text(text = value, color = textColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}