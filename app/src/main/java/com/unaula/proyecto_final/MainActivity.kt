
package com.unaula.proyecto_final

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.unaula.proyecto_final.ui.navigation.AppNavigation
import com.unaula.proyecto_final.ui.theme.PROYECTO_FINALTheme
import com.unaula.proyecto_final.worker.AlertaMantenimientoWorker
import com.unaula.proyecto_final.worker.NotificationHelper
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar canal de notificaciones y programar Worker de alertas diarias
        NotificationHelper.crearCanalNotificacion(this)
        iniciarTrabajoDeAlertas()
        solicitarPermisoNotificaciones()

        enableEdgeToEdge()
        setContent {
            PROYECTO_FINALTheme {
                AppNavigation()
            }
        }
    }

    private fun iniciarTrabajoDeAlertas() {
        val alertaWorkRequest = PeriodicWorkRequestBuilder<AlertaMantenimientoWorker>(
            1, TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "AlertaMantenimientoWork",
            ExistingPeriodicWorkPolicy.KEEP,
            alertaWorkRequest
        )
    }

    private fun solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }
}
