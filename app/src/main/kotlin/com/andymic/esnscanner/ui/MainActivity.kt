package com.andymic.esnscanner.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color.TRANSPARENT
import android.graphics.Color.argb
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.andymic.esnscanner.CameraScanner
import com.andymic.esnscanner.ScanViewModel
import com.andymic.esnscanner.ui.components.BottomStatusBox
import com.andymic.esnscanner.ui.components.NavigationRail
import com.andymic.esnscanner.ui.components.TopInfoBox
import com.andymic.esnscanner.ui.theme.ESNCyan
import com.andymic.esnscanner.ui.theme.ESNGreen
import com.andymic.esnscanner.ui.theme.ESNMagenta
import com.andymic.esnscanner.ui.theme.ESNOrange
import com.andymic.esnscanner.ui.theme.ESNScannerAppTheme

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

    Row(modifier = modifier.fillMaxSize()) {
        NavigationRail(
            Modifier,
            selectedDestination
        ) { destination ->
            selectedDestination = destination
            navController.navigate(destination.route) {
                popUpTo(navController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }

        Surface(
            modifier = Modifier
                .weight(1f)
                .windowInsetsPadding(WindowInsets.safeDrawing),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                bottomStart = 16.dp
            ),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            ESNcardNavHost(navController, Destinations.Home.spec.route, Modifier.fillMaxSize())
        }
    }
}

@Composable
fun ESNcardNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = Destinations.Home.spec.route) {
            HomeScreen()
        }
        composable(route = Destinations.Scan.spec.route) {
            ScanScreen()
        }
        composable(route = Destinations.Add.spec.route) {
            AddScreen()
        }
        composable(route = Destinations.Produce.spec.route) {
            ProduceScreen()
        }
        composable(route = Destinations.Deliver.spec.route) {
            DeliverScreen()
        }
    }
}


@Composable
fun HomeScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Home Screen")
    }
}

@SuppressLint("SourceLockedOrientationActivity")
@Composable
fun ScanScreen(viewModel: ScanViewModel = viewModel()) {
    val uiState by viewModel.scanState.collectAsState()
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )
    LaunchedEffect(key1 = true) {
        val activity = context as? Activity
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (!hasCameraPermission) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Camera permission is required.")
            }
            return
        }
        Column(modifier = Modifier.fillMaxSize()) {
            TopInfoBox(
                uiState = uiState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(4.5f)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(16.dp)
            )

            Box(
                modifier = Modifier
                    .weight(4.5f)
                    .aspectRatio(1f)
            ) {
                CameraScanner({ barcodes ->
                    for (barcode in barcodes) {
                        val value = barcode.rawValue
                        if (value == null)
                            continue
                        viewModel.scanRequest(value)
                    }
                }, LocalContext.current)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .border(
                            width = 4.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    ESNCyan,
                                    ESNMagenta,
                                    ESNGreen,
                                    ESNOrange
                                ),
                                start = Offset.Zero,
                                end = Offset.Infinite
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                )
            }

            BottomStatusBox(
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
                uiState)
        }
    }
}

@Composable
fun AddScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Add Screen")
    }
}

@Composable
fun ProduceScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Produce Screen")
    }
}

@Composable
fun DeliverScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Deliver Screen")
    }
}