package com.andymic.esnscanner.ui.components.produce

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andymic.esnscanner.models.ProduceUIState
import com.andymic.esnscanner.ui.theme.ESNCyan
import com.andymic.esnscanner.ui.theme.ESNDarkBlue

@Composable
fun ProduceTopBox(uiState: ProduceUIState, modifier: Modifier) {
    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.6f))
            .padding(16.dp)
    ) {
        if (uiState is ProduceUIState.Success) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = uiState.result.cardNumber,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }
        if (uiState is ProduceUIState.Loading) {
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