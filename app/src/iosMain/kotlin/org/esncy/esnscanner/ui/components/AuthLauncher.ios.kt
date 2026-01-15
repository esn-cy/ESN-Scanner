package org.esncy.esnscanner.ui.components

import platform.AuthenticationServices.ASPresentationAnchor
import platform.AuthenticationServices.ASWebAuthenticationPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASWebAuthenticationSession
import platform.Foundation.NSURL
import platform.Foundation.NSURLComponents
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.darwin.NSObject

actual class AuthLauncher actual constructor(
    activity: Any?,
    private val onCodeReceived: ((String) -> Unit)?,
    private val onError: ((String) -> Unit)?
) : AuthLauncherInterface {
    private val contextProvider = ContextProvider()

    private var webAuthSession: ASWebAuthenticationSession? = null

    actual override fun launchBrowser(url: String) {
        val nsUrl = NSURL.URLWithString(url) ?: return
        val callbackScheme = "org.esncy.esnscanner"

        val session = ASWebAuthenticationSession(
            uRL = nsUrl,
            callbackURLScheme = callbackScheme
        ) { callbackURL, error ->
            this.webAuthSession = null

            if (error != null) {
                onError?.invoke(error.localizedDescription)
                return@ASWebAuthenticationSession
            }

            callbackURL?.let { callbackURL ->
                val components = NSURLComponents.componentsWithURL(callbackURL, false)
                val code = components?.queryItems?.firstOrNull {
                    (it as platform.Foundation.NSURLQueryItem).name == "code"
                }?.let { (it as platform.Foundation.NSURLQueryItem).value }

                if (code != null) {
                    onCodeReceived?.invoke(code)
                } else {
                    onError?.invoke("No authorization code found in callback.")
                }
            }
        }

        session.presentationContextProvider = contextProvider
        session.prefersEphemeralWebBrowserSession = false

        this.webAuthSession = session

        session.start()
    }

    private class ContextProvider : NSObject(),
        ASWebAuthenticationPresentationContextProvidingProtocol {
        override fun presentationAnchorForWebAuthenticationSession(
            session: ASWebAuthenticationSession
        ): ASPresentationAnchor {
            return UIApplication.sharedApplication.keyWindow ?: UIWindow()
        }
    }
}
