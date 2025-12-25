package org.esncy.esnscanner.ui

import esnscanner.app.generated.resources.Res
import esnscanner.app.generated.resources.add_card_filled
import esnscanner.app.generated.resources.add_card_outlined
import esnscanner.app.generated.resources.camera_filled
import esnscanner.app.generated.resources.camera_outlined
import esnscanner.app.generated.resources.edit_filled
import esnscanner.app.generated.resources.edit_outlined
import esnscanner.app.generated.resources.hand_package_filled
import esnscanner.app.generated.resources.hand_package_outlined
import esnscanner.app.generated.resources.home_filled
import esnscanner.app.generated.resources.home_outlined
import org.jetbrains.compose.resources.DrawableResource

data class Destination(
    val route: String,
    val label: String,
    val icons: Pair<DrawableResource, DrawableResource>,
    val contentDescription: String,
    val enabledCondition: String? = null
)

enum class Destinations(
    val spec: Destination
) {
    Home(
        spec = Destination(
            route = "home",
            icons = Pair(Res.drawable.home_outlined, Res.drawable.home_filled),
            label = "Home",
            contentDescription = "Navigate to Home Screen"
        )
    ),
    Scan(
        spec = Destination(
            route = "scan",
            icons = Pair(Res.drawable.camera_outlined, Res.drawable.camera_filled),
            label = "Scan",
            contentDescription = "Navigate to Scan Card Screen",
            enabledCondition = "oneOnline"
        )
    ),
    Add(
        spec = Destination(
            route = "add",
            icons = Pair(Res.drawable.add_card_outlined, Res.drawable.add_card_filled),
            label = "Add",
            contentDescription = "Navigate to Add Card Screen",
            enabledCondition = "isLocalOnline"
        )
    ),
    Produce(
        spec = Destination(
            route = "produce",
            icons = Pair(Res.drawable.edit_outlined, Res.drawable.edit_filled),
            label = "Produce",
            contentDescription = "Navigate to Produce Card Screen",
            enabledCondition = "isLocalOnline"
        )
    ),
    Deliver(
        spec = Destination(
            route = "deliver",
            icons = Pair(Res.drawable.hand_package_outlined, Res.drawable.hand_package_filled),
            label = "Deliver",
            contentDescription = "Navigate to Deliver Card Screen",
            enabledCondition = "isLocalOnline"
        )
    )
}