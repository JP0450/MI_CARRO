package com.unaula.proyecto_final.data

import android.content.Context
import com.unaula.proyecto_final.model.CriterioAlerta
import com.unaula.proyecto_final.model.EstadoMantenimiento
import com.unaula.proyecto_final.model.MantenimientoProgramado
import com.unaula.proyecto_final.model.MantenimientoRealizado
import com.unaula.proyecto_final.model.Repuesto
import com.unaula.proyecto_final.model.TipoMantenimiento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class MantenimientoRepository(private val context: Context) {

    private val _programados = MutableStateFlow<List<MantenimientoProgramado>>(emptyList())
    val programados: StateFlow<List<MantenimientoProgramado>> = _programados.asStateFlow()

    private val _realizados = MutableStateFlow<List<MantenimientoRealizado>>(emptyList())
    val realizados: StateFlow<List<MantenimientoRealizado>> = _realizados.asStateFlow()

    private val firestore = FirebaseProvider.getFirestore(context)

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        // Cargar Firestore si está disponible
        firestore?.collection("mantenimientos_programados")
            ?.addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            MantenimientoProgramado(
                                id = doc.id,
                                vehiculoId = doc.getString("vehiculoId") ?: "",
                                titulo = doc.getString("titulo") ?: "",
                                descripcion = doc.getString("descripcion") ?: "",
                                criterio = CriterioAlerta.valueOf(doc.getString("criterio") ?: "AMBOS"),
                                kilometrajeObjetivo = doc.getLong("kilometrajeObjetivo")?.toInt(),
                                fechaObjetivo = doc.getLong("fechaObjetivo"),
                                frecuenciaKm = doc.getLong("frecuenciaKm")?.toInt(),
                                frecuenciaMeses = doc.getLong("frecuenciaMeses")?.toInt(),
                                estado = EstadoMantenimiento.valueOf(doc.getString("estado") ?: "AL_DIA")
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (list.isNotEmpty()) {
                        _programados.value = list
                    }
                }
            }

        firestore?.collection("mantenimientos_realizados")
            ?.addSnapshotListener { snapshot, error ->
                if (error == null && snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            MantenimientoRealizado(
                                id = doc.id,
                                vehiculoId = doc.getString("vehiculoId") ?: "",
                                mantenimientoProgramadoId = doc.getString("mantenimientoProgramadoId"),
                                titulo = doc.getString("titulo") ?: "",
                                tipo = TipoMantenimiento.valueOf(doc.getString("tipo") ?: "PREVENTIVO"),
                                taller = doc.getString("taller") ?: "",
                                fecha = doc.getLong("fecha") ?: System.currentTimeMillis(),
                                kilometrajeRealizado = (doc.getLong("kilometrajeRealizado") ?: 0).toInt(),
                                costoTotal = doc.getDouble("costoTotal") ?: 0.0,
                                observaciones = doc.getString("observaciones") ?: "",
                                evidenciasUrls = (doc.get("evidenciasUrls") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (list.isNotEmpty()) {
                        _realizados.value = list
                    }
                }
            }

        // Cargar datos de muestra iniciales si no hay
        if (_programados.value.isEmpty()) {
            val unMesMillis = 30L * 24 * 60 * 60 * 1000
            _programados.value = listOf(
                MantenimientoProgramado(
                    id = "p1",
                    vehiculoId = "v1",
                    titulo = "Cambio de Aceite de Motor",
                    descripcion = "Aceite sintético 10W-30 y filtro de aceite",
                    criterio = CriterioAlerta.AMBOS,
                    kilometrajeObjetivo = 50000,
                    fechaObjetivo = System.currentTimeMillis() + unMesMillis,
                    frecuenciaKm = 10000,
                    frecuenciaMeses = 6,
                    estado = EstadoMantenimiento.AL_DIA
                ),
                MantenimientoProgramado(
                    id = "p2",
                    vehiculoId = "v1",
                    titulo = "Revisión de Frenos",
                    descripcion = "Cambio de pastillas delanteras y rectificación de discos",
                    criterio = CriterioAlerta.KILOMETRAJE,
                    kilometrajeObjetivo = 46000,
                    frecuenciaKm = 20000,
                    estado = EstadoMantenimiento.PROXIMO
                ),
                MantenimientoProgramado(
                    id = "p3",
                    vehiculoId = "v2",
                    titulo = "Mantenimiento de Cadena y Kit de Arrastre",
                    descripcion = "Limpieza, lubricación y tensión de cadena",
                    criterio = CriterioAlerta.KILOMETRAJE,
                    kilometrajeObjetivo = 13000,
                    frecuenciaKm = 3000,
                    estado = EstadoMantenimiento.PROXIMO
                )
            )
        }

        if (_realizados.value.isEmpty()) {
            _realizados.value = listOf(
                MantenimientoRealizado(
                    id = "r1",
                    vehiculoId = "v1",
                    titulo = "Cambio de Filtro de Aire y Batería",
                    tipo = TipoMantenimiento.PREVENTIVO,
                    taller = "Taller Central Toyota",
                    fecha = System.currentTimeMillis() - (60L * 24 * 60 * 60 * 1000),
                    kilometrajeRealizado = 40000,
                    costoTotal = 320000.0,
                    observaciones = "Se cambió batería por garantía expirada",
                    repuestos = listOf(
                        Repuesto(
                            id = "rep1",
                            nombre = "Batería Mac Gold",
                            marca = "MAC",
                            costo = 280000.0,
                            garantiaMeses = 12,
                            garantiaKm = 20000,
                            fechaInstalacion = System.currentTimeMillis() - (60L * 24 * 60 * 60 * 1000),
                            kilometrajeInstalacion = 40000
                        )
                    )
                )
            )
        }
    }

    fun guardarProgramado(mantenimiento: MantenimientoProgramado) {
        val idToUse = if (mantenimiento.id.isBlank()) UUID.randomUUID().toString() else mantenimiento.id
        val item = mantenimiento.copy(id = idToUse)

        val currentList = _programados.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == idToUse }
        if (index >= 0) {
            currentList[index] = item
        } else {
            currentList.add(item)
        }
        _programados.value = currentList

        firestore?.collection("mantenimientos_programados")?.document(idToUse)?.set(
            mapOf(
                "vehiculoId" to item.vehiculoId,
                "titulo" to item.titulo,
                "descripcion" to item.descripcion,
                "criterio" to item.criterio.name,
                "kilometrajeObjetivo" to item.kilometrajeObjetivo,
                "fechaObjetivo" to item.fechaObjetivo,
                "frecuenciaKm" to item.frecuenciaKm,
                "frecuenciaMeses" to item.frecuenciaMeses,
                "estado" to item.estado.name
            )
        )
    }

    fun registrarMantenimientoRealizado(realizado: MantenimientoRealizado) {
        val idToUse = if (realizado.id.isBlank()) UUID.randomUUID().toString() else realizado.id
        val item = realizado.copy(id = idToUse)

        val currentList = _realizados.value.toMutableList()
        currentList.add(0, item)
        _realizados.value = currentList

        // Si fue originado de un mantenimiento programado, actualizarlo como REALIZADO o reprogramar siguiente
        if (!item.mantenimientoProgramadoId.isNullOrBlank()) {
            val progList = _programados.value.toMutableList()
            val index = progList.indexOfFirst { it.id == item.mantenimientoProgramadoId }
            if (index >= 0) {
                val prog = progList[index]
                val nextKm = if (prog.frecuenciaKm != null) item.kilometrajeRealizado + prog.frecuenciaKm else null
                val nextFecha = if (prog.frecuenciaMeses != null) {
                    item.fecha + (prog.frecuenciaMeses.toLong() * 30 * 24 * 60 * 60 * 1000)
                } else null

                val updatedProg = prog.copy(
                    kilometrajeObjetivo = nextKm,
                    fechaObjetivo = nextFecha,
                    estado = EstadoMantenimiento.AL_DIA
                )
                progList[index] = updatedProg
                _programados.value = progList
                guardarProgramado(updatedProg)
            }
        }

        firestore?.collection("mantenimientos_realizados")?.document(idToUse)?.set(
            mapOf(
                "vehiculoId" to item.vehiculoId,
                "mantenimientoProgramadoId" to item.mantenimientoProgramadoId,
                "titulo" to item.titulo,
                "tipo" to item.tipo.name,
                "taller" to item.taller,
                "fecha" to item.fecha,
                "kilometrajeRealizado" to item.kilometrajeRealizado,
                "costoTotal" to item.costoTotal,
                "observaciones" to item.observaciones,
                "evidenciasUrls" to item.evidenciasUrls
            )
        )
    }

    fun evaluarEstadosMantenimientos(vehiculoId: String, kmActual: Int) {
        val progList = _programados.value.toMutableList()
        var changed = false
        val ahora = System.currentTimeMillis()
        val margenKm = 500 // Alerta cuando falten 500 km o menos

        progList.forEachIndexed { i, prog ->
            if (prog.vehiculoId == vehiculoId && prog.estado != EstadoMantenimiento.REALIZADO) {
                var esVencido = false
                var esProximo = false

                // Evaluar por Kilometraje
                if (prog.criterio == CriterioAlerta.KILOMETRAJE || prog.criterio == CriterioAlerta.AMBOS) {
                    prog.kilometrajeObjetivo?.let { kmObj ->
                        if (kmActual >= kmObj) {
                            esVencido = true
                        } else if (kmObj - kmActual <= margenKm) {
                            esProximo = true
                        }
                    }
                }

                // Evaluar por Fecha
                if (prog.criterio == CriterioAlerta.FECHA || prog.criterio == CriterioAlerta.AMBOS) {
                    prog.fechaObjetivo?.let { fObj ->
                        val diasSombra = 7L * 24 * 60 * 60 * 1000 // 7 días de anticipación
                        if (ahora >= fObj) {
                            esVencido = true
                        } else if (fObj - ahora <= diasSombra) {
                            esProximo = true
                        }
                    }
                }

                val nuevoEstado = when {
                    esVencido -> EstadoMantenimiento.VENCIDO
                    esProximo -> EstadoMantenimiento.PROXIMO
                    else -> EstadoMantenimiento.AL_DIA
                }

                if (prog.estado != nuevoEstado) {
                    progList[i] = prog.copy(estado = nuevoEstado)
                    changed = true
                }
            }
        }

        if (changed) {
            _programados.value = progList
        }
    }
}
