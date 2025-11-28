package com.andymic.esnscanner.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class StatusColorScheme(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color
)

val lightStatusColorScheme = StatusColorScheme(
    success = Color(0xFF006D20),
    onSuccess = Color(0xFFFFFFFF),
    successContainer = Color(0xFFD7FFD6),
    onSuccessContainer = Color(0xFF003E00),
    warning = Color(0xFFF9C425),
    onWarning = Color(0xFF000000),
    warningContainer = Color(0xFFFFF176),
    onWarningContainer = Color(0xFF413C0B)
)

val darkStatusColorScheme = StatusColorScheme(
    success = Color(0xFF8DD380),
    onSuccess = Color(0xFF00390E),
    successContainer = Color(0xFF005318),
    onSuccessContainer = Color(0xFFB3F099),
    warning = Color(0xFFFEF08A),
    onWarning = Color(0xFF454700),
    warningContainer = Color(0xFF827717),
    onWarningContainer = Color(0xFFFFF176)
)

val LocalStatusColorScheme = staticCompositionLocalOf {
    lightStatusColorScheme
}

@Suppress("UnusedReceiverParameter")
val MaterialTheme.statusColorScheme: StatusColorScheme
    @Composable
    get() = LocalStatusColorScheme.current