package com.andymic.esnscanner.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andymic.esnscanner.models.OnlineViewModel
import com.andymic.esnscanner.models.UpdateViewModel
import com.andymic.esnscanner.ui.components.home.OnlineIndicator
import com.andymic.esnscanner.ui.components.home.UpdateIndicator

@Composable
fun HomeScreen(
    onlineViewModel: OnlineViewModel = viewModel(),
    updateViewModel: UpdateViewModel = viewModel()
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))

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