package com.unaula.proyecto_final.ui.screens

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
import com.unaula.proyecto_final.model.TipoVehiculo
import com.unaula.proyecto_final.model.Vehiculo
import com.unaula.proyecto_final.ui.components.DatePickerField
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
    var anioText by remember { mutableStateOf("2023") }
    var kmText by remember { mutableStateOf("0") }
    var fechaSoat by remember { mutableStateOf<Long?>(null) }
    var fechaTecno by remember { mutableStateOf<Long?>(null) }
    var tipoVehiculoSeleccionado by remember { mutableStateOf(TipoVehiculo.AUTOMOVIL) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    placaText = ""
                    marcaText = ""
                    modeloText = ""
                    anioText = "2023"
                    kmText = "0"
                    fechaSoat = null
                    fechaTecno = null
                    tipoVehiculoSeleccionado = TipoVehiculo.AUTOMOVIL
                    showAddDialog = true
                }
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
                text = "Gestión de Vehículos",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (vehiculos.isEmpty()) {
                Text(
                    text = "No hay vehículos registrados. Pulsa + para agregar uno.",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.imePadding()
                ) {
                    items(vehiculos) { vehiculo ->
                        val hasDocError = vehiculo.documentacionVencidaOFaltante
                        val cardBg = if (hasDocError) {
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = cardBg)
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
                                        modifier = Modifier.height(36.dp),
                                        tint = MaterialTheme.colorScheme.primary
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
                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = tipoVehiculoSeleccionado.label,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipo de Vehículo") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            TipoVehiculo.entries.forEach { tipo ->
                                DropdownMenuItem(
                                    text = { Text(tipo.label) },
                                    onClick = {
                                        tipoVehiculoSeleccionado = tipo
                                        dropdownExpanded = false
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
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = marcaText,
                        onValueChange = { marcaText = it },
                        label = { Text("Marca (Ej. Toyota, Yamaha)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = modeloText,
                        onValueChange = { modeloText = it },
                        label = { Text("Modelo (Ej. Corolla, FZ 25)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = anioText,
                            onValueChange = { anioText = it },
                            label = { Text("Año") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = kmText,
                            onValueChange = { kmText = it },
                            label = { Text("KM Actual") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
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
                    }
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
