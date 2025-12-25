package org.esncy.esnscanner.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import esnscanner.app.generated.resources.Res
import esnscanner.app.generated.resources.kelson_bold
import esnscanner.app.generated.resources.kelson_extra_bold
import esnscanner.app.generated.resources.kelson_light
import esnscanner.app.generated.resources.kelson_medium
import esnscanner.app.generated.resources.kelson_regular
import esnscanner.app.generated.resources.kelson_thin
import esnscanner.app.generated.resources.lato_black
import esnscanner.app.generated.resources.lato_black_italic
import esnscanner.app.generated.resources.lato_bold
import esnscanner.app.generated.resources.lato_bold_italic
import esnscanner.app.generated.resources.lato_light
import esnscanner.app.generated.resources.lato_light_italic
import esnscanner.app.generated.resources.lato_regular
import esnscanner.app.generated.resources.lato_regular_italic
import esnscanner.app.generated.resources.lato_thin
import esnscanner.app.generated.resources.lato_thin_italic
import org.jetbrains.compose.resources.Font

@Composable
fun type(): Typography {
    val LatoFont = FontFamily(
        Font(Res.font.lato_thin, FontWeight.Thin, FontStyle.Normal),
        Font(Res.font.lato_thin_italic, FontWeight.Thin, FontStyle.Italic),
        Font(Res.font.lato_light, FontWeight.Light, FontStyle.Normal),
        Font(Res.font.lato_light_italic, FontWeight.Light, FontStyle.Italic),
        Font(Res.font.lato_regular, FontWeight.Normal, FontStyle.Normal),
        Font(Res.font.lato_regular_italic, FontWeight.Normal, FontStyle.Italic),
        Font(Res.font.lato_bold, FontWeight.Bold, FontStyle.Normal),
        Font(Res.font.lato_bold_italic, FontWeight.Bold, FontStyle.Italic),
        Font(Res.font.lato_black, FontWeight.Black, FontStyle.Normal),
        Font(Res.font.lato_black_italic, FontWeight.Black, FontStyle.Italic),
    )

    val KelsonFont = FontFamily(
        Font(Res.font.kelson_thin, FontWeight.Thin, FontStyle.Normal),
        Font(Res.font.kelson_light, FontWeight.Light, FontStyle.Normal),
        Font(Res.font.kelson_regular, FontWeight.Normal, FontStyle.Normal),
        Font(Res.font.kelson_medium, FontWeight.Medium, FontStyle.Normal),
        Font(Res.font.kelson_bold, FontWeight.Bold, FontStyle.Normal),
        Font(Res.font.kelson_extra_bold, FontWeight.ExtraBold, FontStyle.Normal),
    )

    val baseline = Typography()

    return Typography(
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
}

