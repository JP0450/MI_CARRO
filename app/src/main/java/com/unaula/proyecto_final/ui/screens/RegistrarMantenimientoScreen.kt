package com.unaula.proyecto_final.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.unaula.proyecto_final.model.MantenimientoRealizado
import com.unaula.proyecto_final.model.Repuesto
import com.unaula.proyecto_final.model.TipoMantenimiento
import com.unaula.proyecto_final.ui.viewmodel.MantenimientoViewModel
import com.unaula.proyecto_final.ui.viewmodel.VehiculoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarMantenimientoScreen(
    vehiculoViewModel: VehiculoViewModel,
    mantenimientoViewModel: MantenimientoViewModel,
    onNavigateBack: () -> Unit
) {
    val vehiculoSeleccionado by vehiculoViewModel.vehiculoSeleccionado.collectAsState()

    var tituloText by remember { mutableStateOf("") }
    var tallerText by remember { mutableStateOf("") }
    var kmRealizadoText by remember { mutableStateOf(vehiculoSeleccionado?.kilometrajeActual?.toString() ?: "0") }
    var costoTotalText by remember { mutableStateOf("") }
    var observacionesText by remember { mutableStateOf("") }
    var tipoMantenimiento by remember { mutableStateOf(TipoMantenimiento.PREVENTIVO) }

    var tipoDropdownExpanded by remember { mutableStateOf(false) }

    // Repuestos dinámicos
    val repuestosList = remember { mutableStateListOf<Repuesto>() }

    var repNombre by remember { mutableStateOf("") }
    var repMarca by remember { mutableStateOf("") }
    var repCosto by remember { mutableStateOf("") }
    var repGarantiaMeses by remember { mutableStateOf("12") }
    var repGarantiaKm by remember { mutableStateOf("10000") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Mantenimiento Realizado") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "Vehículo: ${vehiculoSeleccionado?.marca} ${vehiculoSeleccionado?.modelo} (${vehiculoSeleccionado?.placa})",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = tituloText,
                    onValueChange = { tituloText = it },
                    label = { Text("Servicio / Título") },
                    placeholder = { Text("Ej. Mantenimiento de los 40,000 km") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                ExposedDropdownMenuBox(
                    expanded = tipoDropdownExpanded,
                    onExpandedChange = { tipoDropdownExpanded = !tipoDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = tipoMantenimiento.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de Mantenimiento") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = tipoDropdownExpanded,
                        onDismissRequest = { tipoDropdownExpanded = false }
                    ) {
                        TipoMantenimiento.entries.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo.label) },
                                onClick = {
                                    tipoMantenimiento = tipo
                                    tipoDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = tallerText,
                    onValueChange = { tallerText = it },
                    label = { Text("Taller / Mecánico") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = kmRealizadoText,
                        onValueChange = { kmRealizadoText = it },
                        label = { Text("KM Realizado") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = costoTotalText,
                        onValueChange = { costoTotalText = it },
                        label = { Text("Costo Total ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = observacionesText,
                    onValueChange = { observacionesText = it },
                    label = { Text("Observaciones / Diagnóstico") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Repuestos Instalados y Garantía",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row {
                            OutlinedTextField(
                                value = repNombre,
                                onValueChange = { repNombre = it },
                                label = { Text("Repuesto") },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            OutlinedTextField(
                                value = repMarca,
                                onValueChange = { repMarca = it },
                                label = { Text("Marca") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row {
                            OutlinedTextField(
                                value = repCosto,
                                onValueChange = { repCosto = it },
                                label = { Text("Costo ($)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            OutlinedTextField(
                                value = repGarantiaMeses,
                                onValueChange = { repGarantiaMeses = it },
                                label = { Text("Garantía (Meses)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (repNombre.isNotBlank()) {
                                    repuestosList.add(
                                        Repuesto(
                                            nombre = repNombre,
                                            marca = repMarca,
                                            costo = repCosto.toDoubleOrNull() ?: 0.0,
                                            garantiaMeses = repGarantiaMeses.toIntOrNull() ?: 0,
                                            garantiaKm = repGarantiaKm.toIntOrNull() ?: 0,
                                            kilometrajeInstalacion = kmRealizadoText.toIntOrNull() ?: 0
                                        )
                                    )
                                    repNombre = ""
                                    repMarca = ""
                                    repCosto = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Agregar Repuesto")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            items(repuestosList) { rep ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "${rep.nombre} (${rep.marca})", fontWeight = FontWeight.Bold)
                            Text(text = "Costo: $${rep.costo} • Garantía: ${rep.garantiaMeses} meses / ${rep.garantiaKm} km")
                        }
                        IconButton(onClick = { repuestosList.remove(rep) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar repuesto", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (tituloText.isNotBlank() && vehiculoSeleccionado != null) {
                            val kmRealizado = kmRealizadoText.toIntOrNull() ?: (vehiculoSeleccionado?.kilometrajeActual ?: 0)
                            val costoTotal = costoTotalText.toDoubleOrNull() ?: 0.0

                            mantenimientoViewModel.registrarRealizado(
                                MantenimientoRealizado(
                                    vehiculoId = vehiculoSeleccionado!!.id,
                                    titulo = tituloText,
                                    tipo = tipoMantenimiento,
                                    taller = tallerText,
                                    fecha = System.currentTimeMillis(),
                                    kilometrajeRealizado = kmRealizado,
                                    costoTotal = costoTotal,
                                    observaciones = observacionesText,
                                    repuestos = repuestosList.toList()
                                )
                            )

                            // Actualizar kilometraje del vehículo si es mayor al actual
                            if (kmRealizado > vehiculoSeleccionado!!.kilometrajeActual) {
                                vehiculoViewModel.actualizarKilometraje(vehiculoSeleccionado!!.id, kmRealizado)
                            }

                            onNavigateBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar Mantenimiento Realizado")
                }
            }
        }
    }
}
