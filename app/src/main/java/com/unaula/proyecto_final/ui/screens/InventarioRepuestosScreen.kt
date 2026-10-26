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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unaula.proyecto_final.ui.theme.ColorDeepGreen
import com.unaula.proyecto_final.ui.theme.ColorTextSecondary
import com.unaula.proyecto_final.ui.viewmodel.MantenimientoViewModel
import com.unaula.proyecto_final.ui.viewmodel.VehiculoViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InventarioRepuestosScreen(
    vehiculoViewModel: VehiculoViewModel,
    mantenimientoViewModel: MantenimientoViewModel
) {
    val vehiculos by vehiculoViewModel.vehiculos.collectAsState()
    val realizados by mantenimientoViewModel.realizados.collectAsState()

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

    val repuestosInstalados = realizados.flatMap { mant ->
        val vehiculo = vehiculos.find { it.id == mant.vehiculoId }
        mant.repuestos.map { rep ->
            Triple(mant, vehiculo, rep)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.White)
            .imePadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Inventario de repuestos instalados (RF-27)",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = ColorDeepGreen
            )
            Text(
                text = "Trazabilidad de componentes y garantías activas en tu flota",
                style = MaterialTheme.typography.bodyMedium,
                color = ColorTextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (repuestosInstalados.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(modifier = Modifier.padding(24.dp)) {
                        Text("No hay repuestos registrados en el historial de mantenimiento.")
                    }
                }
            }
        } else {
            items(repuestosInstalados) { (mant, vehiculo, rep) ->
                val fechaInstalacionStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(rep.fechaInstalacion))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${rep.nombre} (${rep.marca})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ColorDeepGreen
                            )
                            Text(
                                text = currencyFormat.format(rep.costo * rep.cantidad),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Vehículo: ${vehiculo?.marca ?: "N/A"} ${vehiculo?.modelo ?: ""} (${vehiculo?.placa ?: "—"})",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Referencia: ${rep.referencia.ifBlank { "N/A" }} • Cantidad: ${rep.cantidad}",
                            style = MaterialTheme.typography.bodySmall,
                            color = ColorTextSecondary
                        )
                        Text(
                            text = "Proveedor: ${rep.proveedor.ifBlank { "N/A" }} • Instalado: $fechaInstalacionStr",
                            style = MaterialTheme.typography.bodySmall,
                            color = ColorTextSecondary
                        )
                        Text(
                            text = "Garantía: ${rep.garantiaMeses} meses / ${rep.garantiaKm} km (Mantenimiento: ${mant.titulo})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
