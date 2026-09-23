package com.unaula.proyecto_final.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unaula.proyecto_final.ui.viewmodel.ReportesViewModel
import com.unaula.proyecto_final.ui.viewmodel.VehiculoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesScreen(
    vehiculoViewModel: VehiculoViewModel,
    reportesViewModel: ReportesViewModel
) {
    val vehiculos by vehiculoViewModel.vehiculos.collectAsState()
    val vehiculoSeleccionado by vehiculoViewModel.vehiculoSeleccionado.collectAsState()

    var dropdownExpanded by remember { mutableStateOf(false) }

    val resumen = remember(vehiculoSeleccionado) {
        reportesViewModel.obtenerResumenVehiculo(vehiculoSeleccionado?.id)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Resumen de Gastos y Estado",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (vehiculos.isNotEmpty()) {
                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = vehiculoSeleccionado?.let { "${it.marca} ${it.modelo} (${it.placa})" } ?: "Todos los vehículos",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Filtrar vehículo para reporte") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todos los vehículos") },
                            onClick = {
                                vehiculoViewModel.seleccionarVehiculo(vehiculos.first())
                                dropdownExpanded = false
                            }
                        )
                        vehiculos.forEach { vehiculo ->
                            DropdownMenuItem(
                                text = { Text("${vehiculo.marca} ${vehiculo.modelo} (${vehiculo.placa})") },
                                onClick = {
                                    vehiculoViewModel.seleccionarVehiculo(vehiculo)
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        item {
            // Tarjeta Gasto Total
            CardStat(
                titulo = "Gasto Total Acumulado",
                valor = "$${resumen.costoTotal}",
                icon = Icons.Default.AttachMoney,
                color = MaterialTheme.colorScheme.primaryContainer
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    CardStat(
                        titulo = "Mantenimiento Preventivo",
                        valor = "$${resumen.costoPreventivos}",
                        icon = Icons.Default.Analytics,
                        color = Color(0xFFE8F5E9)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    CardStat(
                        titulo = "Mantenimiento Correctivo",
                        valor = "$${resumen.costoCorrectivos}",
                        icon = Icons.Default.Build,
                        color = Color(0xFFFFEBEE)
                    )
                }
            }
        }

        item {
            Text(
                text = "Estado del Plan de Mantenimiento",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    CardStat(
                        titulo = "Al día",
                        valor = "${resumen.mantenimientosAlDia}",
                        icon = Icons.Default.CheckCircle,
                        color = Color(0xFFE8F5E9)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    CardStat(
                        titulo = "Próximos / Vencidos",
                        valor = "${resumen.mantenimientosProximos + resumen.mantenimientosVencidos}",
                        icon = Icons.Default.Warning,
                        color = Color(0xFFFFF3E0)
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Métricas Generales",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "• Mantenimientos realizados: ${resumen.totalMantenimientos}")
                    Text(text = "• Repuestos e insumos instalados: ${resumen.totalRepuestos}")
                }
            }
        }
    }
}

@Composable
fun CardStat(
    titulo: String,
    valor: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null)
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text(text = titulo, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = valor,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
