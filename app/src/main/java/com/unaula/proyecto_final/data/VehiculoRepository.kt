package com.unaula.proyecto_final.data

import android.content.Context
import com.unaula.proyecto_final.model.TipoVehiculo
import com.unaula.proyecto_final.model.Vehiculo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class VehiculoRepository(private val context: Context) {

    private val _vehiculos = MutableStateFlow<List<Vehiculo>>(emptyList())
    val vehiculos: StateFlow<List<Vehiculo>> = _vehiculos.asStateFlow()

    private val firestore = FirebaseProvider.getFirestore(context)

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        if (firestore != null) {
            firestore.collection("vehiculos")
                .addSnapshotListener { snapshot, error ->
                    if (error == null && snapshot != null) {
                        val list = snapshot.documents.mapNotNull { doc ->
                            try {
                                val tipoStr = doc.getString("tipo") ?: "AUTOMOVIL"
                                Vehiculo(
                                    id = doc.id,
                                    placa = doc.getString("placa") ?: "",
                                    marca = doc.getString("marca") ?: "",
                                    modelo = doc.getString("modelo") ?: "",
                                    anio = (doc.getLong("anio") ?: 2022).toInt(),
                                    tipo = TipoVehiculo.valueOf(tipoStr),
                                    kilometrajeActual = (doc.getLong("kilometrajeActual") ?: 0).toInt(),
                                    fechaSeguroSoat = doc.getLong("fechaSeguroSoat"),
                                    fechaTecnoMecanica = doc.getLong("fechaTecnoMecanica"),
                                    fotoUrl = doc.getString("fotoUrl"),
                                    fechaActualizacionKm = doc.getLong("fechaActualizacionKm") ?: System.currentTimeMillis()
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        if (list.isNotEmpty()) {
                            _vehiculos.value = list
                        }
                    }
                }
        }

        // Si está vacío, agregar datos de muestra locales para prueba inicial
        if (_vehiculos.value.isEmpty()) {
            val unAnioAdelante = System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000)
            _vehiculos.value = listOf(
                Vehiculo(
                    id = "v1",
                    placa = "ABC-123",
                    marca = "Toyota",
                    modelo = "Corolla",
                    anio = 2021,
                    tipo = TipoVehiculo.AUTOMOVIL,
                    kilometrajeActual = 45200,
                    fechaSeguroSoat = unAnioAdelante,
                    fechaTecnoMecanica = unAnioAdelante
                ),
                Vehiculo(
                    id = "v2",
                    placa = "XYZ-789",
                    marca = "Yamaha",
                    modelo = "FZ 25",
                    anio = 2023,
                    tipo = TipoVehiculo.MOTOCICLETA,
                    kilometrajeActual = 12500,
                    fechaSeguroSoat = null, // Para demostrar alerta de documentación faltante
                    fechaTecnoMecanica = null
                )
            )
        }
    }

    fun guardarVehiculo(vehiculo: Vehiculo) {
        val idToUse = if (vehiculo.id.isBlank()) UUID.randomUUID().toString() else vehiculo.id
        val newVehiculo = vehiculo.copy(id = idToUse)

        val currentList = _vehiculos.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == idToUse }
        if (index >= 0) {
            currentList[index] = newVehiculo
        } else {
            currentList.add(newVehiculo)
        }
        _vehiculos.value = currentList

        firestore?.collection("vehiculos")?.document(idToUse)?.set(
            mapOf(
                "placa" to newVehiculo.placa,
                "marca" to newVehiculo.marca,
                "modelo" to newVehiculo.modelo,
                "anio" to newVehiculo.anio,
                "tipo" to newVehiculo.tipo.name,
                "kilometrajeActual" to newVehiculo.kilometrajeActual,
                "fechaSeguroSoat" to newVehiculo.fechaSeguroSoat,
                "fechaTecnoMecanica" to newVehiculo.fechaTecnoMecanica,
                "fotoUrl" to newVehiculo.fotoUrl,
                "fechaActualizacionKm" to newVehiculo.fechaActualizacionKm
            )
        )
    }

    fun actualizarKilometraje(vehiculoId: String, nuevoKm: Int) {
        val currentList = _vehiculos.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == vehiculoId }
        if (index >= 0) {
            val updated = currentList[index].copy(
                kilometrajeActual = nuevoKm,
                fechaActualizacionKm = System.currentTimeMillis()
            )
            currentList[index] = updated
            _vehiculos.value = currentList

            firestore?.collection("vehiculos")?.document(vehiculoId)?.update(
                mapOf(
                    "kilometrajeActual" to nuevoKm,
                    "fechaActualizacionKm" to updated.fechaActualizacionKm
                )
            )
        }
    }

    fun eliminarVehiculo(vehiculoId: String) {
        _vehiculos.value = _vehiculos.value.filterNot { it.id == vehiculoId }
        firestore?.collection("vehiculos")?.document(vehiculoId)?.delete()
    }
}
