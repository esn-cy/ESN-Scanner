package com.andymic.esnscanner.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andymic.esnscanner.models.OnlineUIState
import com.andymic.esnscanner.models.OnlineViewModel
import com.andymic.esnscanner.ui.screens.StatusRow
import com.andymic.esnscanner.ui.theme.statusColorScheme

@Composable
fun OnlineIndicator(modifier: Modifier = Modifier, viewModel: OnlineViewModel = viewModel()) {
    val uiState by viewModel.state.collectAsState()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (uiState) {
            is OnlineUIState.Loading -> {
                StatusRow(label = "Local Status", status = "LOADING...")
                StatusRow(label = "International Status", status = "LOADING...")
                StatusRow(label = "Dataset Status", status = "LOADING...")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "LOADING...",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            is OnlineUIState.Success -> {
                val serviceStatus = (uiState as OnlineUIState.Success).result.serviceStatus
                val isLocalOnline = (uiState as OnlineUIState.Success).result.isLocalOnline
                val isInternationalOnline =
                    (uiState as OnlineUIState.Success).result.isInternationalOnline
                val isDatasetOnline =
                    (uiState as OnlineUIState.Success).result.isDatasetOnline

                StatusRow(
                    label = "Local Status",
                    status = if (isLocalOnline) "Online" else "Offline"
                )
                StatusRow(
                    label = "International Status",
                    status = if (isInternationalOnline) "Online" else "Offline"
                )
                StatusRow(
                    label = "Dataset Status",
                    status = if (isDatasetOnline) "Online" else "Offline"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            color =
                                if (serviceStatus == "Full Information Available")
                                    MaterialTheme.colorScheme.surfaceContainerHigh
                                else if (serviceStatus.endsWith("Only"))
                                    MaterialTheme.statusColorScheme.warningContainer
                                else MaterialTheme.colorScheme.errorContainer
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = serviceStatus,
                        color =
                            if (serviceStatus == "Full Information Available")
                                MaterialTheme.colorScheme.onSurface
                            else if (serviceStatus.endsWith("Only"))
                                MaterialTheme.statusColorScheme.onSuccessContainer
                            else MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                if (serviceStatus == "No Information Available") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(color = MaterialTheme.colorScheme.error)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .clickable {
                                viewModel.runTest()
                            }
                    ) {
                        Text(
                            text = "Rerun Test",
                            color = MaterialTheme.colorScheme.onError,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }

            is OnlineUIState.Idle -> {
                StatusRow(label = "Local Status", status = "-")
                StatusRow(label = "International Status", status = "-")
                StatusRow(label = "Dataset Status", status = "-")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "LOADING...",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}