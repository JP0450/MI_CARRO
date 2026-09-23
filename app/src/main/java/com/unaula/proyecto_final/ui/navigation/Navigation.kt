package com.unaula.proyecto_final.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.unaula.proyecto_final.ui.viewmodel.MantenimientoViewModel
import com.unaula.proyecto_final.ui.viewmodel.ReportesViewModel
import com.unaula.proyecto_final.ui.viewmodel.VehiculoViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Dashboard : Screen("dashboard", "Inicio", Icons.Default.Home)
    object Vehiculos : Screen("vehiculos", "Vehículos", Icons.Default.DirectionsCar)
    object ProgramarMantenimiento : Screen("programar_mantenimiento", "Programar")
    object RegistrarMantenimiento : Screen("registrar_mantenimiento", "Registrar")
    object Historial : Screen("historial", "Historial", Icons.Default.Build)
    object Reportes : Screen("reportes", "Reportes", Icons.Default.Analytics)
}

@Composable
fun AppNavigation(
    vehiculoViewModel: VehiculoViewModel = viewModel(),
    mantenimientoViewModel: MantenimientoViewModel = viewModel(),
    reportesViewModel: ReportesViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        Screen.Dashboard,
        Screen.Vehiculos,
        Screen.Historial,
        Screen.Reportes
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomNavItems.map { it.route }) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                screen.icon?.let {
                                    Icon(imageVector = it, contentDescription = screen.title)
                                }
                            },
                            label = { Text(screen.title) },
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
            modifier = Modifier.padding(innerPadding)
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
                    reportesViewModel = reportesViewModel
                )
            }
        }
    }
}
