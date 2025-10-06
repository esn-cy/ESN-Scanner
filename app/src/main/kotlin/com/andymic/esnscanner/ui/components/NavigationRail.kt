package com.andymic.esnscanner.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.andymic.esnscanner.models.ConnectionUIState
import com.andymic.esnscanner.models.ConnectionViewModel
import com.andymic.esnscanner.ui.Destination
import com.andymic.esnscanner.ui.Destinations

@Composable
fun NavigationRail(
    modifier: Modifier = Modifier,
    selectedDestination: Destination,
    onDestinationSelected: (Destination) -> Unit,
    viewModel: ConnectionViewModel
) {
    val uiState by viewModel.state.collectAsState()

    Box(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .padding(WindowInsets.safeDrawing.asPaddingValues())
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            NavigationRail(contentColor = Color.Transparent) {
                Destinations.entries.forEach { destination ->
                    val isEnabled = when (uiState) {
                        is ConnectionUIState.Idle, is ConnectionUIState.Loading -> false
                        is ConnectionUIState.Success -> {
                            when (destination.spec.enabledCondition) {
                                null -> true
                                "isLocalOnline" -> (uiState as ConnectionUIState.Success).result.isLocalOnline
                                "isInternationalOnline" -> (uiState as ConnectionUIState.Success).result.isInternationalOnline
                                "isDatasetOnline" -> (uiState as ConnectionUIState.Success).result.isDatasetOnline
                                "oneOnline" -> (uiState as ConnectionUIState.Success).result.serviceStatus != "No Information Available"
                                else -> false
                            }
                        }
                    }

                    NavigationRailItem(
                        selected = selectedDestination == destination.spec,
                        onClick = {
                            if (isEnabled) {
                                onDestinationSelected(destination.spec)
                            }
                        },
                        icon = {
                            Icon(
                                destination.spec.icon,
                                contentDescription = destination.spec.contentDescription
                            )
                        },
                        label = { Text(destination.spec.label) },
                        colors =
                            if (isEnabled) {
                                NavigationRailItemDefaults.colors()
                            } else {
                                NavigationRailItemDefaults.colors(
                                    unselectedIconColor = MaterialTheme.colorScheme.error.copy(
                                        alpha = 0.38f
                                    ),
                                    unselectedTextColor = MaterialTheme.colorScheme.error.copy(
                                        alpha = 0.38f
                                    )
                                )
                            }
                    )
                }
            }
        }
    }
}
