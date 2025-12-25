package org.esncy.esnscanner.ui.components.camera

import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * A state holder for managing the camera, including the flash.
 */
interface CameraScannerState {
    val isFlashOn: Boolean
    val lifecycleOwner: LifecycleOwner
    fun toggleFlash()
}

/**
 * Creates and remembers a [CameraScannerState].
 */
@Composable
expect fun rememberCameraScannerState(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
): CameraScannerState