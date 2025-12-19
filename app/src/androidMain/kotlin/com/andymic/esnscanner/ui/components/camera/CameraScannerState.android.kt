package com.andymic.esnscanner.ui.components.camera

import androidx.camera.core.Camera
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.LifecycleOwner

/**
 * A state holder for managing the camera, including the flash.
 */
class AndroidCameraScannerState(
    override val lifecycleOwner: LifecycleOwner
) : CameraScannerState {
    var camera: Camera? by mutableStateOf(null)
        private set
    override var isFlashOn: Boolean by mutableStateOf(false)
        private set
    val hasFlashUnit: Boolean
        get() = camera?.cameraInfo?.hasFlashUnit() == true

    /**
     * Toggles the camera's torch (flash).
     */
    override fun toggleFlash() {
        if (hasFlashUnit) {
            isFlashOn = !isFlashOn
            camera?.cameraControl?.enableTorch(isFlashOn)
        }
    }

    fun onCameraInitialized(cameraInstance: Camera) {
        this.camera = cameraInstance
        isFlashOn = cameraInstance.cameraInfo.torchState.value == 1
    }
}

/**
 * Creates and remembers a [CameraScannerState].
 */
@Composable
actual fun rememberCameraScannerState(lifecycleOwner: LifecycleOwner): CameraScannerState {
    return remember(lifecycleOwner) {
        AndroidCameraScannerState(lifecycleOwner)
    }
}