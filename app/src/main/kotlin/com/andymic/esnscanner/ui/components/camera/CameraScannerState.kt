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
class CameraScannerState(
    val lifecycleOwner: LifecycleOwner
) {
    var camera: Camera? by mutableStateOf(null)
        private set
    var isFlashOn: Boolean by mutableStateOf(false)
        private set
    val hasFlashUnit: Boolean
        get() = camera?.cameraInfo?.hasFlashUnit() == true

    /**
     * Toggles the camera's torch (flash).
     */
    fun toggleFlash() {
        if (hasFlashUnit) {
            isFlashOn = !isFlashOn
            camera?.cameraControl?.enableTorch(isFlashOn)
        }
    }

    internal fun onCameraInitialized(cameraInstance: Camera) {
        this.camera = cameraInstance
    }
}

/**
 * Creates and remembers a [CameraScannerState].
 */
@Composable
fun rememberCameraScannerState(
    lifecycleOwner: LifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
): CameraScannerState {
    return remember(lifecycleOwner) {
        CameraScannerState(lifecycleOwner)
    }
}