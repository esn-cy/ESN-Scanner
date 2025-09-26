package com.andymic.esnscanner.ui.components.produce

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
import com.andymic.esnscanner.models.ProduceUIState

@Composable
fun ProduceBottomBox(uiState: ProduceUIState, modifier: Modifier) {
    val backgroundColor = when (uiState) {
        is ProduceUIState.Success -> Color.Green
        is ProduceUIState.Error -> Color.Red
        is ProduceUIState.Loading -> Color.DarkGray
        is ProduceUIState.Idle -> Color.Black
    }
    Box(
        modifier = modifier
            .background(backgroundColor)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        val text = when (uiState) {
            is ProduceUIState.Success -> uiState.result.status
            is ProduceUIState.Error -> uiState.message
            is ProduceUIState.Loading -> "LOADING..."
            is ProduceUIState.Idle -> "Point camera at a barcode"
        }

        val color = when (uiState) {
            is ProduceUIState.Success -> Color.Black
            is ProduceUIState.Error -> Color.Black
            is ProduceUIState.Loading -> Color.Black
            is ProduceUIState.Idle -> Color.White
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