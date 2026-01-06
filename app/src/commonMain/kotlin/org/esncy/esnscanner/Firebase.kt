package org.esncy.esnscanner

expect class Firebase {
    class Trace(name: String) {
        var trace: Any

        fun start()

        fun stop()

        fun putMetric(name: String, value: Long)
    }
}