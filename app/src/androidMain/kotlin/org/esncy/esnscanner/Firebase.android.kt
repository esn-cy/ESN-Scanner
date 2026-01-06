package org.esncy.esnscanner

import com.google.firebase.perf.performance

actual class Firebase {
    actual class Trace actual constructor(name: String) {

        actual var trace: Any = com.google.firebase.Firebase.performance.newTrace(name)

        actual fun start() {
            (trace as com.google.firebase.perf.metrics.Trace).start()
        }

        actual fun stop() {
            (trace as com.google.firebase.perf.metrics.Trace).stop()
        }

        actual fun putMetric(name: String, value: Long) {
            (trace as com.google.firebase.perf.metrics.Trace).putMetric(name, value)
        }
    }
}