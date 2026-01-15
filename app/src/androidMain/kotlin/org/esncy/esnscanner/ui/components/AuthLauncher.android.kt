package org.esncy.esnscanner.ui.components

import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

actual class AuthLauncher actual constructor(
    private val activity: Any?,
    onCodeReceived: ((String) -> Unit)?,
    onError: ((String) -> Unit)?
) : AuthLauncherInterface {
    actual override fun launchBrowser(url: String) {
        val context = (activity as? android.content.Context) ?: return
        val intent = CustomTabsIntent.Builder().build()
        intent.launchUrl(context, url.toUri())
    }
}