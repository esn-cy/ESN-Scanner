package com.andymic.esnscanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andymic.esnscanner.models.OnlineViewModel
import com.andymic.esnscanner.ui.components.home.OnlineIndicator
import com.andymic.esnscanner.ui.theme.statusColorScheme

@Preview
@Composable
fun HomeScreen(viewModel: OnlineViewModel = viewModel()) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        OnlineIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            viewModel = viewModel
        )
    }
}

@Composable
fun StatusRow(label: String, status: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (status != "Offline") MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.errorContainer)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
        Spacer(Modifier.weight(1f))
        StatusText(status = status)
    }
}

@Composable
fun StatusText(status: String) {
    val textColor = when (status) {
        "LOADING..." -> MaterialTheme.colorScheme.onSurface
        "Online" -> MaterialTheme.statusColorScheme.success
        "Offline" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }
    Text(
        text = status,
        color = textColor,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    )
}