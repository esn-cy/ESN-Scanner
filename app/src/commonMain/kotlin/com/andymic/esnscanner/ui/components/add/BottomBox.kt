package com.andymic.esnscanner.ui.components.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andymic.esnscanner.models.AddUIState
import com.andymic.esnscanner.ui.theme.statusColorScheme

@Composable
fun AddBottomBox(uiState: AddUIState, modifier: Modifier) {
    val backgroundColor = when (uiState) {
        is AddUIState.Success -> MaterialTheme.statusColorScheme.successContainer
        is AddUIState.Error -> MaterialTheme.colorScheme.errorContainer
        is AddUIState.Loading -> MaterialTheme.colorScheme.surfaceContainer
        is AddUIState.Idle -> MaterialTheme.colorScheme.surfaceContainer
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
            is AddUIState.Success -> MaterialTheme.statusColorScheme.onSuccessContainer
            is AddUIState.Error -> MaterialTheme.colorScheme.onErrorContainer
            is AddUIState.Loading -> MaterialTheme.colorScheme.onSurface
            is AddUIState.Idle -> MaterialTheme.colorScheme.onSurface
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