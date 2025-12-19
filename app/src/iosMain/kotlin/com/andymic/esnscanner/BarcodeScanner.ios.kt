package com.andymic.esnscanner

import cnames.structs.opaqueCMSampleBuffer
import cocoapods.GoogleMLKit.MLKBarcode
import cocoapods.GoogleMLKit.MLKBarcodeFormatCode128
import cocoapods.GoogleMLKit.MLKBarcodeFormatQRCode
import cocoapods.GoogleMLKit.MLKBarcodeScanner
import cocoapods.GoogleMLKit.MLKBarcodeScannerOptions
import cocoapods.GoogleMLKit.MLKVisionImage
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import objcnames.protocols.MLKCompatibleImageProtocol
import platform.UIKit.UIImageOrientation
import kotlin.native.runtime.GC
import kotlin.native.runtime.NativeRuntimeApi

actual class BarcodeScanner actual constructor(private val onBarcodesScanned: (List<String>) -> Unit) {
    @OptIn(ExperimentalForeignApi::class)
    private val options = MLKBarcodeScannerOptions(
        formats = MLKBarcodeFormatCode128 or MLKBarcodeFormatQRCode
    )

    @OptIn(ExperimentalForeignApi::class)
    private val scanner = MLKBarcodeScanner.barcodeScannerWithOptions(options)

    @OptIn(ExperimentalForeignApi::class, NativeRuntimeApi::class)
    actual fun analyze(image: Any) {
        val visionImage = MLKVisionImage(image as CPointer<opaqueCMSampleBuffer>?)
        visionImage.orientation = UIImageOrientation.UIImageOrientationRight

        scanner.processImage(visionImage as MLKCompatibleImageProtocol) { barcodes, error ->
            if (error != null) {
                println("Error scanning: ${error.localizedDescription}")
                return@processImage
            }

            if (barcodes != null && barcodes.isNotEmpty()) {
                val results = barcodes.mapNotNull {
                    (it as? MLKBarcode)?.rawValue
                }

                if (results.isNotEmpty()) {
                    onBarcodesScanned(results)
                }
            }
            GC.collect()
        }
    }
}