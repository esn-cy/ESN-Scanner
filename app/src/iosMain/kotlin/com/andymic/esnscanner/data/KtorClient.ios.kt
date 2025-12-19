package com.andymic.esnscanner.data

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

actual val engine: HttpClientEngineFactory<*> = Darwin