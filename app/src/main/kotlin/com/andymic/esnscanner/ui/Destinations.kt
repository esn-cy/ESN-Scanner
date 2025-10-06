package com.andymic.esnscanner.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.ui.graphics.vector.ImageVector

data class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String,
    val enabledCondition: String? = null
)

enum class Destinations(
    val spec: Destination
) {
    Home(
        spec = Destination(
            route = "home",
            icon = Icons.Default.Home,
            label = "Home",
            contentDescription = "Navigate to Home Screen"
        )
    ),
    Scan(
        spec = Destination(
            route = "scan",
            icon = Icons.Default.CameraAlt,
            label = "Scan",
            contentDescription = "Navigate to Scan Card Screen",
            enabledCondition = "oneOnline"
        )
    ),
    Add(
        spec = Destination(
            route = "add",
            icon = Icons.Default.AddCard,
            label = "Add",
            contentDescription = "Navigate to Add Card Screen",
            enabledCondition = "isLocalOnline"
        )
    ),
    Produce(
        spec = Destination(
            route = "produce",
            icon = Icons.Default.Edit,
            label = "Produce",
            contentDescription = "Navigate to Produce Card Screen",
            enabledCondition = "isLocalOnline"
        )
    ),
    Deliver(
        spec = Destination(
            route = "deliver",
            icon = Icons.Default.LocalShipping,
            label = "Deliver",
            contentDescription = "Navigate to Deliver Card Screen",
            enabledCondition = "isLocalOnline"
        )
    )
}