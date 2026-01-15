package org.esncy.esnscanner

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.esncy.esnscanner.models.SectionDataUIState
import org.esncy.esnscanner.models.SectionDataViewModel
import org.esncy.esnscanner.models.TokenViewModel
import org.esncy.esnscanner.models.UpdateViewModel
import org.esncy.esnscanner.ui.components.AuthLauncher

class MainActivity : ComponentActivity() {
    private val updateViewModel: UpdateViewModel by viewModels()
    private val sectionDataViewModel: SectionDataViewModel by viewModels()

    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var tokenViewModel: TokenViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        ContextProvider.setContext(getApplicationContext())

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        super.onCreate(savedInstanceState)

        tokenViewModel = TokenViewModel(sectionDataViewModel.dataFlow)

        appUpdateManager = AppUpdateManagerFactory.create(this)

        splashScreen.setKeepOnScreenCondition {
            sectionDataViewModel.state.value is SectionDataUIState.Loading
        }

        setContent {
            App(
                updateViewModel,
                sectionDataViewModel,
                tokenViewModel,
                AuthLauncher(activity = this)
            )
        }

        splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
            val splashScreenView = splashScreenViewProvider.view
            val iconView = splashScreenViewProvider.iconView

            val slideOut = ObjectAnimator.ofFloat(
                iconView,
                View.TRANSLATION_Y,
                0f,
                -splashScreenView.height.toFloat()
            ).apply {
                interpolator = AnticipateInterpolator()
                duration = 300L
                doOnEnd {
                    splashScreenViewProvider.remove()
                }
            }

            slideOut.start()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.data?.let { uri ->
            val code = uri.getQueryParameter("code")
            if (code != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    tokenViewModel.handleCallback(code)
                }
            }
        }
    }
}