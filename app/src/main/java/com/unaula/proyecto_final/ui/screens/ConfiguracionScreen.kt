package com.unaula.proyecto_final.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.unaula.proyecto_final.ui.theme.ColorDeepGreen
import com.unaula.proyecto_final.ui.theme.ColorTextSecondary
import com.unaula.proyecto_final.ui.viewmodel.ConfiguracionViewModel

@Composable
fun ConfiguracionScreen(
    configuracionViewModel: ConfiguracionViewModel
) {
    val context = LocalContext.current
    val notificacionesActivas by configuracionViewModel.notificacionesActivas.collectAsState()
    val anticipacionKm by configuracionViewModel.anticipacionKm.collectAsState()
    val anticipacionDias by configuracionViewModel.anticipacionDias.collectAsState()

    var showResetDialog1 by remember { mutableStateOf(false) }
    var showResetDialog2 by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(com.unaula.proyecto_final.ui.theme.ColorBlushPink)
            .imePadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "CONFIGURACIÓN",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = ColorTextSecondary
            )
            Text(
                text = "Configuración",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = ColorDeepGreen
            )
            Text(
                text = "Ajusta los avisos y umbrales de tu flota.",
                style = MaterialTheme.typography.bodyMedium,
                color = ColorTextSecondary
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Activar Alertas y Notificaciones",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Permitir generación de avisos de mantenimientos y documentos próximos o vencidos",
                        style = MaterialTheme.typography.bodySmall,
                        color = ColorTextSecondary
                    )
                }
                Switch(
                    checked = notificacionesActivas,
                    onCheckedChange = { configuracionViewModel.setNotificacionesActivas(it) }
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Umbrales Globales de Anticipación",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Define con cuánta antelación deseas ser advertido por defecto.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ColorTextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = anticipacionKm.toString(),
                    onValueChange = {
                        it.toIntOrNull()?.let { km -> configuracionViewModel.setAnticipacionKm(km) }
                    },
                    label = { Text("Anticipación Predeterminada (KM)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = anticipacionDias.toString(),
                    onValueChange = {
                        it.toIntOrNull()?.let { dias -> configuracionViewModel.setAnticipacionDias(dias) }
                    },
                    label = { Text("Anticipación Predeterminada (Días)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Zona de Peligro - RF-40 (Factory Reset)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Zona de Peligro - Borrado Total (RF-40)",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = "Elimina definitivamente toda la configuración, datos y preferencias locales de la aplicación.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { showResetDialog1 = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Restauración de Fábrica / Borrar Todo")
                }
            }
        }
    }

    // Diálogo de Confirmación 1
    if (showResetDialog1) {
        AlertDialog(
            onDismissRequest = { showResetDialog1 = false },
            title = { Text("⚠️ ¿Estás seguro?") },
            text = { Text("Esta acción eliminará todas las preferencias y configuraciones guardadas. ¿Deseas continuar?") },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog1 = false
                        showResetDialog2 = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Sí, continuar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog1 = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de Doble Confirmación 2
    if (showResetDialog2) {
        AlertDialog(
            onDismissRequest = { showResetDialog2 = false },
            title = { Text("🚨 ADVERTENCIA FINAL") },
            text = { Text("Esta acción es irreversible y purgará por completo todos los datos locales de MiCarro. ¿Confirmas la eliminación definitiva?") },
            confirmButton = {
                Button(
                    onClick = {
                        configuracionViewModel.factoryReset {
                            Toast.makeText(context, "Restauración de fábrica completada", Toast.LENGTH_LONG).show()
                        }
                        showResetDialog2 = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("CONFIRMAR BORRADO TOTAL")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog2 = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
