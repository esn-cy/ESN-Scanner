package com.andymic.esnscanner.ui.components.deliver

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
import com.andymic.esnscanner.models.DeliverUIState
import com.andymic.esnscanner.ui.theme.statusColorScheme

@Composable
fun DeliverBottomBox(uiState: DeliverUIState, modifier: Modifier) {
    val backgroundColor = when (uiState) {
        is DeliverUIState.Success -> MaterialTheme.statusColorScheme.successContainer
        is DeliverUIState.Error -> MaterialTheme.colorScheme.errorContainer
        is DeliverUIState.Loading -> MaterialTheme.colorScheme.surfaceContainer
        is DeliverUIState.Idle -> MaterialTheme.colorScheme.surfaceContainer
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
            is DeliverUIState.Success -> MaterialTheme.statusColorScheme.onSuccessContainer
            is DeliverUIState.Error -> MaterialTheme.colorScheme.onErrorContainer
            is DeliverUIState.Loading -> MaterialTheme.colorScheme.onSurface
            is DeliverUIState.Idle -> MaterialTheme.colorScheme.onSurface
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