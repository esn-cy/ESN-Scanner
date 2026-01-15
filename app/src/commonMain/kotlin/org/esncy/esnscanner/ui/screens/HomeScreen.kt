package org.esncy.esnscanner.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.esncy.esnscanner.models.OnlineViewModel
import org.esncy.esnscanner.models.UpdateViewModel
import org.esncy.esnscanner.ui.components.home.OnlineIndicator
import org.esncy.esnscanner.ui.components.home.UpdateIndicator

@Composable
fun HomeScreen(
    onlineViewModel: OnlineViewModel,
    updateViewModel: UpdateViewModel,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        UpdateIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            viewModel = updateViewModel
        )
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        OnlineIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            viewModel = onlineViewModel
        )
    }
}