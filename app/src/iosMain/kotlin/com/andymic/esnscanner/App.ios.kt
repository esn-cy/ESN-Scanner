package com.andymic.esnscanner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.andymic.esnscanner.models.SectionDataViewModel
import com.andymic.esnscanner.models.UpdateViewModel

fun MainViewController() = ComposeUIViewController {
    val updateViewModel = remember { UpdateViewModel() }
    val sectionDataViewModel = remember { SectionDataViewModel() }

    App(updateViewModel, sectionDataViewModel)
}

@Composable
actual fun getPlatformContext(): Any? = null