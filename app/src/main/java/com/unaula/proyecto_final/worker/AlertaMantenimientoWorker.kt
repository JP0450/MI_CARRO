package com.unaula.proyecto_final.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.unaula.proyecto_final.data.MantenimientoRepository
import com.unaula.proyecto_final.data.VehiculoRepository
import com.unaula.proyecto_final.model.EstadoMantenimiento

class AlertaMantenimientoWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val vehiculoRepo = VehiculoRepository(applicationContext)
        val mantenimientoRepo = MantenimientoRepository(applicationContext)

        val vehiculos = vehiculoRepo.vehiculos.value
        val mantenimientos = mantenimientoRepo.programados.value

        var notificationCounter = 1000

        vehiculos.forEach { vehiculo ->
            // Alerta de documentación (SOAT / Tecno-Mecánica)
            if (vehiculo.documentacionVencidaOFaltante) {
                NotificationHelper.enviarNotificacion(
                    applicationContext,
                    "⛔ Alerta Documentación: ${vehiculo.placa}",
                    "El vehículo ${vehiculo.placa} no puede circular por falta de documentación (SOAT/Técnico-Mecánica).",
                    notificationCounter++
                )
            }

            mantenimientoRepo.evaluarEstadosMantenimientos(vehiculo.id, vehiculo.kilometrajeActual)
            val delVehiculo = mantenimientos.filter { it.vehiculoId == vehiculo.id }

            delVehiculo.forEach { prog ->
                when (prog.estado) {
                    EstadoMantenimiento.VENCIDO -> {
                        NotificationHelper.enviarNotificacion(
                            applicationContext,
                            "⚠️ Mantenimiento Vencido: ${vehiculo.placa}",
                            "El servicio '${prog.titulo}' está vencido. Por favor regístralo.",
                            notificationCounter++
                        )
                    }
                    EstadoMantenimiento.PROXIMO -> {
                        NotificationHelper.enviarNotificacion(
                            applicationContext,
                            "🕒 Mantenimiento Próximo: ${vehiculo.placa}",
                            "El servicio '${prog.titulo}' está próximo a vencer.",
                            notificationCounter++
                        )
                    }
                    else -> {}
                }
            }
        }

        return Result.success()
    }
}
