package com.unaula.proyecto_final.data

import android.content.Context
import com.unaula.proyecto_final.model.HistorialKilometraje
import com.unaula.proyecto_final.model.TipoCombustible
import com.unaula.proyecto_final.model.TipoVehiculo
import com.unaula.proyecto_final.model.Vehiculo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class VehiculoRepository(private val context: Context) {

    companion object {
        private val _vehiculos = MutableStateFlow<List<Vehiculo>>(emptyList())
        private val _historialKm = MutableStateFlow<List<HistorialKilometraje>>(emptyList())
        private var isInitialized = false
    }

    val vehiculos: StateFlow<List<Vehiculo>> = _vehiculos.asStateFlow()
    val historialKm: StateFlow<List<HistorialKilometraje>> = _historialKm.asStateFlow()

    private val firestore = FirebaseProvider.getFirestore(context)

    init {
        if (!isInitialized) {
            isInitialized = true
            val unAnioAdelante = System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000)
            val defaultList = listOf(
                Vehiculo(
                    id = "v1",
                    placa = "ABC-123",
                    marca = "Toyota",
                    modelo = "Corolla",
                    anio = 2021,
                    tipo = TipoVehiculo.AUTOMOVIL,
                    kilometrajeActual = 45200,
                    fechaSeguroSoat = unAnioAdelante,
                    fechaTecnoMecanica = unAnioAdelante,
                    esPrincipal = true,
                    color = "Blanco Perlado",
                    vin = "1HGCR2F33HA000001",
                    tipoCombustible = TipoCombustible.GASOLINA,
                    cilindraje = "2000 cc",
                    sincronizado = true,
                    archivado = false
                ),
                Vehiculo(
                    id = "v2",
                    placa = "XYZ-789",
                    marca = "Yamaha",
                    modelo = "FZ 25",
                    anio = 2023,
                    tipo = TipoVehiculo.MOTOCICLETA,
                    kilometrajeActual = 12500,
                    fechaSeguroSoat = null,
                    fechaTecnoMecanica = null,
                    esPrincipal = false,
                    color = "Negro Mate",
                    vin = "ME1RE2503P000002",
                    tipoCombustible = TipoCombustible.GASOLINA,
                    cilindraje = "250 cc",
                    sincronizado = true,
                    archivado = false
                )
            )
            _vehiculos.value = defaultList.sortedByDescending { it.esPrincipal }

            _historialKm.value = listOf(
                HistorialKilometraje(id = "hk1", vehiculoId = "v1", kilometraje = 45200, nota = "Lectura inicial"),
                HistorialKilometraje(id = "hk2", vehiculoId = "v2", kilometraje = 12500, nota = "Lectura inicial")
            )
        }

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
                                val combStr = doc.getString("tipoCombustible") ?: "GASOLINA"
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
                                    fechaActualizacionKm = doc.getLong("fechaActualizacionKm") ?: System.currentTimeMillis(),
                                    esPrincipal = doc.getBoolean("esPrincipal") ?: false,
                                    color = doc.getString("color") ?: "",
                                    vin = doc.getString("vin") ?: "",
                                    tipoCombustible = try { TipoCombustible.valueOf(combStr) } catch (e: Exception) { TipoCombustible.GASOLINA },
                                    cilindraje = doc.getString("cilindraje") ?: "",
                                    sincronizado = true,
                                    archivado = doc.getBoolean("archivado") ?: false
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        if (list.isNotEmpty()) {
                            val finalList = if (list.none { it.esPrincipal }) {
                                list.mapIndexed { idx, v -> if (idx == 0) v.copy(esPrincipal = true) else v }
                            } else {
                                list
                            }
                            _vehiculos.value = finalList.sortedByDescending { it.esPrincipal }
                        } else {
                            migrarVehiculosAFirestore()
                        }
                    }
                }
        }
    }

    private fun migrarVehiculosAFirestore() {
        val currentList = _vehiculos.value
        currentList.forEach { vehiculo ->
            firestore?.collection("vehiculos")?.document(vehiculo.id)?.set(
                mapOf(
                    "placa" to vehiculo.placa,
                    "marca" to vehiculo.marca,
                    "modelo" to vehiculo.modelo,
                    "anio" to vehiculo.anio,
                    "tipo" to vehiculo.tipo.name,
                    "kilometrajeActual" to vehiculo.kilometrajeActual,
                    "fechaSeguroSoat" to vehiculo.fechaSeguroSoat,
                    "fechaTecnoMecanica" to vehiculo.fechaTecnoMecanica,
                    "fotoUrl" to vehiculo.fotoUrl,
                    "fechaActualizacionKm" to vehiculo.fechaActualizacionKm,
                    "esPrincipal" to vehiculo.esPrincipal,
                    "color" to vehiculo.color,
                    "vin" to vehiculo.vin,
                    "tipoCombustible" to vehiculo.tipoCombustible.name,
                    "cilindraje" to vehiculo.cilindraje,
                    "archivado" to vehiculo.archivado
                )
            )
        }
    }

    fun guardarVehiculo(vehiculo: Vehiculo): Boolean {
        val placaNorm = vehiculo.placa.trim().uppercase()
        val idToUse = if (vehiculo.id.isBlank()) UUID.randomUUID().toString() else vehiculo.id

        val duplicate = _vehiculos.value.find { it.id != idToUse && it.placa.trim().uppercase() == placaNorm }
        if (duplicate != null) {
            return false
        }

        val currentList = _vehiculos.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == idToUse }

        val makePrincipal = vehiculo.esPrincipal || currentList.isEmpty() || (index >= 0 && currentList[index].esPrincipal)

        val updatedVehiculo = vehiculo.copy(
            id = idToUse,
            placa = placaNorm,
            esPrincipal = makePrincipal,
            sincronizado = firestore != null
        )

        val processedList = currentList.map { v ->
            if (makePrincipal) {
                v.copy(esPrincipal = (v.id == idToUse))
            } else {
                v
            }
        }.toMutableList()

        if (index >= 0) {
            processedList[index] = updatedVehiculo
        } else {
            processedList.add(updatedVehiculo)
            val histList = _historialKm.value.toMutableList()
            histList.add(
                HistorialKilometraje(
                    id = UUID.randomUUID().toString(),
                    vehiculoId = idToUse,
                    kilometraje = updatedVehiculo.kilometrajeActual,
                    nota = "Kilometraje inicial de registro"
                )
            )
            _historialKm.value = histList
        }

        val finalAssignedList = if (processedList.none { it.esPrincipal } && processedList.isNotEmpty()) {
            processedList.mapIndexed { idx, v -> if (idx == 0) v.copy(esPrincipal = true) else v }
        } else {
            processedList
        }

        _vehiculos.value = finalAssignedList.sortedByDescending { it.esPrincipal }

        finalAssignedList.forEach { v ->
            firestore?.collection("vehiculos")?.document(v.id)?.set(
                mapOf(
                    "placa" to v.placa,
                    "marca" to v.marca,
                    "modelo" to v.modelo,
                    "anio" to v.anio,
                    "tipo" to v.tipo.name,
                    "kilometrajeActual" to v.kilometrajeActual,
                    "fechaSeguroSoat" to v.fechaSeguroSoat,
                    "fechaTecnoMecanica" to v.fechaTecnoMecanica,
                    "fotoUrl" to v.fotoUrl,
                    "fechaActualizacionKm" to v.fechaActualizacionKm,
                    "esPrincipal" to v.esPrincipal,
                    "color" to v.color,
                    "vin" to v.vin,
                    "tipoCombustible" to v.tipoCombustible.name,
                    "cilindraje" to v.cilindraje,
                    "archivado" to v.archivado
                )
            )
        }
        return true
    }

    fun asignarVehiculoPrincipal(vehiculoId: String) {
        val currentList = _vehiculos.value.map { v ->
            v.copy(esPrincipal = (v.id == vehiculoId))
        }.sortedByDescending { it.esPrincipal }

        _vehiculos.value = currentList

        currentList.forEach { v ->
            firestore?.collection("vehiculos")?.document(v.id)?.update("esPrincipal", v.esPrincipal)
        }
    }

    fun archivarVehiculo(vehiculoId: String, archivado: Boolean) {
        val currentList = _vehiculos.value.map { v ->
            if (v.id == vehiculoId) v.copy(archivado = archivado) else v
        }.toMutableList()

        val target = currentList.find { it.id == vehiculoId }
        if (archivado && target?.esPrincipal == true) {
            val nextActive = currentList.firstOrNull { !it.archivado && it.id != vehiculoId }
            if (nextActive != null) {
                val idxTarget = currentList.indexOfFirst { it.id == vehiculoId }
                val idxNext = currentList.indexOfFirst { it.id == nextActive.id }
                if (idxTarget >= 0) currentList[idxTarget] = currentList[idxTarget].copy(esPrincipal = false)
                if (idxNext >= 0) currentList[idxNext] = currentList[idxNext].copy(esPrincipal = true)
            }
        }

        _vehiculos.value = currentList.sortedByDescending { it.esPrincipal }

        currentList.forEach { v ->
            firestore?.collection("vehiculos")?.document(v.id)?.set(
                mapOf(
                    "placa" to v.placa,
                    "marca" to v.marca,
                    "modelo" to v.modelo,
                    "anio" to v.anio,
                    "tipo" to v.tipo.name,
                    "kilometrajeActual" to v.kilometrajeActual,
                    "fechaSeguroSoat" to v.fechaSeguroSoat,
                    "fechaTecnoMecanica" to v.fechaTecnoMecanica,
                    "fotoUrl" to v.fotoUrl,
                    "fechaActualizacionKm" to v.fechaActualizacionKm,
                    "esPrincipal" to v.esPrincipal,
                    "color" to v.color,
                    "vin" to v.vin,
                    "tipoCombustible" to v.tipoCombustible.name,
                    "cilindraje" to v.cilindraje,
                    "archivado" to v.archivado
                )
            )
        }
    }

    fun actualizarKilometraje(vehiculoId: String, nuevoKm: Int, nota: String = "Actualización manual") {
        val currentList = _vehiculos.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == vehiculoId }
        if (index >= 0) {
            val updated = currentList[index].copy(
                kilometrajeActual = nuevoKm,
                fechaActualizacionKm = System.currentTimeMillis()
            )
            currentList[index] = updated
            _vehiculos.value = currentList.sortedByDescending { it.esPrincipal }

            val histList = _historialKm.value.toMutableList()
            histList.add(
                HistorialKilometraje(
                    id = UUID.randomUUID().toString(),
                    vehiculoId = vehiculoId,
                    kilometraje = nuevoKm,
                    nota = nota
                )
            )
            _historialKm.value = histList

            firestore?.collection("vehiculos")?.document(vehiculoId)?.update(
                mapOf(
                    "kilometrajeActual" to nuevoKm,
                    "fechaActualizacionKm" to updated.fechaActualizacionKm
                )
            )
        }
    }

    fun eliminarVehiculo(vehiculoId: String) {
        val remaining = _vehiculos.value.filterNot { it.id == vehiculoId }
        val finalRemaining = if (remaining.isNotEmpty() && remaining.none { it.esPrincipal }) {
            remaining.mapIndexed { idx, v -> if (idx == 0) v.copy(esPrincipal = true) else v }
        } else {
            remaining
        }
        _vehiculos.value = finalRemaining.sortedByDescending { it.esPrincipal }
        firestore?.collection("vehiculos")?.document(vehiculoId)?.delete()

        finalRemaining.find { it.esPrincipal }?.let { newPrincipal ->
            firestore?.collection("vehiculos")?.document(newPrincipal.id)?.update("esPrincipal", true)
        }
    }
}
