package com.unaula.proyecto_final.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OilBarrel
import androidx.compose.material3.Button
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
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unaula.proyecto_final.model.CriterioAlerta
import com.unaula.proyecto_final.model.MantenimientoProgramado
import com.unaula.proyecto_final.ui.components.DatePickerField
import com.unaula.proyecto_final.ui.viewmodel.MantenimientoViewModel
import com.unaula.proyecto_final.ui.viewmodel.VehiculoViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProgramarMantenimientoScreen(
    vehiculoViewModel: VehiculoViewModel,
    mantenimientoViewModel: MantenimientoViewModel,
    onNavigateBack: () -> Unit
) {
    val vehiculoSeleccionado by vehiculoViewModel.vehiculoSeleccionado.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val kmActual = vehiculoSeleccionado?.kilometrajeActual ?: 0

    var tituloText by remember { mutableStateOf("") }
    var descripcionText by remember { mutableStateOf("") }
    var criterioSeleccionado by remember { mutableStateOf(CriterioAlerta.AMBOS) }
    var kmObjetivoText by remember { mutableStateOf((kmActual + 5000).toString()) }
    var fechaObjetivoTimestamp by remember { mutableStateOf<Long?>(System.currentTimeMillis() + (180L * 24 * 60 * 60 * 1000)) }
    var frecuenciaKmText by remember { mutableStateOf("5000") }
    var frecuenciaMesesText by remember { mutableStateOf("6") }

    var criterioDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.White,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Programar mantenimiento",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = com.unaula.proyecto_final.ui.theme.ColorDeepGreen
            )
            Text(
                text = "Define el próximo servicio y sus alertas",
                style = MaterialTheme.typography.bodyMedium,
                color = com.unaula.proyecto_final.ui.theme.ColorTextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = com.unaula.proyecto_final.ui.theme.ColorMintAqua
                )
            ) {
                Text(
                    text = "Vehículo: ${vehiculoSeleccionado?.marca ?: "Sin"} ${vehiculoSeleccionado?.modelo ?: "seleccionar"} (${vehiculoSeleccionado?.placa ?: "—"})",
                    modifier = Modifier.padding(14.dp),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Plantillas Rápidas para Mantenimiento Periódico
            Text(
                text = "Plantillas Rápidas de Mantenimiento:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = tituloText == "Cambio de Aceite de Motor",
                    onClick = {
                        tituloText = "Cambio de Aceite de Motor"
                        descripcionText = "Cambio de aceite de motor y filtro de aceite."
                        frecuenciaKmText = "5000"
                        frecuenciaMesesText = "6"
                        kmObjetivoText = (kmActual + 5000).toString()
                        fechaObjetivoTimestamp = System.currentTimeMillis() + (180L * 24 * 60 * 60 * 1000)
                        criterioSeleccionado = CriterioAlerta.AMBOS
                    },
                    label = { Text("🛢️ Aceite (5.000 km)") }
                )

                FilterChip(
                    selected = tituloText == "Cambio de Filtro de Aire",
                    onClick = {
                        tituloText = "Cambio de Filtro de Aire"
                        descripcionText = "Reemplazo del elemento filtrante de aire del motor."
                        frecuenciaKmText = "10000"
                        frecuenciaMesesText = "12"
                        kmObjetivoText = (kmActual + 10000).toString()
                        fechaObjetivoTimestamp = System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000)
                        criterioSeleccionado = CriterioAlerta.AMBOS
                    },
                    label = { Text("🌬️ Filtro Aire (10.000 km)") }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (isLandscape) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = tituloText,
                        onValueChange = { tituloText = it },
                        label = { Text("Título / Servicio") },
                        placeholder = { Text("Ej. Cambio de Aceite o Filtro") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    ExposedDropdownMenuBox(
                        expanded = criterioDropdownExpanded,
                        onExpandedChange = { criterioDropdownExpanded = !criterioDropdownExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = criterioSeleccionado.label,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Criterio Alerta") },
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
                }
            } else {
                OutlinedTextField(
                    value = tituloText,
                    onValueChange = { tituloText = it },
                    label = { Text("Título / Nombre del Mantenimiento") },
                    placeholder = { Text("Ej. Cambio de Aceite de Motor / Filtro de Aire") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

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
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = descripcionText,
                onValueChange = { descripcionText = it },
                label = { Text("Observaciones / Detalles del Servicio") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (criterioSeleccionado == CriterioAlerta.KILOMETRAJE || criterioSeleccionado == CriterioAlerta.AMBOS) {
                OutlinedTextField(
                    value = kmObjetivoText,
                    onValueChange = { kmObjetivoText = it },
                    label = { Text("Siguiente cambio a los KM (Objetivo)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (criterioSeleccionado == CriterioAlerta.FECHA || criterioSeleccionado == CriterioAlerta.AMBOS) {
                DatePickerField(
                    label = "Fecha Objetivo de Cambio",
                    selectedTimestamp = fechaObjetivoTimestamp,
                    onDateSelected = { fechaObjetivoTimestamp = it },
                    minDate = System.currentTimeMillis()
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Text(
                text = "Intervalo Periódico de Repetición:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = frecuenciaKmText,
                    onValueChange = { frecuenciaKmText = it },
                    label = { Text("Avisar cada (KM)") },
                    placeholder = { Text("Ej. 5000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = frecuenciaMesesText,
                    onValueChange = { frecuenciaMesesText = it },
                    label = { Text("Avisar cada (Meses)") },
                    placeholder = { Text("Ej. 6") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tarjeta informativa del intervalo
            val freqKm = frecuenciaKmText.toIntOrNull() ?: 0
            val targetKm = kmObjetivoText.toIntOrNull() ?: (kmActual + freqKm)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "KM Actual del vehículo: $kmActual km.\n" +
                                "Siguiente alerta a los $targetKm km " +
                                if (freqKm > 0) "(Repetición automática cada $freqKm km)." else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (tituloText.isNotBlank() && vehiculoSeleccionado != null) {
                        val kmObj = kmObjetivoText.toIntOrNull() ?: (kmActual + 5000)

                        mantenimientoViewModel.guardarProgramado(
                            MantenimientoProgramado(
                                vehiculoId = vehiculoSeleccionado!!.id,
                                titulo = tituloText,
                                descripcion = descripcionText,
                                criterio = criterioSeleccionado,
                                kilometrajeObjetivo = kmObj,
                                fechaObjetivo = fechaObjetivoTimestamp,
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
                Text("Guardar Programación Periódica")
            }
        }
    }
}
