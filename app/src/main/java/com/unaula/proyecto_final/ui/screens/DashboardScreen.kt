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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unaula.proyecto_final.model.EstadoMantenimiento
import com.unaula.proyecto_final.model.MantenimientoProgramado
import com.unaula.proyecto_final.model.Vehiculo
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

    val mantenimientosAlDia = mantenimientosVehiculo.count { it.estado == EstadoMantenimiento.AL_DIA }
    val mantenimientosProximos = mantenimientosVehiculo.count { it.estado == EstadoMantenimiento.PROXIMO }
    val mantenimientosVencidos = mantenimientosVehiculo.count { it.estado == EstadoMantenimiento.VENCIDO }

    val estadoGeneralColor = when {
        mantenimientosVencidos > 0 -> Color(0xFFD32F2F) // Rojo
        mantenimientosProximos > 0 -> Color(0xFFF57C00) // Naranja/Amarillo
        else -> Color(0xFF388E3C) // Verde
    }

    val estadoGeneralTexto = when {
        mantenimientosVencidos > 0 -> "Atención: Mantenimiento Vencido"
        mantenimientosProximos > 0 -> "Próximo a Vencer"
        else -> "Al día"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Cabecera con selector de vehículo
        Text(
            text = "Panel de Control",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (vehiculos.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No tienes vehículos registrados")
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(onClick = onNavigateToVehiculos) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Registrar Primer Vehículo")
                    }
                }
            }
        } else {
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

            Spacer(modifier = Modifier.height(16.dp))

            vehiculoSeleccionado?.let { v ->
                // Tarjeta principal del Vehículo
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
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
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    modifier = Modifier.size(36.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "${v.marca} ${v.modelo}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Placa: ${v.placa} • ${v.tipo.label}",
                                        style = MaterialTheme.typography.bodyMedium
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
                                Icon(Icons.Default.Speed, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "KM Actual: ${v.kilometrajeActual} km",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
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
                                .background(estadoGeneralColor.copy(alpha = 0.15f))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
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

                Spacer(modifier = Modifier.height(16.dp))

                // Botones Rápidos de Acción
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

                Spacer(modifier = Modifier.height(20.dp))

                // Lista de Alertas Activas (Mantenimientos próximos o vencidos)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Alertas de Mantenimiento",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                val alertas = mantenimientosVehiculo.filter {
                    it.estado == EstadoMantenimiento.PROXIMO || it.estado == EstadoMantenimiento.VENCIDO
                }

                if (alertas.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Box(modifier = Modifier.padding(16.dp)) {
                            Text("✅ No hay alertas pendientes. Todo está al día.")
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(alertas) { alerta ->
                            ItemAlertaCard(alerta)
                        }
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
                Column {
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
    val cardColor = if (isVencido) Color(0xFFFFEBEE) else Color(0xFFFFF3E0)
    val badgeColor = if (isVencido) Color(0xFFD32F2F) else Color(0xFFF57C00)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor)
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
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (isVencido) "VENCIDO" else "PRÓXIMO",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(badgeColor)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            if (alerta.descripcion.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = alerta.descripcion, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(6.dp))

            alerta.kilometrajeObjetivo?.let { km ->
                Text(text = "• Kilometraje objetivo: $km km", fontSize = 13.sp)
            }
            alerta.fechaObjetivo?.let { f ->
                val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(f))
                Text(text = "• Fecha objetivo: $dateStr", fontSize = 13.sp)
            }
        }
    }
}
