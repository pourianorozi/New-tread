package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.ChartScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EducationScreen
import com.example.ui.screens.PineScriptGuideScreen
import com.example.ui.screens.SignalHistoryScreen
import com.example.ui.screens.WatchlistScreen
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.OnDarkTextMuted
import com.example.ui.theme.OnDarkTextPrimary
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SmartTradeTheme
import com.example.ui.viewmodel.TradingViewModel

sealed class NavRoute(val route: String, val titleFa: String, val icon: ImageVector) {
    object Dashboard : NavRoute("dashboard", "داشبورد", Icons.Default.Analytics)
    object Chart : NavRoute("chart", "نمودار", Icons.Default.ShowChart)
    object Watchlist : NavRoute("watchlist", "دیده‌بان", Icons.Default.Bookmark)
    object PineScript : NavRoute("pinescript", "تنظیم هشدار", Icons.Default.Code)
    object History : NavRoute("history", "سوابق", Icons.Default.History)
    object Academy : NavRoute("academy", "آموزش", Icons.Default.School)
}

class MainActivity : ComponentActivity() {

    private val viewModel: TradingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SmartTradeTheme {
                SmartTradeApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SmartTradeApp(viewModel: TradingViewModel) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val navItems = listOf(
        NavRoute.Dashboard,
        NavRoute.Chart,
        NavRoute.Watchlist,
        NavRoute.PineScript,
        NavRoute.History,
        NavRoute.Academy
    )

    // Listen to notification messages from ViewModel
    LaunchedEffect(Unit) {
        viewModel.notificationMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                contentColor = OnDarkTextPrimary
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                navItems.forEach { item ->
                    val isSelected = currentRoute == item.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.titleFa,
                                tint = if (isSelected) PrimaryBlue else OnDarkTextMuted
                            )
                        },
                        label = {
                            Text(
                                text = item.titleFa,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) PrimaryBlue else OnDarkTextMuted
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = PrimaryBlue.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoute.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoute.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToChart = {
                        navController.navigate(NavRoute.Chart.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(NavRoute.Chart.route) {
                ChartScreen(viewModel = viewModel)
            }

            composable(NavRoute.Watchlist.route) {
                WatchlistScreen(
                    viewModel = viewModel,
                    onSelectAsset = {
                        navController.navigate(NavRoute.Dashboard.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(NavRoute.PineScript.route) {
                PineScriptGuideScreen(viewModel = viewModel)
            }

            composable(NavRoute.History.route) {
                SignalHistoryScreen(viewModel = viewModel)
            }

            composable(NavRoute.Academy.route) {
                EducationScreen()
            }
        }
    }
}
