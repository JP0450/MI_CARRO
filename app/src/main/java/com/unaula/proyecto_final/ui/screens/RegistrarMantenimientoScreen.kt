package com.unaula.proyecto_final.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.unaula.proyecto_final.model.MantenimientoRealizado
import com.unaula.proyecto_final.model.Repuesto
import com.unaula.proyecto_final.model.TipoMantenimiento
import com.unaula.proyecto_final.ui.components.DatePickerField
import com.unaula.proyecto_final.ui.viewmodel.MantenimientoViewModel
import com.unaula.proyecto_final.ui.viewmodel.VehiculoViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RegistrarMantenimientoScreen(
    vehiculoViewModel: VehiculoViewModel,
    mantenimientoViewModel: MantenimientoViewModel,
    onNavigateBack: () -> Unit
) {
    val vehiculoSeleccionado by vehiculoViewModel.vehiculoSeleccionado.collectAsState()
    val categorias by mantenimientoViewModel.categorias.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var tituloText by remember { mutableStateOf("") }
    var tallerText by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Otros") }
    var fechaEjecucion by remember { mutableStateOf(System.currentTimeMillis()) }
    var kmRealizadoText by remember { mutableStateOf(vehiculoSeleccionado?.kilometrajeActual?.toString() ?: "0") }

    // Desglose de costos (RF-19)
    var costoManoDeObraText by remember { mutableStateOf("") }
    var observacionesText by remember { mutableStateOf("") }
    var tipoMantenimiento by remember { mutableStateOf(TipoMantenimiento.PREVENTIVO) }

    var tipoDropdownExpanded by remember { mutableStateOf(false) }
    var categoriaDropdownExpanded by remember { mutableStateOf(false) }

    // Repuestos dinámicos detallados (RF-25)
    val repuestosList = remember { mutableStateListOf<Repuesto>() }

    var repNombre by remember { mutableStateOf("") }
    var repMarca by remember { mutableStateOf("") }
    var repReferencia by remember { mutableStateOf("") }
    var repCantidadText by remember { mutableStateOf("1") }
    var repProveedor by remember { mutableStateOf("") }
    var repCostoUnitario by remember { mutableStateOf("") }
    var repGarantiaMeses by remember { mutableStateOf("12") }
    var repGarantiaKm by remember { mutableStateOf("10000") }

    val costoRepuestosSuma = repuestosList.sumOf { it.costo * it.cantidad }
    val manoDeObraVal = costoManoDeObraText.toDoubleOrNull() ?: 0.0
    val costoTotalVal = manoDeObraVal + costoRepuestosSuma
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.White,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            item {
                Text(
                    text = "Registrar servicio",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = com.unaula.proyecto_final.ui.theme.ColorDeepGreen
                )
                Text(
                    text = "Desglose de costos y repuestos detallados",
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
                Spacer(modifier = Modifier.height(10.dp))

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

                // Selector de Categoría
                ExposedDropdownMenuBox(
                    expanded = categoriaDropdownExpanded,
                    onExpandedChange = { categoriaDropdownExpanded = !categoriaDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = categoriaSeleccionada,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría de Mantenimiento") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaDropdownExpanded) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoriaDropdownExpanded,
                        onDismissRequest = { categoriaDropdownExpanded = false }
                    ) {
                        categorias.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.nombre) },
                                onClick = {
                                    categoriaSeleccionada = cat.nombre
                                    categoriaDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = tallerText,
                    onValueChange = { tallerText = it },
                    label = { Text("Taller / Mecánico / Responsable") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                DatePickerField(
                    label = "Fecha de Ejecución",
                    selectedTimestamp = fechaEjecucion,
                    onDateSelected = { fechaEjecucion = it },
                    maxDate = System.currentTimeMillis()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = kmRealizadoText,
                    onValueChange = { kmRealizadoText = it },
                    label = { Text("KM Realizado") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Desglose Financiero (RF-19)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = costoManoDeObraText,
                        onValueChange = { costoManoDeObraText = it },
                        label = { Text("Mano de Obra ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = currencyFormat.format(costoRepuestosSuma),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Costo Repuestos (Auto)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(
                        text = "Costo Total Calculado: ${currencyFormat.format(costoTotalVal)}",
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = com.unaula.proyecto_final.ui.theme.ColorDeepGreen
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = observacionesText,
                    onValueChange = { observacionesText = it },
                    label = { Text("Observaciones / Diagnóstico") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Repuestos Instalados (RF-25)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = repNombre,
                                onValueChange = { repNombre = it },
                                label = { Text("Nombre Repuesto") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = repMarca,
                                onValueChange = { repMarca = it },
                                label = { Text("Marca") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = repReferencia,
                                onValueChange = { repReferencia = it },
                                label = { Text("Referencia") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = repCantidadText,
                                onValueChange = { repCantidadText = it },
                                label = { Text("Cantidad") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = repProveedor,
                                onValueChange = { repProveedor = it },
                                label = { Text("Proveedor") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = repCostoUnitario,
                                onValueChange = { repCostoUnitario = it },
                                label = { Text("Valor Unitario ($)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = repGarantiaMeses,
                                onValueChange = { repGarantiaMeses = it },
                                label = { Text("Garantía (Meses)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = repGarantiaKm,
                                onValueChange = { repGarantiaKm = it },
                                label = { Text("Garantía (KM)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (repNombre.isNotBlank()) {
                                    val unitCosto = repCostoUnitario.toDoubleOrNull() ?: 0.0
                                    val cant = repCantidadText.toIntOrNull() ?: 1
                                    repuestosList.add(
                                        Repuesto(
                                            nombre = repNombre,
                                            marca = repMarca,
                                            referencia = repReferencia,
                                            cantidad = cant,
                                            proveedor = repProveedor,
                                            costo = unitCosto,
                                            garantiaMeses = repGarantiaMeses.toIntOrNull() ?: 0,
                                            garantiaKm = repGarantiaKm.toIntOrNull() ?: 0,
                                            fechaInstalacion = fechaEjecucion,
                                            kilometrajeInstalacion = kmRealizadoText.toIntOrNull() ?: 0
                                        )
                                    )
                                    repNombre = ""
                                    repMarca = ""
                                    repReferencia = ""
                                    repCantidadText = "1"
                                    repProveedor = ""
                                    repCostoUnitario = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Agregar Repuesto Detallado")
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "${rep.nombre} (${rep.marca}) - Ref: ${rep.referencia.ifBlank { "N/A" }}", fontWeight = FontWeight.Bold)
                            Text(text = "Cant: ${rep.cantidad} x ${currencyFormat.format(rep.costo)} = ${currencyFormat.format(rep.costo * rep.cantidad)}")
                            Text(text = "Prov: ${rep.proveedor.ifBlank { "N/A" }} • Gar: ${rep.garantiaMeses}m / ${rep.garantiaKm}km", style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { repuestosList.remove(rep) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar repuesto", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (tituloText.isNotBlank() && vehiculoSeleccionado != null) {
                            val kmRealizado = kmRealizadoText.toIntOrNull() ?: (vehiculoSeleccionado?.kilometrajeActual ?: 0)

                            mantenimientoViewModel.registrarRealizado(
                                MantenimientoRealizado(
                                    vehiculoId = vehiculoSeleccionado!!.id,
                                    titulo = tituloText,
                                    categoria = categoriaSeleccionada,
                                    tipo = tipoMantenimiento,
                                    taller = tallerText,
                                    fecha = fechaEjecucion,
                                    kilometrajeRealizado = kmRealizado,
                                    costoManoDeObra = manoDeObraVal,
                                    costoRepuestos = costoRepuestosSuma,
                                    costoTotal = costoTotalVal,
                                    observaciones = observacionesText,
                                    repuestos = repuestosList.toList()
                                )
                            )

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
