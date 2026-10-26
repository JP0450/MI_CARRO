package com.unaula.proyecto_final.ui.screens

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unaula.proyecto_final.model.EstadoMantenimiento
import com.unaula.proyecto_final.model.Vehiculo
import com.unaula.proyecto_final.ui.theme.ColorAzureBlue
import com.unaula.proyecto_final.ui.theme.ColorBurgundy
import com.unaula.proyecto_final.ui.theme.ColorBorder
import com.unaula.proyecto_final.ui.theme.ColorBorderMint
import com.unaula.proyecto_final.ui.theme.ColorDeepBlue
import com.unaula.proyecto_final.ui.theme.ColorDeepGreen
import com.unaula.proyecto_final.ui.theme.ColorMintAqua
import com.unaula.proyecto_final.ui.theme.ColorNeonGreen
import com.unaula.proyecto_final.ui.theme.ColorTextPrimary
import com.unaula.proyecto_final.ui.theme.ColorTextSecondary
import com.unaula.proyecto_final.ui.viewmodel.MantenimientoViewModel
import com.unaula.proyecto_final.ui.viewmodel.ReportesViewModel
import com.unaula.proyecto_final.ui.viewmodel.ResumenGastos
import com.unaula.proyecto_final.ui.viewmodel.VehiculoViewModel
import java.io.IOException
import java.io.OutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun ReportesScreen(
    vehiculoViewModel: VehiculoViewModel,
    mantenimientoViewModel: MantenimientoViewModel,
    reportesViewModel: ReportesViewModel
) {
    val context = LocalContext.current
    val vehiculos by vehiculoViewModel.vehiculos.collectAsState()
    val programados by mantenimientoViewModel.programados.collectAsState()
    val realizados by mantenimientoViewModel.realizados.collectAsState()
    var selectedVehicleId by remember { mutableStateOf<String?>(null) }
    var vehicleMenuExpanded by remember { mutableStateOf(false) }
    var exportError by remember { mutableStateOf<String?>(null) }
    var showInventarioRepuestos by remember { mutableStateOf(false) }

    val selectedVehicle = vehiculos.firstOrNull { it.id == selectedVehicleId }
    val filteredVehicles = if (selectedVehicle == null) vehiculos else listOf(selectedVehicle)
    val filteredProgramados = remember(programados, selectedVehicleId) {
        if (selectedVehicleId == null) programados
        else programados.filter { it.vehiculoId == selectedVehicleId }
    }
    val filteredRealizados = remember(realizados, selectedVehicleId) {
        if (selectedVehicleId == null) realizados
        else realizados.filter { it.vehiculoId == selectedVehicleId }
    }
    val resumen = remember(filteredProgramados, filteredRealizados, selectedVehicleId) {
        reportesViewModel.obtenerResumenVehiculo(selectedVehicleId)
    }

    val currentDate = remember {
        SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("es-CO")).format(Date())
    }
    val reportDateRange = "01 ago – $currentDate"
    val completedTasks = filteredRealizados.size
    val totalTasks = completedTasks + filteredProgramados.size
    val completion = if (totalTasks == 0) 0f else completedTasks.toFloat() / totalTasks
    val dueSoon = filteredProgramados.count { it.estado == EstadoMantenimiento.PROXIMO }
    val overdue = filteredProgramados.count { it.estado == EstadoMantenimiento.VENCIDO }
    val enabledVehicles = filteredVehicles.filterNot { it.documentacionVencidaOFaltante }
    val blockedVehicle = filteredVehicles.firstOrNull { it.documentacionVencidaOFaltante }
    val currency = remember {
        NumberFormat.getNumberInstance(Locale.forLanguageTag("es-CO"))
    }
    val totalPreventive = resumen.costoPreventivos + resumen.costoCorrectivos
    val preventiveShare = if (totalPreventive <= 0.0) 0f
    else (resumen.costoPreventivos / totalPreventive).toFloat().coerceIn(0f, 1f)

    val pdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) {
            try {
                val output = context.contentResolver.openOutputStream(uri)
                    ?: throw IOException("Android no pudo abrir el archivo de destino.")
                output.use {
                    writeReportPdf(
                        output = it,
                        dateRange = reportDateRange,
                        summary = resumen,
                        enabledCount = enabledVehicles.size,
                        vehicleCount = filteredVehicles.size,
                        completedTasks = completedTasks,
                        totalTasks = totalTasks
                    )
                }
                Toast.makeText(context, "Reporte PDF guardado", Toast.LENGTH_SHORT).show()
            } catch (exception: IOException) {
                exportError = exception.localizedMessage
                    ?: "No se pudo guardar el reporte PDF."
            }
        }
    }

    LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 14.dp,
                top = 20.dp,
                end = 14.dp,
                bottom = 22.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text(
                        text = "Reportes y KPIs",
                        color = ColorDeepGreen,
                        fontSize = 27.sp,
                        lineHeight = 31.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Decisiones claras para una flota más eficiente.",
                        color = ColorTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                }
            }

            item {
                Box {
                    VehicleSelector(
                        vehicle = selectedVehicle,
                        totalVehicles = vehiculos.size,
                        onClick = { vehicleMenuExpanded = true }
                    )
                    DropdownMenu(
                        expanded = vehicleMenuExpanded,
                        onDismissRequest = { vehicleMenuExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todos los vehículos") },
                            onClick = {
                                selectedVehicleId = null
                                vehicleMenuExpanded = false
                            }
                        )
                        vehiculos.forEach { vehicle ->
                            DropdownMenuItem(
                                text = {
                                    Text("${vehicle.marca} ${vehicle.modelo} (${vehicle.placa})")
                                },
                                onClick = {
                                    selectedVehicleId = vehicle.id
                                    vehicleMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                AnalysisPeriod(dateRange = reportDateRange)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.AccountBalanceWallet,
                        title = "Gasto total (COP)",
                        value = "$${currency.format(resumen.costoTotal.roundToInt())}",
                        subtitle = "${resumen.totalMantenimientos} servicios realizados"
                    )
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Inventory2,
                        title = "Repuestos instalados",
                        value = resumen.totalRepuestos.toString().padStart(2, '0'),
                        subtitle = "En ${resumen.totalMantenimientos} mantenimientos"
                    )
                }
            }

            item {
                Button(
                    onClick = { showInventarioRepuestos = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorDeepGreen)
                ) {
                    Icon(Icons.Default.Inventory2, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ver Inventario Consolidado de Repuestos (RF-27)")
                }
            }

            item {
                SpendingBreakdown(
                    summary = resumen,
                    currency = currency,
                    preventiveShare = preventiveShare
                )
            }

            item {
                FleetPlanCard(
                    completedTasks = completedTasks,
                    totalTasks = totalTasks,
                    completion = completion,
                    dueSoon = dueSoon,
                    overdue = overdue
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Disponibilidad de flota",
                        color = ColorDeepGreen,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${filteredVehicles.size} vehículos",
                        color = ColorTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            item {
                FleetAvailabilityCard(
                    enabledVehicles = enabledVehicles,
                    blockedVehicle = blockedVehicle,
                    totalVehicles = filteredVehicles.size
                )
            }

            item {
                Button(
                    onClick = { pdfLauncher.launch("Reporte_MI_CARRO.pdf") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorAzureBlue,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier = Modifier.width(9.dp))
                    Text(
                        text = "Descargar reporte PDF",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            item {
                Text(
                    text = "Actualizado el $currentDate · Moneda: COP",
                    color = ColorTextSecondary,
                    fontSize = 10.sp,
                    lineHeight = 15.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

    exportError?.let { message ->
        AlertDialog(
            onDismissRequest = { exportError = null },
            title = { Text("No se pudo exportar el reporte") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { exportError = null }) {
                    Text("Entendido")
                }
            }
        )
    }

    if (showInventarioRepuestos) {
        AlertDialog(
            onDismissRequest = { showInventarioRepuestos = false },
            title = { Text("Inventario Consolidado de Repuestos (RF-27)") },
            text = {
                Box(modifier = Modifier.height(450.dp)) {
                    InventarioRepuestosScreen(
                        vehiculoViewModel = vehiculoViewModel,
                        mantenimientoViewModel = mantenimientoViewModel
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showInventarioRepuestos = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
private fun VehicleSelector(
    vehicle: Vehiculo?,
    totalVehicles: Int,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ColorBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ColorMintAqua),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.DirectionsCar,
                    contentDescription = null,
                    tint = ColorDeepGreen,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "FLOTA CORPORATIVA",
                    color = ColorTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
                Text(
                    text = vehicle?.let { "${it.marca} ${it.modelo} · ${it.placa}" }
                        ?: "Todos los vehículos · $totalVehicles",
                    color = ColorTextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = "Seleccionar vehículo",
                tint = ColorDeepGreen
            )
        }
    }
}

@Composable
private fun AnalysisPeriod(dateRange: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ColorBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = ColorAzureBlue,
                modifier = Modifier.size(17.dp)
            )
            Text(
                text = dateRange,
                modifier = Modifier.weight(1f),
                color = ColorTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = "Cambiar período",
                tint = ColorTextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    subtitle: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ColorBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(icon, contentDescription = null, tint = ColorDeepGreen, modifier = Modifier.size(20.dp))
            Text(
                text = title,
                color = ColorTextSecondary,
                fontSize = 12.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = value,
                color = ColorDeepGreen,
                fontSize = 25.sp,
                lineHeight = 29.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false
            )
            Text(
                text = subtitle,
                color = ColorTextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
private fun SpendingBreakdown(
    summary: ResumenGastos,
    currency: NumberFormat,
    preventiveShare: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ColorBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Preventivo vs. correctivo",
                color = ColorDeepGreen,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Distribución del gasto de mantenimiento",
                color = ColorTextSecondary,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "${(preventiveShare * 100).roundToInt()}%",
                    color = ColorDeepGreen,
                    fontSize = 32.sp,
                    lineHeight = 38.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${((1f - preventiveShare) * 100).roundToInt()}%",
                    color = ColorDeepBlue,
                    fontSize = 23.sp,
                    lineHeight = 29.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(22.dp)
                    .clip(CircleShape)
                    .background(ColorAzureBlue),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                if (preventiveShare > 0f) {
                    Box(
                        modifier = Modifier
                            .weight(preventiveShare)
                            .fillMaxHeight()
                            .background(ColorBurgundy)
                    )
                }
                if (preventiveShare < 1f) {
                    Box(
                        modifier = Modifier
                            .weight(1f - preventiveShare)
                            .fillMaxHeight()
                            .background(ColorAzureBlue)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SpendingLegend(
                    modifier = Modifier.weight(1f),
                    title = "Preventivo",
                    amount = "$${currency.format(summary.costoPreventivos.roundToInt())}",
                    color = ColorDeepGreen
                )
                SpendingLegend(
                    modifier = Modifier.weight(1f),
                    title = "Correctivo",
                    amount = "$${currency.format(summary.costoCorrectivos.roundToInt())}",
                    color = ColorAzureBlue
                )
            }
        }
    }
}

@Composable
private fun SpendingLegend(
    modifier: Modifier,
    title: String,
    amount: String,
    color: Color
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = title, color = ColorTextSecondary, fontSize = 11.sp)
        }
        Text(
            text = amount,
            color = ColorTextPrimary,
            fontSize = 15.sp,
            lineHeight = 19.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun FleetPlanCard(
    completedTasks: Int,
    totalTasks: Int,
    completion: Float,
    dueSoon: Int,
    overdue: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ColorBurgundy)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Plan de flota",
                color = ColorDeepGreen,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = ColorDeepGreen,
                modifier = Modifier.size(22.dp)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    text = "$completedTasks de $totalTasks",
                    color = ColorDeepGreen,
                    fontSize = 32.sp,
                    lineHeight = 38.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "tareas completadas",
                    color = ColorTextSecondary,
                    fontSize = 12.sp
                )
            }
            Text(
                text = "${(completion * 100).roundToInt()}%",
                color = ColorDeepGreen,
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(CircleShape)
                .background(ColorDeepGreen.copy(alpha = 0.18f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(completion)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(ColorDeepGreen)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (dueSoon > 0) {
                StatusChip(
                    label = "$dueSoon próximo${if (dueSoon == 1) "" else "s"}",
                    container = Color(0xFFFFF0D5),
                    content = Color(0xFF925400),
                    iconColor = Color(0xFFE8A000)
                )
            }
            if (overdue > 0) {
                StatusChip(
                    label = "$overdue vencido${if (overdue == 1) "" else "s"}",
                    container = Color(0xFFFCE7EB),
                    content = Color(0xFFB52E43),
                    iconColor = Color(0xFFB52E43)
                )
            }
        }
    }
}

@Composable
private fun StatusChip(
    label: String,
    container: Color,
    content: Color,
    iconColor: Color
) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(container)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(iconColor)
        )
        Text(label, color = content, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun FleetAvailabilityCard(
    enabledVehicles: List<Vehiculo>,
    blockedVehicle: Vehiculo?,
    totalVehicles: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColorMintAqua),
        border = BorderStroke(1.dp, ColorBorderMint),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFF7FBF79),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "${enabledVehicles.size} de $totalVehicles habilitados",
                        color = ColorDeepGreen,
                        fontSize = 20.sp,
                        lineHeight = 24.sp
                    )
                    Text(
                        text = "Documentación vigente para circular",
                        color = ColorDeepGreen,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                enabledVehicles.forEach { vehicle ->
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFFE9F9E5))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(ColorNeonGreen)
                        )
                        Text(
                            text = vehicle.placa,
                            color = ColorDeepGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            blockedVehicle?.let { vehicle ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        tint = Color(0xFFB52E43),
                        modifier = Modifier.size(18.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "${vehicle.placa} · No puede circular",
                            color = Color(0xFFB52E43),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                        Text(
                            text = documentationIssue(vehicle),
                            color = ColorTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            if (totalVehicles == 0) {
                Text(
                    text = "Registra un vehículo para consultar la documentación de la flota.",
                    color = ColorDeepGreen,
                    fontSize = 12.sp
                )
            }
        }
    }
}

private fun documentationIssue(vehicle: Vehiculo): String {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("es-CO"))
    val now = System.currentTimeMillis()
    return when {
        vehicle.fechaSeguroSoat == null -> "SOAT sin registrar"
        vehicle.fechaSeguroSoat < now ->
            "SOAT vencido desde el ${dateFormat.format(Date(vehicle.fechaSeguroSoat))}"
        vehicle.fechaTecnoMecanica == null -> "Técnico-mecánica sin registrar"
        vehicle.fechaTecnoMecanica < now ->
            "Técnico-mecánica vencida desde el ${dateFormat.format(Date(vehicle.fechaTecnoMecanica))}"
        else -> "Documentación incompleta"
    }
}

private fun writeReportPdf(
    output: OutputStream,
    dateRange: String,
    summary: ResumenGastos,
    enabledCount: Int,
    vehicleCount: Int,
    completedTasks: Int,
    totalTasks: Int
) {
    val document = PdfDocument()
    try {
        val page = document.startPage(
            PdfDocument.PageInfo.Builder(612, 792, 1).create()
        )
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(45, 35, 48)
            textSize = 22f
            typeface = android.graphics.Typeface.create(
                "sans-serif",
                android.graphics.Typeface.BOLD
            )
        }
        val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.rgb(45, 35, 48)
            textSize = 13f
        }
        val lines = listOf(
            "Periodo: $dateRange",
            "Gasto total: ${summary.costoTotal.toLong()} COP",
            "Gasto preventivo: ${summary.costoPreventivos.toLong()} COP",
            "Gasto correctivo: ${summary.costoCorrectivos.toLong()} COP",
            "Servicios realizados: ${summary.totalMantenimientos}",
            "Repuestos instalados: ${summary.totalRepuestos}",
            "Plan de flota: $completedTasks de $totalTasks tareas completadas",
            "Vehiculos habilitados: $enabledCount de $vehicleCount",
            "Mantenimientos proximos: ${summary.mantenimientosProximos}",
            "Mantenimientos vencidos: ${summary.mantenimientosVencidos}"
        )
        page.canvas.drawText("MI CARRO - Reportes y KPIs", 48f, 58f, titlePaint)
        var y = 96f
        lines.forEach { line ->
            page.canvas.drawText(line, 48f, y, bodyPaint)
            y += 26f
        }
        document.finishPage(page)
        document.writeTo(output)
    } finally {
        document.close()
    }
}
