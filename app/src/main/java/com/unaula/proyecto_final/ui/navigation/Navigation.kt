package com.unaula.proyecto_final.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.unaula.proyecto_final.ui.screens.DashboardScreen
import com.unaula.proyecto_final.ui.screens.HistorialScreen
import com.unaula.proyecto_final.ui.screens.ProgramarMantenimientoScreen
import com.unaula.proyecto_final.ui.screens.RegistrarMantenimientoScreen
import com.unaula.proyecto_final.ui.screens.ReportesScreen
import com.unaula.proyecto_final.ui.screens.VehiculosScreen
import com.unaula.proyecto_final.ui.screens.ConfiguracionScreen
import com.unaula.proyecto_final.ui.components.AppBrandBar
import com.unaula.proyecto_final.ui.viewmodel.MantenimientoViewModel
import com.unaula.proyecto_final.ui.viewmodel.ReportesViewModel
import com.unaula.proyecto_final.ui.viewmodel.VehiculoViewModel
import com.unaula.proyecto_final.ui.viewmodel.ConfiguracionViewModel
import com.unaula.proyecto_final.ui.theme.ColorDeepBlue
import com.unaula.proyecto_final.ui.theme.ColorTextSecondary

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Dashboard : Screen("dashboard", "Inicio", Icons.Outlined.Dashboard)
    object Vehiculos : Screen("vehiculos", "Vehículos", Icons.Outlined.DirectionsCar)
    object ProgramarMantenimiento : Screen("programar_mantenimiento", "Programar")
    object RegistrarMantenimiento : Screen("registrar_mantenimiento", "Registrar")
    object Historial : Screen("historial", "Historial", Icons.AutoMirrored.Filled.Assignment)
    object Reportes : Screen("reportes", "Reportes", Icons.Outlined.Analytics)
    object Configuracion : Screen("configuracion", "Configuración", Icons.Outlined.Settings)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppNavigation(
    vehiculoViewModel: VehiculoViewModel = viewModel(),
    mantenimientoViewModel: MantenimientoViewModel = viewModel(),
    reportesViewModel: ReportesViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isImeVisible = WindowInsets.isImeVisible

    val bottomNavItems = listOf(
        Screen.Dashboard,
        Screen.Vehiculos,
        Screen.Historial,
        Screen.Reportes
    )
    val isFormRoute = currentRoute == Screen.ProgramarMantenimiento.route ||
        currentRoute == Screen.RegistrarMantenimiento.route ||
        currentRoute == Screen.Configuracion.route

    Scaffold(
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            AppBrandBar(
                onBack = if (isFormRoute) {
                    { navController.popBackStack() }
                } else {
                    null
                },
                onSettingsClick = if (currentRoute != Screen.Configuracion.route && !isFormRoute) {
                    { navController.navigate(Screen.Configuracion.route) }
                } else {
                    null
                }
            )
        },
        bottomBar = {
            if (!isImeVisible && currentRoute in bottomNavItems.map { it.route }) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = ColorTextSecondary,
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                screen.icon?.let {
                                    Icon(imageVector = it, contentDescription = screen.title)
                                }
                            },
                            label = { Text(screen.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ColorDeepBlue,
                                selectedTextColor = ColorDeepBlue,
                                indicatorColor = androidx.compose.ui.graphics.Color(0xFFEAF3FF),
                                unselectedIconColor = ColorTextSecondary,
                                unselectedTextColor = ColorTextSecondary
                            ),
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(300)) + slideInVertically { it / 10 } },
            exitTransition = { fadeOut(animationSpec = tween(200)) },
            popEnterTransition = { fadeIn(animationSpec = tween(300)) },
            popExitTransition = { fadeOut(animationSpec = tween(200)) + slideOutVertically { it / 10 } }
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    vehiculoViewModel = vehiculoViewModel,
                    mantenimientoViewModel = mantenimientoViewModel,
                    onNavigateToProgramar = { navController.navigate(Screen.ProgramarMantenimiento.route) },
                    onNavigateToRegistrar = { navController.navigate(Screen.RegistrarMantenimiento.route) },
                    onNavigateToVehiculos = { navController.navigate(Screen.Vehiculos.route) }
                )
            }
            composable(Screen.Vehiculos.route) {
                VehiculosScreen(
                    vehiculoViewModel = vehiculoViewModel
                )
            }
            composable(Screen.ProgramarMantenimiento.route) {
                ProgramarMantenimientoScreen(
                    vehiculoViewModel = vehiculoViewModel,
                    mantenimientoViewModel = mantenimientoViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.RegistrarMantenimiento.route) {
                RegistrarMantenimientoScreen(
                    vehiculoViewModel = vehiculoViewModel,
                    mantenimientoViewModel = mantenimientoViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Historial.route) {
                HistorialScreen(
                    vehiculoViewModel = vehiculoViewModel,
                    mantenimientoViewModel = mantenimientoViewModel
                )
            }
            composable(Screen.Reportes.route) {
                ReportesScreen(
                    vehiculoViewModel = vehiculoViewModel,
                    mantenimientoViewModel = mantenimientoViewModel,
                    reportesViewModel = reportesViewModel
                )
            }
            composable(Screen.Configuracion.route) {
                ConfiguracionScreen(
                    configuracionViewModel = viewModel()
                )
            }
        }
    }
}
