package com.andymic.esnscanner.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.andymic.esnscanner.ui.Destination
import com.andymic.esnscanner.ui.Destinations

@Composable
fun NavigationRail(
    modifier: Modifier = Modifier,
    selectedDestination: Destination,
    onDestinationSelected: (Destination) -> Unit
) {
    Box(
        modifier = modifier.padding(horizontal = 8.dp).padding(WindowInsets.safeDrawing.asPaddingValues())
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 4.dp
        ) {
            NavigationRail(contentColor = Color.Transparent) {
                Destinations.entries.forEachIndexed { index, destination ->
                    NavigationRailItem(
                        selected = selectedDestination == destination.spec,
                        onClick = {
                            onDestinationSelected(destination.spec)
                        },
                        icon = {
                            Icon(
                                destination.spec.icon,
                                contentDescription = destination.spec.contentDescription
                            )
                        },
                        label = { Text(destination.spec.label) }
                    )
                }
            }
        }
    }
}
