package org.esncy.esnscanner.ui.components.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import org.esncy.esnscanner.BarcodeScanner
import platform.AVFoundation.AVCaptureConnection
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureOutput
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureTorchModeOff
import platform.AVFoundation.AVCaptureTorchModeOn
import platform.AVFoundation.AVCaptureVideoDataOutput
import platform.AVFoundation.AVCaptureVideoDataOutputSampleBufferDelegateProtocol
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.hasTorch
import platform.AVFoundation.torchMode
import platform.CoreMedia.CMSampleBufferRef
import platform.CoreVideo.kCVPixelFormatType_32BGRA
import platform.QuartzCore.CATransaction
import platform.UIKit.UIColor
import platform.UIKit.UIView
import platform.darwin.DISPATCH_QUEUE_PRIORITY_DEFAULT
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_global_queue
import platform.darwin.dispatch_queue_create

@Composable
actual fun CameraScanner(
    onBarcodesScanned: (List<String>) -> Unit,
    state: CameraScannerState,
    modifier: Modifier
) {
    val cameraView = remember { CameraPreviewView(onBarcodesScanned) }

    UIKitView(
        factory = { cameraView },
        modifier = modifier,
        update = { view ->
            view.toggleFlash(state.isFlashOn)
        }
    )
}

@OptIn(ExperimentalForeignApi::class)
private class CameraPreviewView(
    private val onScanned: (List<String>) -> Unit
) : UIView(frame = platform.CoreGraphics.CGRectZero.readValue()) {

    private val captureSession = AVCaptureSession()
    private val barcodeScanner = BarcodeScanner { codes ->
        onScanned(codes)
    }

    private val cameraDelegate =
        object : NSObject(), AVCaptureVideoDataOutputSampleBufferDelegateProtocol {
            override fun captureOutput(
                output: AVCaptureOutput,
                didOutputSampleBuffer: CMSampleBufferRef?,
                fromConnection: AVCaptureConnection
            ) {
                didOutputSampleBuffer?.let {
                    barcodeScanner.analyze(it)
                }
            }
        }

    private val cameraQueue = dispatch_queue_create("org.esncy.esnscanner.camera", null)

    private var previewLayer: AVCaptureVideoPreviewLayer

    init {
        backgroundColor = UIColor.blackColor

        previewLayer = AVCaptureVideoPreviewLayer(session = captureSession)
        previewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill
        layer.addSublayer(previewLayer)

        setupCamera()
    }

    private fun setupCamera() {
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT.toLong(), 0u)) {
            captureSession.beginConfiguration()

            val device = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo)
            if (device == null) {
                println("Error: No video device available.")
                return@dispatch_async
            }

            val input = AVCaptureDeviceInput.deviceInputWithDevice(device, null)
            if (input == null) {
                println("Error: Could not create input from device.")
                return@dispatch_async
            }

            if (captureSession.canAddInput(input))
                captureSession.addInput(input)
            else {
                println("Error: Cannot add input to session.")
                return@dispatch_async
            }

            val videoOutput = AVCaptureVideoDataOutput()

            videoOutput.videoSettings = mapOf(
                "PixelFormatType" to kCVPixelFormatType_32BGRA
            )
            videoOutput.alwaysDiscardsLateVideoFrames = true

            videoOutput.setSampleBufferDelegate(cameraDelegate, cameraQueue)

            if (captureSession.canAddOutput(videoOutput))
                captureSession.addOutput(videoOutput)
            else {
                println("Error: Cannot add output to session.")
                return@dispatch_async
            }

            captureSession.commitConfiguration()
            captureSession.startRunning()
            println("Camera Session Started.")
        }
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        CATransaction.begin()
        CATransaction.setDisableActions(true)
        previewLayer.frame = bounds
        CATransaction.commit()
    }

    fun toggleFlash(on: Boolean) {
        val device = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo) ?: return
        if (device.hasTorch) {
            try {
                device.lockForConfiguration(null)
                device.torchMode = if (on) AVCaptureTorchModeOn else AVCaptureTorchModeOff
                device.unlockForConfiguration()
            } catch (_: Exception) {
            }
        }
    }
}