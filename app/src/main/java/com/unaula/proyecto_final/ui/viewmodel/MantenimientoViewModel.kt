package com.unaula.proyecto_final.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.unaula.proyecto_final.data.MantenimientoRepository
import com.unaula.proyecto_final.model.CategoriaMantenimiento
import com.unaula.proyecto_final.model.MantenimientoProgramado
import com.unaula.proyecto_final.model.MantenimientoRealizado
import com.unaula.proyecto_final.model.Repuesto
import kotlinx.coroutines.flow.StateFlow

class MantenimientoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MantenimientoRepository(application)

    val programados: StateFlow<List<MantenimientoProgramado>> = repository.programados
    val realizados: StateFlow<List<MantenimientoRealizado>> = repository.realizados
    val categorias: StateFlow<List<CategoriaMantenimiento>> = repository.categorias

    fun guardarProgramado(mantenimiento: MantenimientoProgramado) {
        repository.guardarProgramado(mantenimiento)
    }

    fun registrarRealizado(realizado: MantenimientoRealizado) {
        repository.registrarMantenimientoRealizado(realizado)
    }

    fun evaluarAlertas(vehiculoId: String, kmActual: Int) {
        repository.evaluarEstadosMantenimientos(vehiculoId, kmActual)
    }

    fun posponerAlerta(mantenimientoId: String) {
        repository.posponerAlerta(mantenimientoId)
    }

    fun obtenerRepuestosDeVehiculo(vehiculoId: String): List<Repuesto> {
        val realizs = realizados.value.filter { it.vehiculoId == vehiculoId }
        return realizs.flatMap { it.repuestos }
    }
}
