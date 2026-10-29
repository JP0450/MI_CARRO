package com.unaula.proyecto_final.ui.screens

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unaula.proyecto_final.data.CatalogoVehiculos
import com.unaula.proyecto_final.model.TipoCombustible
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
    val historialKmList by vehiculoViewModel.historialKm.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var vehiculoAEditar by remember { mutableStateOf<Vehiculo?>(null) }
    var vehiculoHistorialId by remember { mutableStateOf<String?>(null) }
    var mostrarArchivados by remember { mutableStateOf(false) }

    val isEditing = vehiculoAEditar != null

    val vehiculosList = remember(vehiculos, mostrarArchivados) {
        vehiculos.filter { if (mostrarArchivados) it.archivado else !it.archivado }
    }

    // Campos del formulario
    var placaText by remember { mutableStateOf("") }
    var marcaText by remember { mutableStateOf("") }
    var modeloText by remember { mutableStateOf("") }
    var vinText by remember { mutableStateOf("") }
    var cilindrajeText by remember { mutableStateOf("") }
    var combustibleSeleccionado by remember { mutableStateOf(TipoCombustible.GASOLINA) }

    val coloresComunes = listOf("Blanco", "Negro", "Gris", "Plata", "Rojo", "Azul", "Azul Oscuro", "Verde", "Amarillo", "Otro / Personalizado")
    var colorSeleccionado by remember { mutableStateOf("Blanco") }
    var esColorPersonalizado by remember { mutableStateOf(false) }
    var colorPersonalizadoText by remember { mutableStateOf("") }

    var esMarcaManual by remember { mutableStateOf(false) }
    var esModeloManual by remember { mutableStateOf(false) }

    var anioText by remember { mutableStateOf("2023") }
    var kmText by remember { mutableStateOf("0") }
    var fechaSoat by remember { mutableStateOf<Long?>(null) }
    var fechaTecno by remember { mutableStateOf<Long?>(null) }
    var tipoVehiculoSeleccionado by remember { mutableStateOf(TipoVehiculo.AUTOMOVIL) }
    var dialogErrorMessage by remember { mutableStateOf("") }

    var tipoDropdownExpanded by remember { mutableStateOf(false) }
    var marcaDropdownExpanded by remember { mutableStateOf(false) }
    var modeloDropdownExpanded by remember { mutableStateOf(false) }
    var combustibleDropdownExpanded by remember { mutableStateOf(false) }
    var colorDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = com.unaula.proyecto_final.ui.theme.ColorBlushPink,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    vehiculoAEditar = null
                    placaText = ""
                    marcaText = ""
                    modeloText = ""
                    vinText = ""
                    cilindrajeText = ""
                    combustibleSeleccionado = TipoCombustible.GASOLINA
                    colorSeleccionado = "Blanco"
                    esColorPersonalizado = false
                    colorPersonalizadoText = ""
                    esMarcaManual = false
                    esModeloManual = false
                    anioText = "2023"
                    kmText = "0"
                    fechaSoat = null
                    fechaTecno = null
                    tipoVehiculoSeleccionado = TipoVehiculo.AUTOMOVIL
                    dialogErrorMessage = ""
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
                text = "VEHÍCULOS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = ColorTextSecondary
            )
            Text(
                text = "Gestión de vehículos",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = ColorDeepGreen
            )
            Text(
                text = "Administra tu inventario, documentos y vehículos archivados",
                style = MaterialTheme.typography.bodyMedium,
                color = ColorTextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Selector Activos / Archivados (RF-03)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = !mostrarArchivados,
                    onClick = { mostrarArchivados = false },
                    label = { Text("Activos (${vehiculos.count { !it.archivado }})") }
                )
                FilterChip(
                    selected = mostrarArchivados,
                    onClick = { mostrarArchivados = true },
                    label = { Text("Archivados (${vehiculos.count { it.archivado }})") }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (vehiculosList.isEmpty()) {
                Text(
                    text = if (mostrarArchivados) "No hay vehículos archivados." else "No hay vehículos registrados. Pulsa + para agregar uno.",
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
                        text = if (mostrarArchivados) "Vehículos Archivados" else "Inventario (Principal primero)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${vehiculosList.size} vehículos",
                        style = MaterialTheme.typography.labelLarge,
                        color = ColorTextSecondary
                    )
                }
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.imePadding()
                ) {
                    items(vehiculosList) { vehiculo ->
                        val hasDocError = vehiculo.documentacionVencidaOFaltante
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
                            border = BorderStroke(
                                1.dp,
                                if (vehiculo.esPrincipal) ColorDeepGreen
                                else if (hasDocError) MaterialTheme.colorScheme.error.copy(alpha = 0.55f)
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
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
                                            if (vehiculo.color.isNotBlank()) {
                                                Text(
                                                    text = "Color: ${vehiculo.color} • Combustible: ${vehiculo.tipoCombustible.label}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = ColorTextSecondary
                                                )
                                            }
                                            Text(
                                                text = "KM: ${vehiculo.kilometrajeActual} km",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = if (vehiculo.archivado) "ARCHIVADO" else if (hasDocError) "DOCUMENTACIÓN PENDIENTE" else "HABILITADO",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (vehiculo.archivado) ColorTextSecondary else if (hasDocError) MaterialTheme.colorScheme.error else androidx.compose.ui.graphics.Color(0xFF205426)
                                                )
                                                if (!vehiculo.sincronizado) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "☁️ Pendiente sync",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.tertiary
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))

                                            val soatStr = vehiculo.fechaSeguroSoat?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it)) } ?: "No ingresado"
                                            val tecnoStr = vehiculo.fechaTecnoMecanica?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it)) } ?: "No ingresado"

                                            Text(
                                                text = "SOAT: $soatStr • Tecno: $tecnoStr",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (hasDocError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Row {
                                        if (!vehiculo.archivado) {
                                            IconButton(
                                                onClick = {
                                                    vehiculoAEditar = vehiculo
                                                    placaText = vehiculo.placa
                                                    marcaText = vehiculo.marca
                                                    modeloText = vehiculo.modelo
                                                    anioText = vehiculo.anio.toString()
                                                    kmText = vehiculo.kilometrajeActual.toString()
                                                    fechaSoat = vehiculo.fechaSeguroSoat
                                                    fechaTecno = vehiculo.fechaTecnoMecanica
                                                    vinText = vehiculo.vin
                                                    cilindrajeText = vehiculo.cilindraje
                                                    combustibleSeleccionado = vehiculo.tipoCombustible
                                                    tipoVehiculoSeleccionado = vehiculo.tipo
                                                    if (coloresComunes.contains(vehiculo.color)) {
                                                        colorSeleccionado = vehiculo.color
                                                        esColorPersonalizado = false
                                                        colorPersonalizadoText = ""
                                                    } else {
                                                        colorSeleccionado = "Otro / Personalizado"
                                                        esColorPersonalizado = true
                                                        colorPersonalizadoText = vehiculo.color
                                                    }
                                                    dialogErrorMessage = ""
                                                    showAddDialog = true
                                                }
                                            ) {
                                                Icon(
                                                    Icons.Default.Edit,
                                                    contentDescription = "Editar",
                                                    tint = ColorDeepGreen
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

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (vehiculo.archivado) {
                                        OutlinedButton(
                                            onClick = { vehiculoViewModel.archivarVehiculo(vehiculo.id, false) },
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("Reactivar Vehículo")
                                        }
                                    } else {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (vehiculo.esPrincipal) {
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(ColorDeepGreen.copy(alpha = 0.15f))
                                                        .padding(vertical = 10.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.Default.Star, contentDescription = null, tint = ColorDeepGreen, modifier = Modifier.size(14.dp))
                                                        Spacer(modifier = Modifier.width(3.dp))
                                                        Text(
                                                            text = "Principal",
                                                            color = ColorDeepGreen,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 11.sp,
                                                            maxLines = 1,
                                                            softWrap = false
                                                        )
                                                    }
                                                }
                                            } else {
                                                OutlinedButton(
                                                    onClick = { vehiculoViewModel.asignarVehiculoPrincipal(vehiculo.id) },
                                                    shape = RoundedCornerShape(10.dp),
                                                    modifier = Modifier.weight(1f),
                                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                                                ) {
                                                    Text("Principal", maxLines = 1, fontSize = 11.sp, softWrap = false)
                                                }
                                            }

                                            OutlinedButton(
                                                onClick = { vehiculoViewModel.archivarVehiculo(vehiculo.id, true) },
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.weight(1f),
                                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                                            ) {
                                                Text("Archivar", maxLines = 1, fontSize = 11.sp, softWrap = false)
                                            }

                                            OutlinedButton(
                                                onClick = { vehiculoHistorialId = vehiculo.id },
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.weight(1f),
                                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                                            ) {
                                                Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(13.dp))
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text("KM", maxLines = 1, fontSize = 11.sp, softWrap = false)
                                            }
                                        }
                                    }
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
            onDismissRequest = {
                showAddDialog = false
                vehiculoAEditar = null
            },
            title = { Text(if (vehiculoAEditar == null) "Registrar Nuevo Vehículo" else "Editar Documentación / Color / KM") },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                        .padding(vertical = 8.dp)
                ) {
                    if (dialogErrorMessage.isNotBlank()) {
                        Text(
                            text = dialogErrorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (isEditing) {
                        Text(
                            text = "ℹ️ Las especificaciones de fábrica (Placa, Marca, Modelo, Año, VIN, Cilindraje) están bloqueadas. Puedes actualizar SOAT, Tecno-Mecánica, Color y Kilometraje.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ColorTextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (!isEditing) {
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
                    } else {
                        OutlinedTextField(
                            value = tipoVehiculoSeleccionado.label,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipo de Vehículo") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    OutlinedTextField(
                        value = placaText,
                        onValueChange = { if (!isEditing) placaText = it.uppercase().filter { char -> char.isLetterOrDigit() }.take(6) },
                        readOnly = isEditing,
                        label = { Text("Placa") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (!isEditing) {
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
                    } else {
                        OutlinedTextField(
                            value = marcaText,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Marca") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (!isEditing) {
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
                    } else {
                        OutlinedTextField(
                            value = modeloText,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Modelo") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = anioText,
                            onValueChange = { if (!isEditing) anioText = it },
                            readOnly = isEditing,
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

                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = colorDropdownExpanded,
                        onExpandedChange = { colorDropdownExpanded = !colorDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = if (esColorPersonalizado) "Otro / Personalizado" else colorSeleccionado,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Color Principal / Secundario") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = colorDropdownExpanded) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = colorDropdownExpanded,
                            onDismissRequest = { colorDropdownExpanded = false }
                        ) {
                            coloresComunes.forEach { col ->
                                DropdownMenuItem(
                                    text = { Text(col) },
                                    onClick = {
                                        if (col == "Otro / Personalizado") {
                                            esColorPersonalizado = true
                                            colorSeleccionado = col
                                        } else {
                                            esColorPersonalizado = false
                                            colorSeleccionado = col
                                            colorPersonalizadoText = ""
                                        }
                                        colorDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (esColorPersonalizado) {
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = colorPersonalizadoText,
                            onValueChange = { colorPersonalizadoText = it },
                            label = { Text("Escribe el color (ej. Blanco con Negro)") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = cilindrajeText,
                            onValueChange = { if (!isEditing) cilindrajeText = it },
                            readOnly = isEditing,
                            label = { Text("Cilindraje") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                        if (!isEditing) {
                            ExposedDropdownMenuBox(
                                expanded = combustibleDropdownExpanded,
                                onExpandedChange = { combustibleDropdownExpanded = !combustibleDropdownExpanded },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = combustibleSeleccionado.label,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Combustible") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = combustibleDropdownExpanded) },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.menuAnchor().fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = combustibleDropdownExpanded,
                                    onDismissRequest = { combustibleDropdownExpanded = false }
                                ) {
                                    TipoCombustible.entries.forEach { comb ->
                                        DropdownMenuItem(
                                            text = { Text(comb.label) },
                                            onClick = {
                                                combustibleSeleccionado = comb
                                                combustibleDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        } else {
                            OutlinedTextField(
                                value = combustibleSeleccionado.label,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Combustible") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = vinText,
                        onValueChange = { if (!isEditing) vinText = it },
                        readOnly = isEditing,
                        label = { Text("Número VIN / Chasis") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Fechas de Documentación Obligatoria (SOAT y Tecno-Mecánica):",
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
                        val anioInt = anioText.toIntOrNull()
                        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                        if (anioInt == null || anioInt !in 1900..(currentYear + 1)) {
                            dialogErrorMessage = "El año del vehículo debe ser un valor válido entre 1900 y ${currentYear + 1}."
                            return@Button
                        }

                        val cleanPlaca = placaText.uppercase().filter { it.isLetterOrDigit() }
                        val placaRegex = Regex("^[A-Z]{3}[0-9]{3}$")
                        if (!placaRegex.matches(cleanPlaca)) {
                            dialogErrorMessage = "La placa debe tener exactamente 3 letras seguidas de 3 números (Ej. ABC123 sin guion)."
                            return@Button
                        }
                        val placaFormateada = "${cleanPlaca.substring(0, 3)}-${cleanPlaca.substring(3, 6)}"

                        val colorFinal = if (esColorPersonalizado) colorPersonalizadoText.ifBlank { "Personalizado" } else colorSeleccionado

                        if (marcaText.isNotBlank()) {
                            vehiculoViewModel.guardarVehiculo(
                                Vehiculo(
                                    id = vehiculoAEditar?.id ?: "",
                                    placa = placaFormateada,
                                    marca = marcaText,
                                    modelo = modeloText,
                                    anio = anioInt,
                                    tipo = tipoVehiculoSeleccionado,
                                    kilometrajeActual = kmText.toIntOrNull() ?: 0,
                                    fechaSeguroSoat = fechaSoat,
                                    fechaTecnoMecanica = fechaTecno,
                                    color = colorFinal,
                                    vin = vinText,
                                    tipoCombustible = combustibleSeleccionado,
                                    cilindraje = cilindrajeText,
                                    esPrincipal = vehiculoAEditar?.esPrincipal ?: (vehiculos.isEmpty()),
                                    archivado = vehiculoAEditar?.archivado ?: false
                                )
                            ) { success, errorMsg ->
                                if (success) {
                                    showAddDialog = false
                                    vehiculoAEditar = null
                                    dialogErrorMessage = ""
                                } else {
                                    dialogErrorMessage = errorMsg
                                }
                            }
                        } else {
                            dialogErrorMessage = "Debes ingresar al menos la placa y la marca."
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (vehiculoAEditar == null) "Guardar" else "Actualizar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    vehiculoAEditar = null
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (vehiculoHistorialId != null) {
        val v = vehiculos.find { it.id == vehiculoHistorialId }
        val registros = historialKmList.filter { it.vehiculoId == vehiculoHistorialId }.sortedByDescending { it.fecha }
        AlertDialog(
            onDismissRequest = { vehiculoHistorialId = null },
            title = { Text("Historial de Kilometraje - ${v?.placa ?: ""}") },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (registros.isEmpty()) {
                        Text("No hay registros de kilometraje.")
                    } else {
                        registros.forEach { reg ->
                            val fechaStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(reg.fecha))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "${reg.kilometraje} km",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = "Fecha: $fechaStr",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    if (reg.nota.isNotBlank()) {
                                        Text(
                                            text = "Nota: ${reg.nota}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = ColorTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { vehiculoHistorialId = null }) {
                    Text("Cerrar")
                }
            }
        )
    }
}
