package com.unaula.proyecto_final.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.ButtonDefaults
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
import com.unaula.proyecto_final.ui.theme.ColorDeepGreen
import com.unaula.proyecto_final.ui.theme.ColorTextSecondary
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
    var showOdometroWarning by remember { mutableStateOf(false) }
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
        else -> com.unaula.proyecto_final.ui.theme.ColorNeonGreen
    }

    val estadoGeneralTexto = when {
        docVencidaOFaltante -> "Falta Documentación (No puede circular)"
        mantenimientosVencidos > 0 -> "Atención: Mantenimiento Vencido"
        mantenimientosProximos > 0 -> "Próximo a Vencer"
        else -> "Al día"
    }
    val vehiculosHabilitados = vehiculos.count { !it.documentacionVencidaOFaltante }
    val alertasActivas = mantenimientosProximos + mantenimientosVencidos +
        if (docVencidaOFaltante) 1 else 0
    val fechaActual = remember {
        SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-CO"))
            .format(Date())
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.White)
            .imePadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Tu flota, bajo control",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = ColorDeepGreen
            )
            Text(
                text = fechaActual.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyMedium,
                color = ColorTextSecondary
            )
        }

        if (vehiculos.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No tienes vehículos registrados",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onNavigateToVehiculos,
                            shape = RoundedCornerShape(12.dp)
                        ) {
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
                        shape = RoundedCornerShape(14.dp),
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
                    AnimatedContent(
                        targetState = v,
                        label = "TransicionVehiculo",
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) + slideInVertically { it / 8 } togetherWith
                                    fadeOut(animationSpec = tween(200))
                        }
                    ) { vehiculo ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(ColorDeepGreen.copy(alpha = 0.12f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (vehiculo.tipo == TipoVehiculo.MOTOCICLETA) Icons.Default.TwoWheeler else Icons.Default.DirectionsCar,
                                                contentDescription = vehiculo.tipo.label,
                                                modifier = Modifier.size(28.dp),
                                                tint = ColorDeepGreen
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "${vehiculo.marca} ${vehiculo.modelo}",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = ColorDeepGreen
                                            )
                                            Text(
                                                text = "Placa: ${vehiculo.placa} • ${vehiculo.tipo.label}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = ColorDeepGreen.copy(alpha = 0.82f)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Speed, contentDescription = null, tint = ColorDeepGreen)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${vehiculo.kilometrajeActual} km",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = ColorDeepGreen
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            nuevoKmText = vehiculo.kilometrajeActual.toString()
                                            showKmDialog = true
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, ColorDeepGreen.copy(alpha = 0.4f))
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = ColorDeepGreen)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Actualizar", color = ColorDeepGreen)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(estadoGeneralColor.copy(alpha = 0.18f))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(estadoGeneralColor)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = estadoGeneralTexto,
                                        color = estadoGeneralColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    if (v.documentacionVencidaOFaltante) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "DOCUMENTACIÓN PENDIENTE",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
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
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = v.mensajeDocumentacion,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onNavigateToProgramar,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Programar")
                        }

                        Button(
                            onClick = onNavigateToRegistrar,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Registrar")
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DashboardStat(
                            modifier = Modifier.weight(1f),
                            value = vehiculos.size.toString().padStart(2, '0'),
                            label = "Vehículos",
                            valueColor = ColorDeepGreen
                        )
                        DashboardStat(
                            modifier = Modifier.weight(1f),
                            value = vehiculosHabilitados.toString().padStart(2, '0'),
                            label = "Habilitados",
                            valueColor = androidx.compose.ui.graphics.Color(0xFF205426)
                        )
                        DashboardStat(
                            modifier = Modifier.weight(1f),
                            value = (vehiculos.size - vehiculosHabilitados).toString().padStart(2, '0'),
                            label = "Bloqueados",
                            valueColor = androidx.compose.ui.graphics.Color(0xFFB52E43)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Requieren tu atención",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (alertasActivas > 0) {
                            Text(
                                text = "$alertasActivas alertas",
                                style = MaterialTheme.typography.bodySmall,
                                color = ColorTextSecondary
                            )
                        }
                    }
                }

                val alertas = mantenimientosVehiculo.filter {
                    it.estado == EstadoMantenimiento.PROXIMO || it.estado == EstadoMantenimiento.VENCIDO
                }

                if (alertas.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        ) {
                            Box(modifier = Modifier.padding(16.dp)) {
                                Text("✅ No hay mantenimientos pendientes. Todo está al día.")
                            }
                        }
                    }
                } else {
                    items(alertas) { alerta ->
                        ItemAlertaCard(
                            alerta = alerta,
                            onPosponerClick = { mantenimientoViewModel.posponerAlerta(alerta.id) }
                        )
                    }
                }
            }
        }
    }

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
                        shape = RoundedCornerShape(12.dp),
                        label = { Text("Nuevo Kilometraje") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val km = nuevoKmText.toIntOrNull()
                        val actualKm = vehiculoSeleccionado?.kilometrajeActual ?: 0
                        if (km != null && vehiculoSeleccionado != null) {
                            if (km < actualKm) {
                                showOdometroWarning = true
                            } else {
                                vehiculoViewModel.actualizarKilometraje(vehiculoSeleccionado!!.id, km)
                                mantenimientoViewModel.evaluarAlertas(vehiculoSeleccionado!!.id, km)
                                showKmDialog = false
                            }
                        }
                    },
                    shape = RoundedCornerShape(10.dp)
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

    if (showOdometroWarning) {
        AlertDialog(
            onDismissRequest = { showOdometroWarning = false },
            title = { Text("⚠️ Advertencia de Odómetro Menor") },
            text = {
                Text("El nuevo kilometraje (${nuevoKmText}) es menor que el registro actual (${vehiculoSeleccionado?.kilometrajeActual} km).\n\n¿Deseas confirmar la lectura anómala (ej. reemplazo de tablero)?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        val km = nuevoKmText.toIntOrNull()
                        if (km != null && vehiculoSeleccionado != null) {
                            vehiculoViewModel.actualizarKilometraje(vehiculoSeleccionado!!.id, km, "Lectura menor / reemplazo de tablero")
                            mantenimientoViewModel.evaluarAlertas(vehiculoSeleccionado!!.id, km)
                        }
                        showOdometroWarning = false
                        showKmDialog = false
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Sí, confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOdometroWarning = false }) {
                    Text("Corregir")
                }
            }
        )
    }
}

@Composable
private fun DashboardStat(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    valueColor: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = valueColor)
            Text(label, style = MaterialTheme.typography.labelSmall, color = ColorTextSecondary, maxLines = 1)
        }
    }
}

@Composable
fun ItemAlertaCard(
    alerta: MantenimientoProgramado,
    onPosponerClick: () -> Unit
) {
    val isVencido = alerta.estado == EstadoMantenimiento.VENCIDO
    val containerColor = if (isVencido) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.tertiaryContainer
    val contentColor = if (isVencido) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onTertiaryContainer

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, contentColor.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(contentColor.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(contentColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isVencido) "VENCIDO" else "PRÓXIMO",
                        color = contentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
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

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onPosponerClick,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Posponer (+7 días)", fontSize = 12.sp, color = contentColor)
            }
        }
    }
}
