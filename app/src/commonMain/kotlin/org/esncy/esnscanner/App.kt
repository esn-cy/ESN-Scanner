package org.esncy.esnscanner

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.esncy.esnscanner.models.AddViewModel
import org.esncy.esnscanner.models.DeliverViewModel
import org.esncy.esnscanner.models.OnlineViewModel
import org.esncy.esnscanner.models.ProduceViewModel
import org.esncy.esnscanner.models.ScanViewModel
import org.esncy.esnscanner.models.SectionDataViewModel
import org.esncy.esnscanner.models.UpdateViewModel
import org.esncy.esnscanner.models.ViewModels
import org.esncy.esnscanner.ui.Destinations
import org.esncy.esnscanner.ui.components.NavigationRail
import org.esncy.esnscanner.ui.components.add.AddBottomBox
import org.esncy.esnscanner.ui.components.add.AddTopBox
import org.esncy.esnscanner.ui.components.deliver.DeliverBottomBox
import org.esncy.esnscanner.ui.components.deliver.DeliverTopBox
import org.esncy.esnscanner.ui.components.produce.ProduceBottomBox
import org.esncy.esnscanner.ui.components.produce.ProduceTopBox
import org.esncy.esnscanner.ui.components.scan.ScanBottomBox
import org.esncy.esnscanner.ui.components.scan.ScanTopBox
import org.esncy.esnscanner.ui.screens.CameraScreen
import org.esncy.esnscanner.ui.screens.HomeScreen
import org.esncy.esnscanner.ui.theme.ESNScannerAppTheme

@Composable
expect fun getPlatformContext(): Any?

@Composable
fun App(
    updateViewModel: UpdateViewModel,
    sectionDataViewModel: SectionDataViewModel
) {
    val context = getPlatformContext()

    LaunchedEffect(Unit) {
        updateViewModel.checkForUpdate(context)
    }

    val viewModels = remember {
        ViewModels(
            AddViewModel(sectionDataViewModel.dataFlow),
            DeliverViewModel(sectionDataViewModel.dataFlow),
            OnlineViewModel(sectionDataViewModel.dataFlow),
            ProduceViewModel(sectionDataViewModel.dataFlow),
            ScanViewModel(sectionDataViewModel.dataFlow),
            sectionDataViewModel,
            updateViewModel
        )
    }

    ESNScannerAppTheme {
        AppContent(viewModels = viewModels)
    }
}

@Composable
fun AppContent(
    modifier: Modifier = Modifier,
    viewModels: ViewModels
) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selectedDestination = Destinations.entries.find {
        it.spec.route == currentRoute
    }?.spec ?: Destinations.Home.spec

    Row(modifier = modifier.fillMaxSize()) {
        NavigationRail(
            selectedDestination = selectedDestination,
            onDestinationSelected = { destination ->
                navController.navigate(destination.route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            viewModel = viewModels.onlineViewModel
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
                sectionDataViewModel = viewModels.sectionDataViewModel
            )
        }
        composable(route = Destinations.Scan.spec.route) {
            CameraScreen(
                viewModels.scanViewModel,
                TopBox = { uiState, modifier -> ScanTopBox(uiState, modifier) },
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
        composable(route = Destinations.Produce.spec.route) {
            CameraScreen(
                viewModels.produceViewModel,
                TopBox = { uiState, modifier -> ProduceTopBox(uiState, modifier) },
                BottomBox = { uiState, modifier -> ProduceBottomBox(uiState, modifier) }
            )
        }
        composable(route = Destinations.Deliver.spec.route) {
            CameraScreen(
                viewModels.deliverViewModel,
                TopBox = { uiState, modifier -> DeliverTopBox(uiState, modifier) },
                BottomBox = { uiState, modifier -> DeliverBottomBox(uiState, modifier) }
            )
        }
    }
}