package com.unaula.proyecto_final.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unaula.proyecto_final.data.VehiculoRepository
import com.unaula.proyecto_final.model.HistorialKilometraje
import com.unaula.proyecto_final.model.Vehiculo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VehiculoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VehiculoRepository(application)

    val vehiculos: StateFlow<List<Vehiculo>> = repository.vehiculos
    val historialKm: StateFlow<List<HistorialKilometraje>> = repository.historialKm

    private val _vehiculoSeleccionado = MutableStateFlow<Vehiculo?>(null)
    val vehiculoSeleccionado: StateFlow<Vehiculo?> = _vehiculoSeleccionado.asStateFlow()

    init {
        viewModelScope.launch {
            vehiculos.collect { list ->
                val activeList = list.filter { !it.archivado }
                val principal = activeList.find { it.esPrincipal } ?: activeList.firstOrNull()
                if (_vehiculoSeleccionado.value == null && principal != null) {
                    _vehiculoSeleccionado.value = principal
                } else if (_vehiculoSeleccionado.value != null) {
                    val updated = activeList.find { it.id == _vehiculoSeleccionado.value?.id }
                    if (updated != null) {
                        _vehiculoSeleccionado.value = updated
                    } else if (principal != null) {
                        _vehiculoSeleccionado.value = principal
                    }
                } else if (principal != null) {
                    _vehiculoSeleccionado.value = principal
                }
            }
        }
    }

    fun seleccionarVehiculo(vehiculo: Vehiculo) {
        _vehiculoSeleccionado.value = vehiculo
    }

    fun guardarVehiculo(vehiculo: Vehiculo, onResult: (Boolean, String) -> Unit) {
        val success = repository.guardarVehiculo(vehiculo)
        if (success) {
            onResult(true, "")
        } else {
            onResult(
                false,
                "Ya existe un vehículo registrado con la placa ${vehiculo.placa.uppercase()}. Las placas son únicas (no distinguen mayúsculas/minúsculas)."
            )
        }
    }

    fun asignarVehiculoPrincipal(vehiculoId: String) {
        repository.asignarVehiculoPrincipal(vehiculoId)
        val principal = vehiculos.value.find { it.id == vehiculoId }
        if (principal != null) {
            _vehiculoSeleccionado.value = principal
        }
    }

    fun archivarVehiculo(vehiculoId: String, archivado: Boolean) {
        repository.archivarVehiculo(vehiculoId, archivado)
        if (archivado && _vehiculoSeleccionado.value?.id == vehiculoId) {
            _vehiculoSeleccionado.value = vehiculos.value.firstOrNull { !it.archivado }
        }
    }

    fun actualizarKilometraje(vehiculoId: String, nuevoKm: Int, nota: String = "Actualización manual") {
        repository.actualizarKilometraje(vehiculoId, nuevoKm, nota)
    }

    fun eliminarVehiculo(vehiculoId: String) {
        repository.eliminarVehiculo(vehiculoId)
        if (_vehiculoSeleccionado.value?.id == vehiculoId) {
            _vehiculoSeleccionado.value = vehiculos.value.firstOrNull { !it.archivado }
        }
    }
}
