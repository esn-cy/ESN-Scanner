package org.esncy.esnscanner

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.esncy.esnscanner.data.KtorClients
import org.esncy.esnscanner.models.AddViewModel
import org.esncy.esnscanner.models.OnlineViewModel
import org.esncy.esnscanner.models.ScanViewModel
import org.esncy.esnscanner.models.SectionDataViewModel
import org.esncy.esnscanner.models.StatusViewModel
import org.esncy.esnscanner.models.Statuses
import org.esncy.esnscanner.models.TokenViewModel
import org.esncy.esnscanner.models.UpdateViewModel
import org.esncy.esnscanner.models.ViewModels
import org.esncy.esnscanner.ui.Destinations
import org.esncy.esnscanner.ui.components.AuthLauncher
import org.esncy.esnscanner.ui.components.NavigationRail
import org.esncy.esnscanner.ui.components.add.AddBottomBox
import org.esncy.esnscanner.ui.components.add.AddTopBox
import org.esncy.esnscanner.ui.components.scan.ScanBottomBox
import org.esncy.esnscanner.ui.components.scan.ScanTopBox
import org.esncy.esnscanner.ui.components.status.StatusBottomBox
import org.esncy.esnscanner.ui.components.status.StatusTopBox
import org.esncy.esnscanner.ui.screens.CameraScreen
import org.esncy.esnscanner.ui.screens.HomeScreen
import org.esncy.esnscanner.ui.screens.RegisterScreen
import org.esncy.esnscanner.ui.screens.SettingsScreen
import org.esncy.esnscanner.ui.theme.ESNScannerAppTheme

@Composable
expect fun getPlatformContext(): Any?

@Composable
fun App(
    updateViewModel: UpdateViewModel,
    sectionDataViewModel: SectionDataViewModel,
    tokenViewModel: TokenViewModel,
    authLauncher: AuthLauncher? = null
) {
    val context = getPlatformContext()

    LaunchedEffect(Unit) {
        updateViewModel.checkForUpdate(context)
    }

    val clients = KtorClients(tokenViewModel)

    val viewModels = remember {
        ViewModels(
            AddViewModel(sectionDataViewModel.dataFlow, clients),
            StatusViewModel(sectionDataViewModel.dataFlow, Statuses.Blacklisted, clients),
            StatusViewModel(sectionDataViewModel.dataFlow, Statuses.Delivered, clients),
            OnlineViewModel(sectionDataViewModel.dataFlow, clients),
            StatusViewModel(sectionDataViewModel.dataFlow, Statuses.Paid, clients),
            StatusViewModel(sectionDataViewModel.dataFlow, Statuses.Issued, clients),
            ScanViewModel(sectionDataViewModel.dataFlow, clients),
            sectionDataViewModel,
            tokenViewModel,
            updateViewModel
        )
    }

    val initAuthLauncher = authLauncher ?: AuthLauncher(
        onCodeReceived = { code ->
            viewModels.tokenViewModel.handleCallback(code)
        },
        onError = {}
    )

    ESNScannerAppTheme {
        AppContent(viewModels = viewModels, authLauncher = initAuthLauncher)
    }
}

@Composable
fun AppContent(
    modifier: Modifier = Modifier,
    viewModels: ViewModels,
    authLauncher: AuthLauncher
) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selectedDestination = Destinations.entries.find {
        it.spec.route == currentRoute
    }?.spec ?: Destinations.Home.spec

    Row(modifier = modifier.fillMaxSize()) {
        NavigationRail(
            modifier = Modifier.fillMaxHeight(),
            selectedDestination = selectedDestination,
            onDestinationSelected = { destination ->
                navController.navigate(destination.route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            onlineViewModel = viewModels.onlineViewModel,
            tokenViewModel = viewModels.tokenViewModel
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
                viewModels = viewModels,
                authLauncher = authLauncher,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun ESNcardNavHost(
    navController: NavHostController,
    startDestination: String,
    viewModels: ViewModels,
    authLauncher: AuthLauncher,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = Destinations.Home.spec.route) {
            HomeScreen(
                onlineViewModel = viewModels.onlineViewModel,
                updateViewModel = viewModels.updateViewModel,
            )
        }
        composable(route = Destinations.Scan.spec.route) {
            CameraScreen(
                viewModels.scanViewModel,
                TopBox = { uiState, modifier ->
                    ScanTopBox(
                        uiState,
                        modifier,
                        KtorClients(viewModels.tokenViewModel)
                    )
                },
                BottomBox = { uiState, modifier -> ScanBottomBox(uiState, modifier) }
            )
        }
        composable(route = Destinations.Add.spec.route) {
            CameraScreen(
                viewModels.addViewModel,
                TopBox = { uiState, modifier -> AddTopBox(uiState, modifier) },
                BottomBox = { uiState, modifier -> AddBottomBox(uiState, modifier) }
            )
        }
        composable(route = Destinations.MarkAsPaid.spec.route) {
            CameraScreen(
                viewModels.paidViewModel,
                TopBox = { uiState, modifier -> StatusTopBox(uiState, modifier) },
                BottomBox = { uiState, modifier -> StatusBottomBox(uiState, modifier) }
            )
        }
        composable(route = Destinations.Issue.spec.route) {
            CameraScreen(
                viewModels.issueViewModel,
                TopBox = { uiState, modifier -> StatusTopBox(uiState, modifier) },
                BottomBox = { uiState, modifier -> StatusBottomBox(uiState, modifier) }
            )
        }
        composable(route = Destinations.Deliver.spec.route) {
            CameraScreen(
                viewModels.deliverViewModel,
                TopBox = { uiState, modifier -> StatusTopBox(uiState, modifier) },
                BottomBox = { uiState, modifier -> StatusBottomBox(uiState, modifier) }
            )
        }
        composable(route = Destinations.Blacklist.spec.route) {
            CameraScreen(
                viewModels.blacklistViewModel,
                TopBox = { uiState, modifier -> StatusTopBox(uiState, modifier) },
                BottomBox = { uiState, modifier -> StatusBottomBox(uiState, modifier) }
            )
        }
        composable(route = Destinations.Register.spec.route) {
            RegisterScreen(
                viewModels.sectionDataViewModel,
            )
        }
        composable(route = Destinations.Settings.spec.route) {
            SettingsScreen(
                viewModels.sectionDataViewModel,
                viewModels.tokenViewModel,
                authLauncher
            )
        }
    }
}