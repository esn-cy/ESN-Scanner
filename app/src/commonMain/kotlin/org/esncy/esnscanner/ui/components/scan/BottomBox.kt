package org.esncy.esnscanner.ui.components.scan

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
import org.esncy.esnscanner.models.ScanUIState
import org.esncy.esnscanner.ui.theme.statusColorScheme

@Composable
fun ScanBottomBox(uiState: ScanUIState, modifier: Modifier) {
    val backgroundColor = when (uiState) {
        is ScanUIState.Success -> {
            when (uiState.result.result) {
                "Valid" -> MaterialTheme.statusColorScheme.successContainer
                "Already Scanned" -> MaterialTheme.colorScheme.errorContainer
                "Not in our System" -> MaterialTheme.statusColorScheme.warningContainer
                "Valid Foreign Card" -> MaterialTheme.statusColorScheme.warningContainer
                "Expired" -> MaterialTheme.colorScheme.errorContainer
                else -> {
                    if (uiState.result.result.contains("Not Registered") || uiState.result.result.contains(
                            "Inconsistent"
                        )
                    )
                        MaterialTheme.statusColorScheme.warningContainer
                    else
                        MaterialTheme.colorScheme.surfaceContainer
                }
            }
        }

        is ScanUIState.Error -> MaterialTheme.colorScheme.errorContainer
        is ScanUIState.Loading -> MaterialTheme.colorScheme.surfaceContainer
        is ScanUIState.Idle -> MaterialTheme.colorScheme.surfaceContainer
    }
    Box(
        modifier = modifier
            .background(backgroundColor)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        val text = when (uiState) {
            is ScanUIState.Success -> uiState.result.result
            is ScanUIState.Error -> uiState.message
            is ScanUIState.Loading -> "LOADING..."
            is ScanUIState.Idle -> "Point camera at a barcode"
        }

        val color = when (uiState) {
            is ScanUIState.Success -> {
                when (uiState.result.result) {
                    "Valid" -> MaterialTheme.statusColorScheme.onSuccessContainer
                    "Already Scanned" -> MaterialTheme.colorScheme.onErrorContainer
                    "Not in our System" -> MaterialTheme.statusColorScheme.onWarningContainer
                    "Valid Foreign Card" -> MaterialTheme.statusColorScheme.onWarningContainer
                    "Expired" -> MaterialTheme.colorScheme.onErrorContainer
                    else -> {
                        if (uiState.result.result.contains("Not Registered") || uiState.result.result.contains(
                                "Inconsistent"
                            )
                        )
                            MaterialTheme.statusColorScheme.onWarningContainer
                        else
                            MaterialTheme.colorScheme.onSurface
                    }
                }
            }

            is ScanUIState.Error -> MaterialTheme.colorScheme.onErrorContainer
            is ScanUIState.Loading -> MaterialTheme.colorScheme.onSurface
            is ScanUIState.Idle -> MaterialTheme.colorScheme.onSurface
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