package org.esncy.esnscanner.ui.components

interface AuthLauncherInterface {
    fun launchBrowser(url: String)
}

expect class AuthLauncher(
    activity: Any? = null,
    onCodeReceived: ((String) -> Unit)? = null,
    onError: ((String) -> Unit)? = null,
) : AuthLauncherInterface {
    override fun launchBrowser(url: String)
}