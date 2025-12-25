package org.esncy.esnscanner.models

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle
import platform.Foundation.NSJSONSerialization
import platform.Foundation.NSURL
import platform.Foundation.NSURLSession
import platform.Foundation.dataTaskWithURL
import platform.UIKit.UIApplication
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

actual class UpdateChecker {
    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun checkForUpdate(context: Any?): UpdateResult =
        suspendCoroutine { continuation ->
            val bundleId = NSBundle.mainBundle.bundleIdentifier
            val currentVersion =
                NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String

            if (bundleId == null || currentVersion == null) {
                throw Exception("Could not determine App Bundle ID or Version")
            }

            val urlString = "https://itunes.apple.com/lookup?bundleId=$bundleId"
            val url = NSURL(string = urlString)

            val task = NSURLSession.sharedSession.dataTaskWithURL(url) { data, _, error ->
                if (error != null) {
                    throw Exception(error.localizedDescription)
                }
                if (data == null) {
                    throw Exception("No data received from App Store")
                }

                val json = NSJSONSerialization.JSONObjectWithData(data, 0u, null) as? Map<String, *>
                val results = json?.get("results") as? List<Map<String, *>>
                val appInfo = results?.firstOrNull()

                if (appInfo == null) {
                    continuation.resume(UpdateResult(false, null))
                    return@dataTaskWithURL
                }

                val storeVersion = appInfo["version"] as? String
                val trackViewUrl = appInfo["trackViewUrl"] as? String

                if (storeVersion != null && trackViewUrl != null) {
                    val isAvailable = isUpdateAvailable(currentVersion, storeVersion)

                    continuation.resume(
                        UpdateResult(
                            isUpdateAvailable = isAvailable,
                            updateInfo = trackViewUrl
                        )
                    )
                } else {
                    continuation.resume(UpdateResult(false, null))
                }
            }
            task.resume()
        }

    actual fun startUpdateFlow(
        updateResult: UpdateResult,
        context: Any?
    ) {
        val urlString = updateResult.updateInfo as? String ?: return
        val url = NSURL(string = urlString)

        if (UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
        }
    }

    private fun isUpdateAvailable(current: String, store: String): Boolean {
        return try {
            val currentParts = current.split(".").map { it.toIntOrNull() ?: 0 }
            val storeParts = store.split(".").map { it.toIntOrNull() ?: 0 }

            val length = maxOf(currentParts.size, storeParts.size)
            for (i in 0 until length) {
                val c = currentParts.getOrElse(i) { 0 }
                val s = storeParts.getOrElse(i) { 0 }

                if (s > c) return true
                if (s < c) return false
            }
            false
        } catch (_: Exception) {
            false
        }
    }
}