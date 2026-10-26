package com.unaula.proyecto_final.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.unaula.proyecto_final.ui.theme.ColorDeepGreen
import com.unaula.proyecto_final.ui.theme.ColorMintAqua
import com.unaula.proyecto_final.ui.theme.ColorTextSecondary
import com.unaula.proyecto_final.ui.viewmodel.MantenimientoViewModel
import com.unaula.proyecto_final.ui.viewmodel.VehiculoViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    vehiculoViewModel: VehiculoViewModel,
    mantenimientoViewModel: MantenimientoViewModel
) {
    val vehiculos by vehiculoViewModel.vehiculos.collectAsState()
    val realizados by mantenimientoViewModel.realizados.collectAsState()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var searchQuery by remember { mutableStateOf("") }
    var filtroTipo by remember { mutableStateOf<TipoMantenimiento?>(null) }
    var filtroVehiculoId by remember { mutableStateOf<String?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val historialFiltrado = remember(realizados, filtroVehiculoId, searchQuery, filtroTipo) {
        realizados.filter { item ->
            val coincideVehiculo = filtroVehiculoId == null || item.vehiculoId == filtroVehiculoId
            val coincideSearch = searchQuery.isBlank() ||
                    item.titulo.contains(searchQuery, ignoreCase = true) ||
                    item.taller.contains(searchQuery, ignoreCase = true) ||
                    item.observaciones.contains(searchQuery, ignoreCase = true)
            val coincideTipo = filtroTipo == null || item.tipo == filtroTipo

            coincideVehiculo && coincideSearch && coincideTipo
        }
    }

    val vehiculoFiltroActual = vehiculos.find { it.id == filtroVehiculoId }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.White)
            .imePadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Historial y auditoría",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = ColorDeepGreen
            )
            Text(
                text = "Consulta los servicios y el seguimiento de tu flota",
                style = MaterialTheme.typography.bodyMedium,
                color = ColorTextSecondary
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
                                value = vehiculoFiltroActual?.let { "${it.marca} ${it.modelo} (${it.placa})" } ?: "Todos los vehículos",
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
                                DropdownMenuItem(
                                    text = { Text("Todos los vehículos") },
                                    onClick = {
                                        filtroVehiculoId = null
                                        dropdownExpanded = false
                                    }
                                )
                                vehiculos.forEach { vehiculo ->
                                    DropdownMenuItem(
                                        text = { Text("${vehiculo.marca} ${vehiculo.modelo} (${vehiculo.placa})") },
                                        onClick = {
                                            filtroVehiculoId = vehiculo.id
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
                            value = vehiculoFiltroActual?.let { "${it.marca} ${it.modelo} (${it.placa})" } ?: "Todos los vehículos",
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
                            DropdownMenuItem(
                                text = { Text("Todos los vehículos") },
                                onClick = {
                                    filtroVehiculoId = null
                                    dropdownExpanded = false
                                }
                            )
                            vehiculos.forEach { vehiculo ->
                                DropdownMenuItem(
                                    text = { Text("${vehiculo.marca} ${vehiculo.modelo} (${vehiculo.placa})") },
                                    onClick = {
                                        filtroVehiculoId = vehiculo.id
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Registros", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("${historialFiltrado.size} encontrados", style = MaterialTheme.typography.labelMedium, color = ColorTextSecondary)
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
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("es-CO")) }
    val costoFormatted = "$ ${numberFormat.format(item.costoTotal.roundToInt())}"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                        style = MaterialTheme.typography.bodySmall,
                        color = ColorTextSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = costoFormatted,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Contraer" else "Expandir"
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(text = "📅 $dateStr", fontSize = 12.sp, color = ColorTextSecondary)
                Text(text = "🚘 ${numberFormat.format(item.kilometrajeRealizado)} km", fontSize = 12.sp, color = ColorTextSecondary)
            }

            // Badges indicativos de repuestos u observaciones
            if (item.repuestos.isNotEmpty() || item.observaciones.isNotBlank()) {
                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (item.repuestos.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ColorMintAqua)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "🛠️ ${item.repuestos.size} repuesto${if (item.repuestos.size > 1) "s" else ""}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ColorDeepGreen
                            )
                        }
                    }
                    if (item.observaciones.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "💬 Con notas",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = ColorTextSecondary
                            )
                        }
                    }
                }
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
                            style = MaterialTheme.typography.bodySmall,
                            color = ColorDeepGreen
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        item.repuestos.forEach { rep ->
                            val repCostoFormatted = "$ ${numberFormat.format(rep.costo.roundToInt())}"
                            val repGarantiaKmFormatted = if (rep.garantiaKm > 0) " / ${numberFormat.format(rep.garantiaKm)} km" else ""

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "• ${rep.nombre} ${if (rep.marca.isNotBlank()) "(${rep.marca})" else ""}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Garantía: ${rep.garantiaMeses} meses$repGarantiaKmFormatted",
                                        fontSize = 11.sp,
                                        color = ColorTextSecondary
                                    )
                                }
                                Text(
                                    text = repCostoFormatted,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ColorDeepGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }
        }
    }
}
