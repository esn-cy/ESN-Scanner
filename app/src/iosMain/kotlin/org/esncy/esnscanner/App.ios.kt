package org.esncy.esnscanner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import coil3.PlatformContext
import org.esncy.esnscanner.models.SectionDataViewModel
import org.esncy.esnscanner.models.TokenViewModel
import org.esncy.esnscanner.models.UpdateViewModel

fun MainViewController() = ComposeUIViewController {
    val updateViewModel = remember { UpdateViewModel() }
    val sectionDataViewModel = remember { SectionDataViewModel() }
    val tokenViewModel = remember { TokenViewModel(sectionDataViewModel.dataFlow) }

    App(updateViewModel, sectionDataViewModel, tokenViewModel, null)
}

@Composable
actual fun getPlatformContext(): Any? = PlatformContext.INSTANCE