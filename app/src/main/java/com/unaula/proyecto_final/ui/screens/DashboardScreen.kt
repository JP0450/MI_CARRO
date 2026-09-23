package com.unaula.proyecto_final.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unaula.proyecto_final.model.EstadoMantenimiento
import com.unaula.proyecto_final.model.MantenimientoProgramado
import com.unaula.proyecto_final.model.TipoVehiculo
import com.unaula.proyecto_final.ui.viewmodel.MantenimientoViewModel
import com.unaula.proyecto_final.ui.viewmodel.VehiculoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    vehiculoViewModel: VehiculoViewModel,
    mantenimientoViewModel: MantenimientoViewModel,
    onNavigateToProgramar: () -> Unit,
    onNavigateToRegistrar: () -> Unit,
    onNavigateToVehiculos: () -> Unit
) {
    val vehiculos by vehiculoViewModel.vehiculos.collectAsState()
    val vehiculoSeleccionado by vehiculoViewModel.vehiculoSeleccionado.collectAsState()
    val programados by mantenimientoViewModel.programados.collectAsState()

    var showKmDialog by remember { mutableStateOf(false) }
    var nuevoKmText by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(vehiculoSeleccionado) {
        vehiculoSeleccionado?.let { v ->
            mantenimientoViewModel.evaluarAlertas(v.id, v.kilometrajeActual)
        }
    }

    val mantenimientosVehiculo = remember(programados, vehiculoSeleccionado) {
        vehiculoSeleccionado?.let { v ->
            programados.filter { it.vehiculoId == v.id }
        } ?: emptyList()
    }

    val mantenimientosProximos = mantenimientosVehiculo.count { it.estado == EstadoMantenimiento.PROXIMO }
    val mantenimientosVencidos = mantenimientosVehiculo.count { it.estado == EstadoMantenimiento.VENCIDO }
    val docVencidaOFaltante = vehiculoSeleccionado?.documentacionVencidaOFaltante == true

    val estadoGeneralColor = when {
        docVencidaOFaltante || mantenimientosVencidos > 0 -> MaterialTheme.colorScheme.error
        mantenimientosProximos > 0 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }

    val estadoGeneralTexto = when {
        docVencidaOFaltante -> "Atención: Falta Documentación (No puede circular)"
        mantenimientosVencidos > 0 -> "Atención: Mantenimiento Vencido"
        mantenimientosProximos > 0 -> "Próximo a Vencer"
        else -> "Al día"
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Panel de Control",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (vehiculos.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No tienes vehículos registrados", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(onClick = onNavigateToVehiculos) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Registrar Primer Vehículo")
                        }
                    }
                }
            }
        } else {
            item {
                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = vehiculoSeleccionado?.let { "${it.marca} ${it.modelo} (${it.placa})" } ?: "Seleccionar vehículo",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Vehículo Activo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        vehiculos.forEach { vehiculo ->
                            DropdownMenuItem(
                                text = { Text("${vehiculo.marca} ${vehiculo.modelo} (${vehiculo.placa}) - ${vehiculo.tipo.label}") },
                                onClick = {
                                    vehiculoViewModel.seleccionarVehiculo(vehiculo)
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            vehiculoSeleccionado?.let { v ->
                item {
                    // Tarjeta principal del Vehículo
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (v.tipo == TipoVehiculo.MOTOCICLETA) Icons.Default.TwoWheeler else Icons.Default.DirectionsCar,
                                        contentDescription = v.tipo.label,
                                        modifier = Modifier.size(36.dp),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "${v.marca} ${v.modelo}",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = "Placa: ${v.placa} • ${v.tipo.label}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Kilometraje actual con botón de edición rápida
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Speed, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "KM Actual: ${v.kilometrajeActual} km",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        nuevoKmText = v.kilometrajeActual.toString()
                                        showKmDialog = true
                                    }
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Actualizar")
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Indicador Semáforo de Estado General
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(estadoGeneralColor.copy(alpha = 0.2f))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(estadoGeneralColor)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Estado: $estadoGeneralTexto",
                                    color = estadoGeneralColor,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                // Alerta específica de Documentación (SOAT / TecnoMecánica)
                item {
                    if (v.documentacionVencidaOFaltante) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "DOCUMENTACIÓN PENDIENTE",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Text(
                                        text = v.mensajeDocumentacion,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = v.mensajeDocumentacion,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Botones Rápidos de Acción
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onNavigateToProgramar,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Programar")
                        }

                        Button(
                            onClick = onNavigateToRegistrar,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Registrar")
                        }
                    }
                }

                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Alertas de Mantenimiento",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                val alertas = mantenimientosVehiculo.filter {
                    it.estado == EstadoMantenimiento.PROXIMO || it.estado == EstadoMantenimiento.VENCIDO
                }

                if (alertas.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Box(modifier = Modifier.padding(16.dp)) {
                                Text("✅ No hay mantenimientos pendientes. Todo está al día.")
                            }
                        }
                    }
                } else {
                    items(alertas) { alerta ->
                        ItemAlertaCard(alerta)
                    }
                }
            }
        }
    }

    // Diálogo para Actualizar Kilometraje
    if (showKmDialog) {
        AlertDialog(
            onDismissRequest = { showKmDialog = false },
            title = { Text("Actualizar Kilometraje") },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                ) {
                    Text("Ingresa el kilometraje actual del vehículo:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = nuevoKmText,
                        onValueChange = { nuevoKmText = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        label = { Text("Nuevo Kilometraje") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val km = nuevoKmText.toIntOrNull()
                        if (km != null && vehiculoSeleccionado != null) {
                            vehiculoViewModel.actualizarKilometraje(vehiculoSeleccionado!!.id, km)
                            mantenimientoViewModel.evaluarAlertas(vehiculoSeleccionado!!.id, km)
                        }
                        showKmDialog = false
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showKmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ItemAlertaCard(alerta: MantenimientoProgramado) {
    val isVencido = alerta.estado == EstadoMantenimiento.VENCIDO
    val containerColor = if (isVencido) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.tertiaryContainer
    val contentColor = if (isVencido) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onTertiaryContainer

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = alerta.titulo,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isVencido) "VENCIDO" else "PRÓXIMO",
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(contentColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            if (alerta.descripcion.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = alerta.descripcion, style = MaterialTheme.typography.bodySmall, color = contentColor)
            }

            Spacer(modifier = Modifier.height(6.dp))

            alerta.kilometrajeObjetivo?.let { km ->
                Text(text = "• Kilometraje objetivo: $km km", fontSize = 13.sp, color = contentColor)
            }
            alerta.fechaObjetivo?.let { f ->
                val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(f))
                Text(text = "• Fecha objetivo: $dateStr", fontSize = 13.sp, color = contentColor)
            }
        }
    }
}
