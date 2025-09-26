package com.andymic.esnscanner.ui.components.deliver

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
import com.andymic.esnscanner.models.DeliverUIState

@Composable
fun DeliverBottomBox(uiState: DeliverUIState, modifier: Modifier) {
    val backgroundColor = when (uiState) {
        is DeliverUIState.Success -> Color.Green
        is DeliverUIState.Error -> Color.Red
        is DeliverUIState.Loading -> Color.DarkGray
        is DeliverUIState.Idle -> Color.Black
    }
    Box(
        modifier = modifier
            .background(backgroundColor)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        val text = when (uiState) {
            is DeliverUIState.Success -> uiState.result.status
            is DeliverUIState.Error -> uiState.message
            is DeliverUIState.Loading -> "LOADING..."
            is DeliverUIState.Idle -> "Point camera at a barcode"
        }

        val color = when (uiState) {
            is DeliverUIState.Success -> Color.Black
            is DeliverUIState.Error -> Color.Black
            is DeliverUIState.Loading -> Color.Black
            is DeliverUIState.Idle -> Color.White
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