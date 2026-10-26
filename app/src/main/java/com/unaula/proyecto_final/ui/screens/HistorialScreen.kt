package com.unaula.proyecto_final.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unaula.proyecto_final.model.MantenimientoRealizado
import com.unaula.proyecto_final.model.TipoMantenimiento
import com.unaula.proyecto_final.ui.theme.ColorDeepGreen
import com.unaula.proyecto_final.ui.theme.ColorTextSecondary
import com.unaula.proyecto_final.ui.viewmodel.MantenimientoViewModel
import com.unaula.proyecto_final.ui.viewmodel.VehiculoViewModel
import java.text.NumberFormat
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
    val realizados by mantenimientoViewModel.realizados.collectAsState()
    val categorias by mantenimientoViewModel.categorias.collectAsState()
    val context = LocalContext.current

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var searchQuery by remember { mutableStateOf("") }
    var filtroTipo by remember { mutableStateOf<TipoMantenimiento?>(null) }
    var filtroVehiculoId by remember { mutableStateOf<String?>(null) }
    var filtroCategoria by remember { mutableStateOf<String?>(null) }
    var filtroTaller by remember { mutableStateOf("") }
    var filtroCostoMin by remember { mutableStateOf("") }
    var filtroCostoMax by remember { mutableStateOf("") }

    var dropdownExpanded by remember { mutableStateOf(false) }
    var categoriaDropdownExpanded by remember { mutableStateOf(false) }
    var showAdvancedFilters by remember { mutableStateOf(false) }

    val historialFiltrado = remember(
        realizados, filtroVehiculoId, searchQuery, filtroTipo,
        filtroCategoria, filtroTaller, filtroCostoMin, filtroCostoMax
    ) {
        realizados.filter { item ->
            val coincideVehiculo = filtroVehiculoId == null || item.vehiculoId == filtroVehiculoId
            val coincideSearch = searchQuery.isBlank() ||
                    item.titulo.contains(searchQuery, ignoreCase = true) ||
                    item.taller.contains(searchQuery, ignoreCase = true) ||
                    item.observaciones.contains(searchQuery, ignoreCase = true)
            val coincideTipo = filtroTipo == null || item.tipo == filtroTipo
            val coincideCat = filtroCategoria == null || item.categoria == filtroCategoria
            val coincideTaller = filtroTaller.isBlank() || item.taller.contains(filtroTaller, ignoreCase = true)
            val minCosto = filtroCostoMin.toDoubleOrNull() ?: 0.0
            val maxCosto = filtroCostoMax.toDoubleOrNull() ?: Double.MAX_VALUE
            val coincideCosto = item.costoTotal in minCosto..maxCosto

            coincideVehiculo && coincideSearch && coincideTipo && coincideCat && coincideTaller && coincideCosto
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Historial y auditoría",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = ColorDeepGreen
                    )
                    Text(
                        text = "Consulta servicios, filtros cruzados y exporta CSV",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ColorTextSecondary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val csvBuilder = StringBuilder()
                        csvBuilder.append("ID,VehiculoId,Titulo,Categoria,Tipo,Taller,Fecha,Kilometraje,CostoTotal,Observaciones\n")
                        historialFiltrado.forEach { h ->
                            val fechaStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(h.fecha))
                            csvBuilder.append("${h.id},${h.vehiculoId},\"${h.titulo}\",${h.categoria},${h.tipo},\"${h.taller}\",$fechaStr,${h.kilometrajeRealizado},${h.costoTotal},\"${h.observaciones}\"\n")
                        }
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_SUBJECT, "Historial de Mantenimientos - MiCarro")
                            putExtra(Intent.EXTRA_TEXT, csvBuilder.toString())
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Exportar Historial CSV")
                        context.startActivity(shareIntent)
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Exportar CSV")
                }
            }
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Búsqueda general") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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
                            label = { Text("Vehículo") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
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

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = { showAdvancedFilters = !showAdvancedFilters },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(56.dp)
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (showAdvancedFilters) "Ocultar Filtros" else "Filtros Avanzados")
                }
            }
        }

        item {
            AnimatedVisibility(visible = showAdvancedFilters) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Filtros Cruzados Avanzados (RF-35)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        // Categoría
                        ExposedDropdownMenuBox(
                            expanded = categoriaDropdownExpanded,
                            onExpandedChange = { categoriaDropdownExpanded = !categoriaDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = filtroCategoria ?: "Todas las categorías",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Filtrar por Categoría") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaDropdownExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = categoriaDropdownExpanded,
                                onDismissRequest = { categoriaDropdownExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Todas las categorías") },
                                    onClick = {
                                        filtroCategoria = null
                                        categoriaDropdownExpanded = false
                                    }
                                )
                                categorias.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat.nombre) },
                                        onClick = {
                                            filtroCategoria = cat.nombre
                                            categoriaDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Taller
                        OutlinedTextField(
                            value = filtroTaller,
                            onValueChange = { filtroTaller = it },
                            label = { Text("Filtrar por Taller / Responsable") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        // Rango de Costo
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = filtroCostoMin,
                                onValueChange = { filtroCostoMin = it },
                                label = { Text("Costo Mínimo ($)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = filtroCostoMax,
                                onValueChange = { filtroCostoMax = it },
                                label = { Text("Costo Máximo ($)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        // Botón limpiar filtros
                        TextButton(
                            onClick = {
                                filtroCategoria = null
                                filtroTaller = ""
                                filtroCostoMin = ""
                                filtroCostoMax = ""
                                searchQuery = ""
                                filtroTipo = null
                                filtroVehiculoId = null
                            }
                        ) {
                            Text("Limpiar todos los filtros")
                        }
                    }
                }
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
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
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(modifier = Modifier.padding(24.dp)) {
                        Text("No se encontraron registros con los filtros aplicados.")
                    }
                }
            }
        } else {
            items(historialFiltrado) { item ->
                HistorialItemCard(item)
            }
        }
    }
}

@Composable
fun HistorialItemCard(item: MantenimientoRealizado) {
    var expanded by remember { mutableStateOf(false) }
    val fechaStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(item.fecha))
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    val costoStr = currencyFormat.format(item.costoTotal)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Cat: ${item.categoria} • Taller: ${item.taller.ifBlank { "No especificado" }} • $fechaStr",
                        style = MaterialTheme.typography.bodySmall,
                        color = ColorTextSecondary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = costoStr,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ColorDeepGreen
                    )
                    Text(
                        text = "${item.kilometrajeRealizado} km",
                        style = MaterialTheme.typography.labelSmall,
                        color = ColorTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tipo: ${item.tipo.label}",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (item.tipo == TipoMantenimiento.PREVENTIVO) ColorDeepGreen else MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = ColorTextSecondary
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    if (item.observaciones.isNotBlank()) {
                        Text(
                            text = "Observaciones: ${item.observaciones}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Text(
                        text = "Desglose: Mano de Obra: ${currencyFormat.format(item.costoManoDeObra)} • Repuestos: ${currencyFormat.format(item.costoRepuestos)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = ColorTextSecondary
                    )

                    if (item.repuestos.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Repuestos instalados (${item.repuestos.size}):",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        item.repuestos.forEach { rep ->
                            Text(
                                text = "• ${rep.nombre} (${rep.marca}) - ${currencyFormat.format(rep.costo * rep.cantidad)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = ColorTextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
