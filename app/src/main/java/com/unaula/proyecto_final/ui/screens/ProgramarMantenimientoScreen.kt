package com.unaula.proyecto_final.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.unaula.proyecto_final.model.CriterioAlerta
import com.unaula.proyecto_final.model.MantenimientoProgramado
import com.unaula.proyecto_final.ui.viewmodel.MantenimientoViewModel
import com.unaula.proyecto_final.ui.viewmodel.VehiculoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramarMantenimientoScreen(
    vehiculoViewModel: VehiculoViewModel,
    mantenimientoViewModel: MantenimientoViewModel,
    onNavigateBack: () -> Unit
) {
    val vehiculos by vehiculoViewModel.vehiculos.collectAsState()
    val vehiculoSeleccionado by vehiculoViewModel.vehiculoSeleccionado.collectAsState()

    var tituloText by remember { mutableStateOf("") }
    var descripcionText by remember { mutableStateOf("") }
    var criterioSeleccionado by remember { mutableStateOf(CriterioAlerta.AMBOS) }
    var kmObjetivoText by remember { mutableStateOf("") }
    var diasObjetivoText by remember { mutableStateOf("30") } // Ej: En 30 días
    var frecuenciaKmText by remember { mutableStateOf("5000") }
    var frecuenciaMesesText by remember { mutableStateOf("6") }

    var criterioDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Programar Mantenimiento") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Vehículo: ${vehiculoSeleccionado?.marca} ${vehiculoSeleccionado?.modelo} (${vehiculoSeleccionado?.placa})",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = tituloText,
                onValueChange = { tituloText = it },
                label = { Text("Título / Nombre del Mantenimiento") },
                placeholder = { Text("Ej. Cambio de Aceite y Filtro") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = descripcionText,
                onValueChange = { descripcionText = it },
                label = { Text("Observaciones / Detalles") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Selector de Criterio
            ExposedDropdownMenuBox(
                expanded = criterioDropdownExpanded,
                onExpandedChange = { criterioDropdownExpanded = !criterioDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = criterioSeleccionado.label,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Criterio de Alerta") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = criterioDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = criterioDropdownExpanded,
                    onDismissRequest = { criterioDropdownExpanded = false }
                ) {
                    CriterioAlerta.entries.forEach { crit ->
                        DropdownMenuItem(
                            text = { Text(crit.label) },
                            onClick = {
                                criterioSeleccionado = crit
                                criterioDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (criterioSeleccionado == CriterioAlerta.KILOMETRAJE || criterioSeleccionado == CriterioAlerta.AMBOS) {
                OutlinedTextField(
                    value = kmObjetivoText,
                    onValueChange = { kmObjetivoText = it },
                    label = { Text("Kilometraje Objetivo (Ej. ${ (vehiculoSeleccionado?.kilometrajeActual ?: 0) + 5000 })") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (criterioSeleccionado == CriterioAlerta.FECHA || criterioSeleccionado == CriterioAlerta.AMBOS) {
                OutlinedTextField(
                    value = diasObjetivoText,
                    onValueChange = { diasObjetivoText = it },
                    label = { Text("Plazo en días para vencimiento") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Row {
                OutlinedTextField(
                    value = frecuenciaKmText,
                    onValueChange = { frecuenciaKmText = it },
                    label = { Text("Frecuencia KM") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = frecuenciaMesesText,
                    onValueChange = { frecuenciaMesesText = it },
                    label = { Text("Frecuencia Meses") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (tituloText.isNotBlank() && vehiculoSeleccionado != null) {
                        val dias = diasObjetivoText.toLongOrNull() ?: 30L
                        val fechaObj = System.currentTimeMillis() + (dias * 24 * 60 * 60 * 1000)
                        val kmObj = kmObjetivoText.toIntOrNull() ?: ((vehiculoSeleccionado?.kilometrajeActual ?: 0) + 5000)

                        mantenimientoViewModel.guardarProgramado(
                            MantenimientoProgramado(
                                vehiculoId = vehiculoSeleccionado!!.id,
                                titulo = tituloText,
                                descripcion = descripcionText,
                                criterio = criterioSeleccionado,
                                kilometrajeObjetivo = kmObj,
                                fechaObjetivo = fechaObj,
                                frecuenciaKm = frecuenciaKmText.toIntOrNull(),
                                frecuenciaMeses = frecuenciaMesesText.toIntOrNull()
                            )
                        )
                        mantenimientoViewModel.evaluarAlertas(vehiculoSeleccionado!!.id, vehiculoSeleccionado!!.kilometrajeActual)
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Programación")
            }
        }
    }
}
