package org.esncy.esnscanner.data

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.android.Android

actual val engine: HttpClientEngineFactory<*> = Android