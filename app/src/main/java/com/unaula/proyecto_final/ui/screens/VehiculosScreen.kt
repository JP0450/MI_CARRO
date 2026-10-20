package com.unaula.proyecto_final.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.unaula.proyecto_final.data.CatalogoVehiculos
import com.unaula.proyecto_final.model.TipoVehiculo
import com.unaula.proyecto_final.model.Vehiculo
import com.unaula.proyecto_final.ui.components.DatePickerField
import com.unaula.proyecto_final.ui.theme.ColorBurgundy
import com.unaula.proyecto_final.ui.theme.ColorDeepGreen
import com.unaula.proyecto_final.ui.theme.ColorTextSecondary
import com.unaula.proyecto_final.ui.viewmodel.VehiculoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehiculosScreen(
    vehiculoViewModel: VehiculoViewModel
) {
    val vehiculos by vehiculoViewModel.vehiculos.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    // Campos del formulario
    var placaText by remember { mutableStateOf("") }
    var marcaText by remember { mutableStateOf("") }
    var modeloText by remember { mutableStateOf("") }
    var esMarcaManual by remember { mutableStateOf(false) }
    var esModeloManual by remember { mutableStateOf(false) }

    var anioText by remember { mutableStateOf("2023") }
    var kmText by remember { mutableStateOf("0") }
    var fechaSoat by remember { mutableStateOf<Long?>(null) }
    var fechaTecno by remember { mutableStateOf<Long?>(null) }
    var tipoVehiculoSeleccionado by remember { mutableStateOf(TipoVehiculo.AUTOMOVIL) }

    var tipoDropdownExpanded by remember { mutableStateOf(false) }
    var marcaDropdownExpanded by remember { mutableStateOf(false) }
    var modeloDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.White,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    placaText = ""
                    marcaText = ""
                    modeloText = ""
                    esMarcaManual = false
                    esModeloManual = false
                    anioText = "2023"
                    kmText = "0"
                    fechaSoat = null
                    fechaTecno = null
                    tipoVehiculoSeleccionado = TipoVehiculo.AUTOMOVIL
                    showAddDialog = true
                },
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Vehículo")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Gestión de vehículos",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = ColorDeepGreen
            )
            Text(
                text = "Administra tu inventario y documentación",
                style = MaterialTheme.typography.bodyMedium,
                color = ColorTextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (vehiculos.isEmpty()) {
                Text(
                    text = "No hay vehículos registrados. Pulsa + para agregar uno.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Inventario",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${vehiculos.size} vehículos",
                        style = MaterialTheme.typography.labelLarge,
                        color = ColorTextSecondary
                    )
                }
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.imePadding()
                ) {
                    items(vehiculos) { vehiculo ->
                        val hasDocError = vehiculo.documentacionVencidaOFaltante
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
                            border = BorderStroke(
                                1.dp,
                                if (hasDocError) MaterialTheme.colorScheme.error.copy(alpha = 0.55f)
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (vehiculo.tipo == TipoVehiculo.MOTOCICLETA) Icons.Default.TwoWheeler else Icons.Default.DirectionsCar,
                                        contentDescription = vehiculo.tipo.label,
                                        modifier = Modifier
                                            .height(38.dp)
                                            .background(
                                                ColorBurgundy.copy(alpha = 0.12f),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .padding(8.dp),
                                        tint = ColorDeepGreen
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "${vehiculo.marca} ${vehiculo.modelo} (${vehiculo.anio})",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = "Placa: ${vehiculo.placa} • ${vehiculo.tipo.label}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "KM: ${vehiculo.kilometrajeActual} km",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = if (hasDocError) "DOCUMENTACIÓN PENDIENTE" else "HABILITADO",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (hasDocError) MaterialTheme.colorScheme.error else androidx.compose.ui.graphics.Color(0xFF205426)
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // Fechas de seguro y tecno
                                        val soatStr = vehiculo.fechaSeguroSoat?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it)) } ?: "No ingresado"
                                        val tecnoStr = vehiculo.fechaTecnoMecanica?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it)) } ?: "No ingresado"

                                        Text(
                                            text = "SOAT: $soatStr • Tecno: $tecnoStr",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (hasDocError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { vehiculoViewModel.eliminarVehiculo(vehiculo.id) }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Registrar Nuevo Vehículo") },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                        .padding(vertical = 8.dp)
                ) {
                    // Selector de Tipo de Vehículo
                    ExposedDropdownMenuBox(
                        expanded = tipoDropdownExpanded,
                        onExpandedChange = { tipoDropdownExpanded = !tipoDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = tipoVehiculoSeleccionado.label,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipo de Vehículo") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoDropdownExpanded) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = tipoDropdownExpanded,
                            onDismissRequest = { tipoDropdownExpanded = false }
                        ) {
                            TipoVehiculo.entries.forEach { tipo ->
                                DropdownMenuItem(
                                    text = { Text(tipo.label) },
                                    onClick = {
                                        if (tipoVehiculoSeleccionado != tipo) {
                                            tipoVehiculoSeleccionado = tipo
                                            marcaText = ""
                                            modeloText = ""
                                            esMarcaManual = false
                                            esModeloManual = false
                                        }
                                        tipoDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = placaText,
                        onValueChange = { placaText = it },
                        label = { Text("Placa (Ej. ABC-123)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Selector de Marca
                    val marcasDisponibles = CatalogoVehiculos.obtenerMarcas(tipoVehiculoSeleccionado)

                    ExposedDropdownMenuBox(
                        expanded = marcaDropdownExpanded,
                        onExpandedChange = { marcaDropdownExpanded = !marcaDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = if (esMarcaManual) "Otra (Manual)" else marcaText.ifBlank { "Seleccionar Marca" },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Marca") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = marcaDropdownExpanded) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = marcaDropdownExpanded,
                            onDismissRequest = { marcaDropdownExpanded = false }
                        ) {
                            marcasDisponibles.forEach { m ->
                                DropdownMenuItem(
                                    text = { Text(m) },
                                    onClick = {
                                        if (m == "Otra (Manual)") {
                                            esMarcaManual = true
                                            marcaText = ""
                                        } else {
                                            esMarcaManual = false
                                            marcaText = m
                                            modeloText = ""
                                            esModeloManual = false
                                        }
                                        marcaDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (esMarcaManual) {
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = marcaText,
                            onValueChange = { marcaText = it },
                            label = { Text("Escribe la Marca manualmente") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Selector de Modelo
                    val modelosDisponibles = CatalogoVehiculos.obtenerModelos(tipoVehiculoSeleccionado, marcaText)

                    ExposedDropdownMenuBox(
                        expanded = modeloDropdownExpanded,
                        onExpandedChange = { modeloDropdownExpanded = !modeloDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = if (esModeloManual) "Otro (Manual)" else modeloText.ifBlank { "Seleccionar Modelo" },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Modelo") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modeloDropdownExpanded) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = modeloDropdownExpanded,
                            onDismissRequest = { modeloDropdownExpanded = false }
                        ) {
                            modelosDisponibles.forEach { mod ->
                                DropdownMenuItem(
                                    text = { Text(mod) },
                                    onClick = {
                                        if (mod == "Otro (Manual)") {
                                            esModeloManual = true
                                            modeloText = ""
                                        } else {
                                            esModeloManual = false
                                            modeloText = mod
                                        }
                                        modeloDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (esModeloManual) {
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = modeloText,
                            onValueChange = { modeloText = it },
                            label = { Text("Escribe el Modelo manualmente") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = anioText,
                            onValueChange = { anioText = it },
                            label = { Text("Año") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = kmText,
                            onValueChange = { kmText = it },
                            label = { Text("KM Actual") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Fechas de Documentación Obligatoria:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    DatePickerField(
                        label = "Vencimiento Seguro SOAT",
                        selectedTimestamp = fechaSoat,
                        onDateSelected = { fechaSoat = it },
                        minDate = System.currentTimeMillis()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DatePickerField(
                        label = "Vencimiento Técnico-Mecánica",
                        selectedTimestamp = fechaTecno,
                        onDateSelected = { fechaTecno = it },
                        minDate = System.currentTimeMillis()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (placaText.isNotBlank() && marcaText.isNotBlank()) {
                            vehiculoViewModel.guardarVehiculo(
                                Vehiculo(
                                    placa = placaText.uppercase(),
                                    marca = marcaText,
                                    modelo = modeloText,
                                    anio = anioText.toIntOrNull() ?: 2023,
                                    tipo = tipoVehiculoSeleccionado,
                                    kilometrajeActual = kmText.toIntOrNull() ?: 0,
                                    fechaSeguroSoat = fechaSoat,
                                    fechaTecnoMecanica = fechaTecno
                                )
                            )
                            showAddDialog = false
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
