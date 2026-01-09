package org.esncy.esnscanner.ui.components.status

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.esncy.esnscanner.models.StatusUIState
import org.esncy.esnscanner.ui.theme.ESNCyan
import org.esncy.esnscanner.ui.theme.ESNDarkBlue

@Composable
fun StatusTopBox(uiState: StatusUIState, modifier: Modifier) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp)
    ) {
        if (uiState is StatusUIState.Success) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = uiState.result.cardNumber,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }
        if (uiState is StatusUIState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize(0.25f)
                    .align(Alignment.Center),
                color = ESNCyan,
                trackColor = ESNDarkBlue,
                strokeWidth = 6.dp
            )
        }
    }
}