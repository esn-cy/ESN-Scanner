package org.esncy.esnscanner.ui

import esnscanner.app.generated.resources.Res
import esnscanner.app.generated.resources.add_card_filled
import esnscanner.app.generated.resources.add_card_outlined
import esnscanner.app.generated.resources.block_filled
import esnscanner.app.generated.resources.block_outlined
import esnscanner.app.generated.resources.camera_filled
import esnscanner.app.generated.resources.camera_outlined
import esnscanner.app.generated.resources.contract_edit_filled
import esnscanner.app.generated.resources.contract_edit_outlined
import esnscanner.app.generated.resources.edit_filled
import esnscanner.app.generated.resources.edit_outlined
import esnscanner.app.generated.resources.hand_package_filled
import esnscanner.app.generated.resources.hand_package_outlined
import esnscanner.app.generated.resources.home_filled
import esnscanner.app.generated.resources.home_outlined
import esnscanner.app.generated.resources.paid_filled
import esnscanner.app.generated.resources.paid_outlined
import esnscanner.app.generated.resources.settings_filled
import esnscanner.app.generated.resources.settings_outlined
import org.jetbrains.compose.resources.DrawableResource

data class Destination(
    val route: String,
    val label: String,
    val icons: Pair<DrawableResource, DrawableResource>,
    val contentDescription: String,
    val enabledCondition: String? = null,
    val permission: String? = null
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
            enabledCondition = "oneOnline",
            permission = "scan cards"
        )
    ),
    Add(
        spec = Destination(
            route = "add",
            icons = Pair(Res.drawable.add_card_outlined, Res.drawable.add_card_filled),
            label = "Add",
            contentDescription = "Navigate to Add Card Screen",
            enabledCondition = "isLocalOnline",
            permission = "manage esncard numbers"
        )
    ),
    MarkAsPaid(
        spec = Destination(
            route = "markAsPaid",
            icons = Pair(Res.drawable.paid_outlined, Res.drawable.paid_filled),
            label = "Mark as Paid",
            contentDescription = "Navigate to Mark as Paid Screen",
            enabledCondition = "isLocalOnline",
            permission = "mark submission as paid"
        )
    ),
    Issue(
        spec = Destination(
            route = "issue",
            icons = Pair(Res.drawable.edit_outlined, Res.drawable.edit_filled),
            label = "Issue",
            contentDescription = "Navigate to Issue Card Screen",
            enabledCondition = "isLocalOnline",
            permission = "issue card"
        )
    ),
    Deliver(
        spec = Destination(
            route = "deliver",
            icons = Pair(Res.drawable.hand_package_outlined, Res.drawable.hand_package_filled),
            label = "Deliver",
            contentDescription = "Navigate to Deliver Card Screen",
            enabledCondition = "isLocalOnline",
            permission = "deliver card"
        )
    ),
    Blacklist(
        spec = Destination(
            route = "blacklist",
            icons = Pair(Res.drawable.block_outlined, Res.drawable.block_filled),
            label = "Blacklist",
            contentDescription = "Navigate to Blacklist Pass Screen",
            enabledCondition = "isLocalOnline",
            permission = "blacklist pass"
        )
    ),
    Register(
        spec = Destination(
            route = "register",
            icons = Pair(Res.drawable.contract_edit_outlined, Res.drawable.contract_edit_filled),
            label = "Register",
            contentDescription = "Navigate to Register Screen",
            enabledCondition = "isLocalOnline"
        )
    ),
    Settings(
        spec = Destination(
            route = "settings",
            icons = Pair(Res.drawable.settings_outlined, Res.drawable.settings_filled),
            label = "Settings",
            contentDescription = "Navigate to Settings Screen",
            enabledCondition = null
        )
    )
}