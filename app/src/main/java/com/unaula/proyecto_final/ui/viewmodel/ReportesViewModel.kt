package com.unaula.proyecto_final.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.unaula.proyecto_final.data.MantenimientoRepository
import com.unaula.proyecto_final.model.EstadoMantenimiento
import com.unaula.proyecto_final.model.MantenimientoRealizado
import com.unaula.proyecto_final.model.TipoMantenimiento

data class ResumenGastos(
    val costoTotal: Double = 0.0,
    val costoPreventivos: Double = 0.0,
    val costoCorrectivos: Double = 0.0,
    val totalMantenimientos: Int = 0,
    val totalRepuestos: Int = 0,
    val mantenimientosAlDia: Int = 0,
    val mantenimientosProximos: Int = 0,
    val mantenimientosVencidos: Int = 0
)

class ReportesViewModel(application: Application) : AndroidViewModel(application) {

    private val mantenimientoRepo = MantenimientoRepository(application)

    fun obtenerResumenVehiculo(vehiculoId: String?): ResumenGastos {
        val realizados = if (vehiculoId.isNullOrBlank()) {
            mantenimientoRepo.realizados.value
        } else {
            mantenimientoRepo.realizados.value.filter { it.vehiculoId == vehiculoId }
        }

        val programados = if (vehiculoId.isNullOrBlank()) {
            mantenimientoRepo.programados.value
        } else {
            mantenimientoRepo.programados.value.filter { it.vehiculoId == vehiculoId }
        }

        val total = realizados.sumOf { it.costoTotal }
        val prev = realizados.filter { it.tipo == TipoMantenimiento.PREVENTIVO }.sumOf { it.costoTotal }
        val corr = realizados.filter { it.tipo == TipoMantenimiento.CORRECTIVO }.sumOf { it.costoTotal }
        val totalReps = realizados.flatMap { it.repuestos }.size

        val alDia = programados.count { it.estado == EstadoMantenimiento.AL_DIA }
        val prox = programados.count { it.estado == EstadoMantenimiento.PROXIMO }
        val venc = programados.count { it.estado == EstadoMantenimiento.VENCIDO }

        return ResumenGastos(
            costoTotal = total,
            costoPreventivos = prev,
            costoCorrectivos = corr,
            totalMantenimientos = realizados.size,
            totalRepuestos = totalReps,
            mantenimientosAlDia = alDia,
            mantenimientosProximos = prox,
            mantenimientosVencidos = venc
        )
    }
}
