package com.unaula.proyecto_final.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unaula.proyecto_final.data.VehiculoRepository
import com.unaula.proyecto_final.model.Vehiculo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VehiculoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VehiculoRepository(application)

    val vehiculos: StateFlow<List<Vehiculo>> = repository.vehiculos

    private val _vehiculoSeleccionado = MutableStateFlow<Vehiculo?>(null)
    val vehiculoSeleccionado: StateFlow<Vehiculo?> = _vehiculoSeleccionado.asStateFlow()

    init {
        viewModelScope.launch {
            vehiculos.collect { list ->
                if (_vehiculoSeleccionado.value == null && list.isNotEmpty()) {
                    _vehiculoSeleccionado.value = list.first()
                } else if (_vehiculoSeleccionado.value != null) {
                    val updated = list.find { it.id == _vehiculoSeleccionado.value?.id }
                    if (updated != null) {
                        _vehiculoSeleccionado.value = updated
                    }
                }
            }
        }
    }

    fun seleccionarVehiculo(vehiculo: Vehiculo) {
        _vehiculoSeleccionado.value = vehiculo
    }

    fun guardarVehiculo(vehiculo: Vehiculo) {
        repository.guardarVehiculo(vehiculo)
    }

    fun actualizarKilometraje(vehiculoId: String, nuevoKm: Int) {
        repository.actualizarKilometraje(vehiculoId, nuevoKm)
    }

    fun eliminarVehiculo(vehiculoId: String) {
        repository.eliminarVehiculo(vehiculoId)
        if (_vehiculoSeleccionado.value?.id == vehiculoId) {
            _vehiculoSeleccionado.value = vehiculos.value.firstOrNull { it.id != vehiculoId }
        }
    }
}
