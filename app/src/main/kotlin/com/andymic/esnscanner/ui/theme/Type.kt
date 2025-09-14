package com.andymic.esnscanner.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.andymic.esnscanner.R

val LatoFont = FontFamily(
    Font(R.font.lato_thin, FontWeight.Thin, FontStyle.Normal),
    Font(R.font.lato_thin_italic, FontWeight.Thin, FontStyle.Italic),
    Font(R.font.lato_light, FontWeight.Light, FontStyle.Normal),
    Font(R.font.lato_light_italic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.lato_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.lato_regular_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.lato_bold, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.lato_bold_italic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.lato_black, FontWeight.Black, FontStyle.Normal),
    Font(R.font.lato_black_italic, FontWeight.Black, FontStyle.Italic),
)

val KelsonFont = FontFamily(
    Font(R.font.kelson_thin, FontWeight.Thin, FontStyle.Normal),
    Font(R.font.kelson_light, FontWeight.Light, FontStyle.Normal),
    Font(R.font.kelson_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.kelson_medium, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.kelson_bold, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.kelson_extra_bold, FontWeight.ExtraBold, FontStyle.Normal),
)

val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = KelsonFont),
    displayMedium = baseline.displayMedium.copy(fontFamily = KelsonFont),
    displaySmall = baseline.displaySmall.copy(fontFamily = KelsonFont),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = KelsonFont),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = KelsonFont),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = KelsonFont),
    titleLarge = baseline.titleLarge.copy(fontFamily = KelsonFont),
    titleMedium = baseline.titleMedium.copy(fontFamily = KelsonFont),
    titleSmall = baseline.titleSmall.copy(fontFamily = KelsonFont),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = LatoFont),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = LatoFont),
    bodySmall = baseline.bodySmall.copy(fontFamily = LatoFont),
    labelLarge = baseline.labelLarge.copy(fontFamily = LatoFont),
    labelMedium = baseline.labelMedium.copy(fontFamily = LatoFont),
    labelSmall = baseline.labelSmall.copy(fontFamily = LatoFont),
)

