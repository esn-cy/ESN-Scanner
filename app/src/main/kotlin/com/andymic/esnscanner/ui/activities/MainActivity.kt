package com.andymic.esnscanner.ui.activities

import android.graphics.Color.TRANSPARENT
import android.graphics.Color.argb
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.andymic.esnscanner.models.AddViewModel
import com.andymic.esnscanner.models.ConnectionViewModel
import com.andymic.esnscanner.models.DeliverViewModel
import com.andymic.esnscanner.models.ProduceViewModel
import com.andymic.esnscanner.models.ScanViewModel
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
import com.google.android.play.core.install.model.UpdateAvailability

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val darkTheme = isSystemInDarkTheme()
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        TRANSPARENT,
                        TRANSPARENT,
                    ) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        argb(0xe6, 0xFF, 0xFF, 0xFF),
                        argb(0x80, 0x1b, 0x1b, 0x1b),
                    ) { darkTheme },
                )
                onDispose {}
            }

            ESNScannerAppTheme {
                AppContent()
            }
        }
    }
}

@Preview
@Composable
fun AppContent(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    var selectedDestination by remember { mutableStateOf(Destinations.Home.spec) }

    val connectionViewModel: ConnectionViewModel = viewModel()

    LaunchedEffect(Unit) {
        connectionViewModel.runTest()
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
            viewModel = connectionViewModel
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
                connectionViewModel = connectionViewModel,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun ESNcardNavHost(
    navController: NavHostController,
    startDestination: String,
    connectionViewModel: ConnectionViewModel,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = Destinations.Home.spec.route) {
            HomeScreen(connectionViewModel)
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