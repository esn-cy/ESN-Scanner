package com.andymic.esnscanner.ui.components.add

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
import com.andymic.esnscanner.models.AddUIState

@Composable
fun AddBottomBox(uiState: AddUIState, modifier: Modifier) {
    val backgroundColor = when (uiState) {
        is AddUIState.Success -> Color.Green
        is AddUIState.Error -> Color.Red
        is AddUIState.Loading -> Color.DarkGray
        is AddUIState.Idle -> Color.Black
    }
    Box(
        modifier = modifier
            .background(backgroundColor)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        val text = when (uiState) {
            is AddUIState.Success -> uiState.result.status
            is AddUIState.Error -> uiState.message
            is AddUIState.Loading -> "LOADING..."
            is AddUIState.Idle -> "Point camera at a barcode"
        }

        val color = when (uiState) {
            is AddUIState.Success -> Color.Black
            is AddUIState.Error -> Color.Black
            is AddUIState.Loading -> Color.Black
            is AddUIState.Idle -> Color.White
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