package com.andymic.esnscanner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andymic.esnscanner.ScanUIState

@Composable
fun BottomStatusBox(modifier: Modifier, uiState: ScanUIState) {
    val backgroundColor = when (uiState) {
        is ScanUIState.Success -> {
            when (uiState.result.result) {
                "Valid" -> Color.Green
                "Already Scanned" -> Color.Red
                "Not in our System" -> Color.Yellow
                "Foreign Card" -> Color.Yellow
                "Valid Locally. Check Number" -> Color.Yellow
                "Expired" -> Color.Red
                else -> Color.Black.copy(alpha = 0.6f)
            }
        }
        is ScanUIState.Error -> Color.Red
        is ScanUIState.Loading -> Color.DarkGray
        is ScanUIState.Idle -> Color.Black
    }
    Box(
        modifier = modifier.background(backgroundColor).padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        val text = when (uiState) {
            is ScanUIState.Success -> uiState.result.result
            is ScanUIState.Error -> uiState.message
            is ScanUIState.Loading -> "LOADING..."
            is ScanUIState.Idle -> "Point camera at a barcode"
        }

        val color = when (uiState) {
            is ScanUIState.Success -> Color.Black
            is ScanUIState.Error -> Color.Black
            is ScanUIState.Loading -> Color.Black
            is ScanUIState.Idle -> Color.White
        }
        Text(
            text = text,
            color = color,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}