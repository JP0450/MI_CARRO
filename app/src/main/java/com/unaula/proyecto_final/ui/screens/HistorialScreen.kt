package com.unaula.proyecto_final.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unaula.proyecto_final.model.MantenimientoRealizado
import com.unaula.proyecto_final.model.TipoMantenimiento
import com.unaula.proyecto_final.ui.viewmodel.MantenimientoViewModel
import com.unaula.proyecto_final.ui.viewmodel.VehiculoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    vehiculoViewModel: VehiculoViewModel,
    mantenimientoViewModel: MantenimientoViewModel
) {
    val vehiculos by vehiculoViewModel.vehiculos.collectAsState()
    val vehiculoSeleccionado by vehiculoViewModel.vehiculoSeleccionado.collectAsState()
    val realizados by mantenimientoViewModel.realizados.collectAsState()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var searchQuery by remember { mutableStateOf("") }
    var filtroTipo by remember { mutableStateOf<TipoMantenimiento?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val historialFiltrado = remember(realizados, vehiculoSeleccionado, searchQuery, filtroTipo) {
        realizados.filter { item ->
            val coincideVehiculo = vehiculoSeleccionado == null || item.vehiculoId == vehiculoSeleccionado?.id
            val coincideSearch = searchQuery.isBlank() ||
                    item.titulo.contains(searchQuery, ignoreCase = true) ||
                    item.taller.contains(searchQuery, ignoreCase = true) ||
                    item.observaciones.contains(searchQuery, ignoreCase = true)
            val coincideTipo = filtroTipo == null || item.tipo == filtroTipo

            coincideVehiculo && coincideSearch && coincideTipo
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Historial de Mantenimientos",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (isLandscape) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (vehiculos.isNotEmpty()) {
                        ExposedDropdownMenuBox(
                            expanded = dropdownExpanded,
                            onExpandedChange = { dropdownExpanded = !dropdownExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = vehiculoSeleccionado?.let { "${it.marca} ${it.modelo} (${it.placa})" } ?: "Todos los vehículos",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Filtrar Vehículo") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false }
                            ) {
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

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Buscar taller, servicio...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        } else {
            if (vehiculos.isNotEmpty()) {
                item {
                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = vehiculoSeleccionado?.let { "${it.marca} ${it.modelo} (${it.placa})" } ?: "Todos los vehículos",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Filtrar por Vehículo") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
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
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar por taller, servicio u observaciones...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = filtroTipo == null,
                    onClick = { filtroTipo = null },
                    label = { Text("Todos") }
                )
                FilterChip(
                    selected = filtroTipo == TipoMantenimiento.PREVENTIVO,
                    onClick = { filtroTipo = TipoMantenimiento.PREVENTIVO },
                    label = { Text("Preventivos") }
                )
                FilterChip(
                    selected = filtroTipo == TipoMantenimiento.CORRECTIVO,
                    onClick = { filtroTipo = TipoMantenimiento.CORRECTIVO },
                    label = { Text("Correctivos") }
                )
            }
        }

        if (historialFiltrado.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("No se encontraron registros de mantenimiento con los filtros seleccionados.")
                    }
                }
            }
        } else {
            items(historialFiltrado) { item ->
                ItemHistorialCard(item)
            }
        }
    }
}

@Composable
fun ItemHistorialCard(item: MantenimientoRealizado) {
    var expanded by remember { mutableStateOf(false) }
    val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(item.fecha))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.titulo,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${item.tipo.label} • Taller: ${item.taller.ifBlank { "N/A" }}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$${item.costoTotal}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "📅 $dateStr", fontSize = 12.sp)
                Text(text = "🚘 ${item.kilometrajeRealizado} km", fontSize = 12.sp)
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    if (item.observaciones.isNotBlank()) {
                        Text(
                            text = "Observaciones: ${item.observaciones}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (item.repuestos.isNotEmpty()) {
                        Text(
                            text = "Repuestos Instalados & Garantía:",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        item.repuestos.forEach { rep ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "• ${rep.nombre} (${rep.marca})", fontSize = 13.sp)
                                Text(text = "$${rep.costo} | ${rep.garantiaMeses}m garantía", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
}
