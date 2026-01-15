package org.esncy.esnscanner.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
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
import org.esncy.esnscanner.models.OnlineUIState
import org.esncy.esnscanner.models.OnlineViewModel
import org.esncy.esnscanner.models.TokenUIState
import org.esncy.esnscanner.models.TokenViewModel
import org.esncy.esnscanner.ui.Destination
import org.esncy.esnscanner.ui.Destinations
import org.jetbrains.compose.resources.vectorResource

@Composable
fun NavigationRail(
    modifier: Modifier = Modifier,
    selectedDestination: Destination,
    onDestinationSelected: (Destination) -> Unit,
    onlineViewModel: OnlineViewModel,
    tokenViewModel: TokenViewModel
) {
    val onlineState by onlineViewModel.state.collectAsState()
    val tokenState by tokenViewModel.state.collectAsState()

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
                for (destination in Destinations.entries) {
                    if (destination.spec.permission != null && destination.spec.enabledCondition == "isLocalOnline") {
                        val userPermissions =
                            (tokenState as? TokenUIState.Success)?.result?.permissions
                        if (userPermissions != null) {
                            if (!userPermissions.contains(destination.spec.permission))
                                continue
                        }
                    }

                    val isEnabled = when (onlineState) {
                        is OnlineUIState.Idle, is OnlineUIState.Loading -> {
                            when (destination.spec.enabledCondition) {
                                null -> true
                                else -> false
                            }
                        }
                        is OnlineUIState.Success -> {
                            when (destination.spec.enabledCondition) {
                                null -> true
                                "isLocalOnline" -> (onlineState as OnlineUIState.Success).result.isLocalOnline
                                "isInternationalOnline" -> (onlineState as OnlineUIState.Success).result.isInternationalOnline
                                "isDatasetOnline" -> (onlineState as OnlineUIState.Success).result.isDatasetOnline
                                "oneOnline" -> (onlineState as OnlineUIState.Success).result.serviceStatus != "No Information Available"
                                else -> false
                            }
                        }
                    }

                    val isSelected = selectedDestination == destination.spec

                    if (destination.spec.route == "settings")
                        Spacer(Modifier.weight(1f))

                    NavigationRailItem(
                        selected = isSelected,
                        onClick = {
                            if (isEnabled) {
                                onDestinationSelected(destination.spec)
                            }
                        },
                        icon = {
                            Icon(
                                if (isSelected) vectorResource(destination.spec.icons.second) else vectorResource(
                                    destination.spec.icons.first
                                ),
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
