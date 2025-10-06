package com.andymic.esnscanner.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.andymic.esnscanner.models.UpdateUIState
import com.andymic.esnscanner.models.UpdateViewModel
import com.andymic.esnscanner.ui.theme.statusColorScheme

@Composable
fun UpdateIndicator(
    modifier: Modifier = Modifier,
    viewModel: UpdateViewModel = viewModel()
) {
    val uiState by viewModel.state.collectAsState()

    Box(modifier = modifier) {
        when (uiState) {
            is UpdateUIState.Idle -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            is UpdateUIState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Loading Update",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            is UpdateUIState.Success -> {
                val isUpdateAvailable = (uiState as UpdateUIState.Success).result.isUpdateAvailable

                val backgroundColor =
                    if (isUpdateAvailable)
                        MaterialTheme.statusColorScheme.warningContainer
                    else
                        MaterialTheme.colorScheme.surfaceContainerHigh

                val textColor =
                    if (isUpdateAvailable)
                        MaterialTheme.statusColorScheme.onWarningContainer
                    else
                        MaterialTheme.colorScheme.onSurface

                val text =
                    if (isUpdateAvailable)
                        "Update Available"
                    else
                        "No Update Available"

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = backgroundColor)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .clickable {
                            viewModel.startUpdateFlow()
                        }
                ) {
                    Text(
                        text = text,
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            is UpdateUIState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Unable to Update",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}