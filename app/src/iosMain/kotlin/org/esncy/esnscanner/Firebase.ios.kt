package org.esncy.esnscanner

import cocoapods.FirebasePerformance.FIRPerformance
import cocoapods.FirebasePerformance.FIRTrace
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual class Firebase {
    actual class Trace actual constructor(name: String) {
        actual var trace: Any = FIRPerformance.startTraceWithName(name)!!

        actual fun start() {
            (trace as FIRTrace).start()
        }

        actual fun stop() {
            (trace as FIRTrace).stop()
        }

        actual fun putMetric(name: String, value: Long) {
            (trace as FIRTrace).setIntValue(value, name)
        }
    }
}