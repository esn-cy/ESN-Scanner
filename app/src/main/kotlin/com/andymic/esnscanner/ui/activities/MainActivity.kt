package com.andymic.esnscanner.ui.activities

import android.animation.ObjectAnimator
import android.graphics.Color.TRANSPARENT
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.andymic.esnscanner.models.AddViewModel
import com.andymic.esnscanner.models.DeliverViewModel
import com.andymic.esnscanner.models.OnlineViewModel
import com.andymic.esnscanner.models.ProduceViewModel
import com.andymic.esnscanner.models.ScanViewModel
import com.andymic.esnscanner.models.UpdateUIState
import com.andymic.esnscanner.models.UpdateViewModel
import com.andymic.esnscanner.ui.Destinations
import com.andymic.esnscanner.ui.components.NavigationRail
import com.andymic.esnscanner.ui.components.add.AddBottomBox
import com.andymic.esnscanner.ui.components.add.AddTopBox
import com.andymic.esnscanner.ui.components.deliver.DeliverBottomBox
import com.andymic.esnscanner.ui.components.deliver.DeliverTopBox
import com.andymic.esnscanner.ui.components.produce.ProduceBottomBox
import com.andymic.esnscanner.ui.components.produce.ProduceTopBox
import com.andymic.esnscanner.ui.components.scan.ScanBottomBox
import com.andymic.esnscanner.ui.components.scan.ScanTopBox
import com.andymic.esnscanner.ui.screens.CameraScreen
import com.andymic.esnscanner.ui.screens.HomeScreen
import com.andymic.esnscanner.ui.theme.ESNScannerAppTheme
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType

class MainActivity : ComponentActivity() {
    private val updateViewModel: UpdateViewModel by viewModels()
    private lateinit var appUpdateManager: AppUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(TRANSPARENT, TRANSPARENT)
        )

        super.onCreate(savedInstanceState)

        appUpdateManager = AppUpdateManagerFactory.create(this)

        splashScreen.setKeepOnScreenCondition { updateViewModel.state.value is UpdateUIState.Loading }

        setContent {
            val updateState by updateViewModel.state.collectAsState()

            LaunchedEffect(updateState) {
                val successState = updateState as? UpdateUIState.Success
                if (successState?.result?.isUpdateAvailable == true) {
                    val updateOptions = AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    appUpdateManager.startUpdateFlow(
                        successState.result.updateInfo,
                        this@MainActivity,
                        updateOptions
                    )
                }
            }

            ESNScannerAppTheme {
                AppContent(updateViewModel = updateViewModel)
            }
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
}

@Preview
@Composable
fun AppContent(
    modifier: Modifier = Modifier,
    updateViewModel: UpdateViewModel = viewModel()
) {
    val navController = rememberNavController()
    var selectedDestination by remember { mutableStateOf(Destinations.Home.spec) }

    val onlineViewModel: OnlineViewModel = viewModel()

    LaunchedEffect(Unit) {
        onlineViewModel.runTest()
    }

    Row(modifier = modifier.fillMaxSize()) {
        NavigationRail(
            selectedDestination = selectedDestination,
            onDestinationSelected = { destination ->
                selectedDestination = destination
                navController.navigate(destination.route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            viewModel = onlineViewModel
        )

        Surface(
            modifier = Modifier
                .weight(1f)
                .windowInsetsPadding(WindowInsets.safeDrawing),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                bottomStart = 16.dp
            ),
            color = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            ESNcardNavHost(
                navController = navController,
                startDestination = Destinations.Home.spec.route,
                onlineViewModel = onlineViewModel,
                updateViewModel = updateViewModel,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun ESNcardNavHost(
    navController: NavHostController,
    startDestination: String,
    onlineViewModel: OnlineViewModel,
    updateViewModel: UpdateViewModel,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = Destinations.Home.spec.route) {
            HomeScreen(
                onlineViewModel = onlineViewModel,
                updateViewModel = updateViewModel
            )
        }
        composable(route = Destinations.Scan.spec.route) {
            CameraScreen(
                viewModel<ScanViewModel>(),
                TopBox = { uiState, modifier -> ScanTopBox(uiState, modifier) },
                BottomBox = { uiState, modifier -> ScanBottomBox(uiState, modifier) }
            )
        }
        composable(route = Destinations.Add.spec.route) {
            CameraScreen(
                viewModel<AddViewModel>(),
                TopBox = { uiState, modifier -> AddTopBox(uiState, modifier) },
                BottomBox = { uiState, modifier -> AddBottomBox(uiState, modifier) }
            )
        }
        composable(route = Destinations.Produce.spec.route) {
            CameraScreen(
                viewModel<ProduceViewModel>(),
                TopBox = { uiState, modifier -> ProduceTopBox(uiState, modifier) },
                BottomBox = { uiState, modifier -> ProduceBottomBox(uiState, modifier) }
            )
        }
        composable(route = Destinations.Deliver.spec.route) {
            CameraScreen(
                viewModel<DeliverViewModel>(),
                TopBox = { uiState, modifier -> DeliverTopBox(uiState, modifier) },
                BottomBox = { uiState, modifier -> DeliverBottomBox(uiState, modifier) }
            )
        }
    }
}