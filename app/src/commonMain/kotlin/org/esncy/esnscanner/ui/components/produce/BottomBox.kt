package org.esncy.esnscanner.ui.components.produce

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
import org.esncy.esnscanner.models.ProduceUIState
import org.esncy.esnscanner.ui.theme.statusColorScheme

@Composable
fun ProduceBottomBox(uiState: ProduceUIState, modifier: Modifier) {
    val backgroundColor = when (uiState) {
        is ProduceUIState.Success -> MaterialTheme.statusColorScheme.successContainer
        is ProduceUIState.Error -> MaterialTheme.colorScheme.errorContainer
        is ProduceUIState.Loading -> MaterialTheme.colorScheme.surfaceContainer
        is ProduceUIState.Idle -> MaterialTheme.colorScheme.surfaceContainer
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
            is ProduceUIState.Success -> MaterialTheme.statusColorScheme.onSuccessContainer
            is ProduceUIState.Error -> MaterialTheme.colorScheme.onErrorContainer
            is ProduceUIState.Loading -> MaterialTheme.colorScheme.onSurface
            is ProduceUIState.Idle -> MaterialTheme.colorScheme.onSurface
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