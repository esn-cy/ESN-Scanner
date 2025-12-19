package com.andymic.esnscanner

expect class BarcodeScanner(onBarcodesScanned: (List<String>) -> Unit) {
    fun analyze(image: Any)
}