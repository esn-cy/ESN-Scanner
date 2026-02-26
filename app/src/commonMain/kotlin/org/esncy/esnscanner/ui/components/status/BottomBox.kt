package org.esncy.esnscanner.ui.components.status

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
import org.esncy.esnscanner.models.StatusUIState
import org.esncy.esnscanner.ui.theme.statusColorScheme

@Composable
fun StatusBottomBox(uiState: StatusUIState, modifier: Modifier) {
    val backgroundColor = when (uiState) {
        is StatusUIState.Success -> MaterialTheme.statusColorScheme.successContainer
        is StatusUIState.Error -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceContainer
    }
    Box(
        modifier = modifier
            .background(backgroundColor)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        val text = when (uiState) {
            is StatusUIState.Success -> uiState.result.status
            is StatusUIState.Error -> uiState.message
            is StatusUIState.Loading -> "LOADING..."
            is StatusUIState.Idle -> "Point camera at a barcode"
            is StatusUIState.AwaitingInput -> "Check open dialog"
        }

        val color = when (uiState) {
            is StatusUIState.Success -> MaterialTheme.statusColorScheme.onSuccessContainer
            is StatusUIState.Error -> MaterialTheme.colorScheme.onErrorContainer
            else -> MaterialTheme.colorScheme.onSurface
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