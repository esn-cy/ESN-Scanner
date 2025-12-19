package com.andymic.esnscanner.ui.components.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.LifecycleOwner

@Composable
actual fun rememberCameraScannerState(lifecycleOwner: LifecycleOwner): CameraScannerState {
    return remember {
        object : CameraScannerState {
            override var isFlashOn by mutableStateOf(false)
            override val lifecycleOwner: LifecycleOwner = lifecycleOwner
            override fun toggleFlash() {
                isFlashOn = !isFlashOn
            }
        }
    }
}