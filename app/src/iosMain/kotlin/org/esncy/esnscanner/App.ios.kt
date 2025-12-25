package org.esncy.esnscanner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import org.esncy.esnscanner.models.SectionDataViewModel
import org.esncy.esnscanner.models.UpdateViewModel

fun MainViewController() = ComposeUIViewController {
    val updateViewModel = remember { UpdateViewModel() }
    val sectionDataViewModel = remember { SectionDataViewModel() }

    App(updateViewModel, sectionDataViewModel)
}

@Composable
actual fun getPlatformContext(): Any? = null