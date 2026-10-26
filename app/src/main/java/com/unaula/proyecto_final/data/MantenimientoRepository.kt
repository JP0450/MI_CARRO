package com.unaula.proyecto_final.data

import android.content.Context
import com.unaula.proyecto_final.model.CategoriaMantenimiento
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

    private val _categorias = MutableStateFlow<List<CategoriaMantenimiento>>(
        listOf(
            CategoriaMantenimiento(id = "c1", nombre = "Aceite"),
            CategoriaMantenimiento(id = "c2", nombre = "Filtros"),
            CategoriaMantenimiento(id = "c3", nombre = "Frenos"),
            CategoriaMantenimiento(id = "c4", nombre = "Llantas"),
            CategoriaMantenimiento(id = "c5", nombre = "Batería"),
            CategoriaMantenimiento(id = "c6", nombre = "Refrigeración"),
            CategoriaMantenimiento(id = "c7", nombre = "Suspensión"),
            CategoriaMantenimiento(id = "c8", nombre = "Transmisión"),
            CategoriaMantenimiento(id = "c9", nombre = "Otros")
        )
    )
    val categorias: StateFlow<List<CategoriaMantenimiento>> = _categorias.asStateFlow()

    private val firestore = FirebaseProvider.getFirestore(context)

    init {
        val unMesMillis = 30L * 24 * 60 * 60 * 1000
        _programados.value = listOf(
            MantenimientoProgramado(
                id = "p1",
                vehiculoId = "v1",
                titulo = "Cambio de Aceite de Motor",
                descripcion = "Aceite sintético 10W-30 y filtro de aceite",
                categoria = "Aceite",
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
                categoria = "Frenos",
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
                categoria = "Transmisión",
                criterio = CriterioAlerta.KILOMETRAJE,
                kilometrajeObjetivo = 13000,
                frecuenciaKm = 3000,
                estado = EstadoMantenimiento.PROXIMO
            )
        )

        _realizados.value = listOf(
            MantenimientoRealizado(
                id = "r1",
                vehiculoId = "v1",
                titulo = "Cambio de Filtro de Aire y Batería",
                tipo = TipoMantenimiento.PREVENTIVO,
                taller = "Taller Central Toyota",
                fecha = System.currentTimeMillis() - (60L * 24 * 60 * 60 * 1000),
                kilometrajeRealizado = 40000,
                costoManoDeObra = 40000.0,
                costoRepuestos = 280000.0,
                costoTotal = 320000.0,
                observaciones = "Se cambió batería por garantía expirada",
                repuestos = listOf(
                    Repuesto(
                        id = "rep1",
                        nombre = "Batería Mac Gold",
                        marca = "MAC",
                        referencia = "GC-55",
                        cantidad = 1,
                        proveedor = "Almacén Baterías",
                        costo = 280000.0,
                        garantiaMeses = 12,
                        garantiaKm = 20000,
                        fechaInstalacion = System.currentTimeMillis() - (60L * 24 * 60 * 60 * 1000),
                        kilometrajeInstalacion = 40000
                    )
                )
            )
        )

        loadInitialData()
    }

    private fun loadInitialData() {
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
                                categoria = doc.getString("categoria") ?: "Otros",
                                criterio = CriterioAlerta.valueOf(doc.getString("criterio") ?: "AMBOS"),
                                kilometrajeObjetivo = doc.getLong("kilometrajeObjetivo")?.toInt(),
                                fechaObjetivo = doc.getLong("fechaObjetivo"),
                                frecuenciaKm = doc.getLong("frecuenciaKm")?.toInt(),
                                frecuenciaMeses = doc.getLong("frecuenciaMeses")?.toInt(),
                                estado = EstadoMantenimiento.valueOf(doc.getString("estado") ?: "AL_DIA"),
                                pospuestoHasta = doc.getLong("pospuestoHasta")
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (list.isNotEmpty()) {
                        _programados.value = list
                    } else {
                        migrarProgramadosAFirestore()
                    }
                }
            }
    }

    private fun migrarProgramadosAFirestore() {
        _programados.value.forEach { item ->
            firestore?.collection("mantenimientos_programados")?.document(item.id)?.set(
                mapOf(
                    "vehiculoId" to item.vehiculoId,
                    "titulo" to item.titulo,
                    "descripcion" to item.descripcion,
                    "categoria" to item.categoria,
                    "criterio" to item.criterio.name,
                    "kilometrajeObjetivo" to item.kilometrajeObjetivo,
                    "fechaObjetivo" to item.fechaObjetivo,
                    "frecuenciaKm" to item.frecuenciaKm,
                    "frecuenciaMeses" to item.frecuenciaMeses,
                    "estado" to item.estado.name,
                    "pospuestoHasta" to item.pospuestoHasta
                )
            )
        }
    }

    fun guardarProgramado(mantenimiento: MantenimientoProgramado) {
        val idToUse = if (mantenimiento.id.isBlank()) UUID.randomUUID().toString() else mantenimiento.id
        val item = mantenimiento.copy(id = idToUse, sincronizado = firestore != null)

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
                "categoria" to item.categoria,
                "criterio" to item.criterio.name,
                "kilometrajeObjetivo" to item.kilometrajeObjetivo,
                "fechaObjetivo" to item.fechaObjetivo,
                "frecuenciaKm" to item.frecuenciaKm,
                "frecuenciaMeses" to item.frecuenciaMeses,
                "estado" to item.estado.name,
                "pospuestoHasta" to item.pospuestoHasta
            )
        )
    }

    fun posponerAlerta(mantenimientoId: String) {
        val currentList = _programados.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == mantenimientoId }
        if (index >= 0) {
            val item = currentList[index]
            val nuevaFecha = (item.fechaObjetivo ?: System.currentTimeMillis()) + (7L * 24 * 60 * 60 * 1000)
            val nuevoKm = (item.kilometrajeObjetivo ?: 0) + 500
            val updated = item.copy(
                fechaObjetivo = nuevaFecha,
                kilometrajeObjetivo = nuevoKm,
                pospuestoHasta = nuevaFecha
            )
            currentList[index] = updated
            _programados.value = currentList

            firestore?.collection("mantenimientos_programados")?.document(mantenimientoId)?.update(
                mapOf(
                    "fechaObjetivo" to nuevaFecha,
                    "kilometrajeObjetivo" to nuevoKm,
                    "pospuestoHasta" to nuevaFecha
                )
            )
        }
    }

    fun registrarMantenimientoRealizado(realizado: MantenimientoRealizado) {
        val idToUse = if (realizado.id.isBlank()) UUID.randomUUID().toString() else realizado.id
        val item = realizado.copy(id = idToUse, sincronizado = firestore != null)

        val currentList = _realizados.value.toMutableList()
        currentList.add(0, item)
        _realizados.value = currentList

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
    }

    fun evaluarEstadosMantenimientos(vehiculoId: String, kmActual: Int) {
        val currentList = _programados.value.map { prog ->
            if (prog.vehiculoId == vehiculoId) {
                val ahora = System.currentTimeMillis()
                val vencidoKm = prog.kilometrajeObjetivo != null && kmActual >= prog.kilometrajeObjetivo
                val vencidoFecha = prog.fechaObjetivo != null && ahora >= prog.fechaObjetivo

                val proximoKm = prog.kilometrajeObjetivo != null && kmActual >= (prog.kilometrajeObjetivo - 500)
                val proximoFecha = prog.fechaObjetivo != null && ahora >= (prog.fechaObjetivo - (7L * 24 * 60 * 60 * 1000))

                val nuevoEstado = when {
                    vencidoKm || vencidoFecha -> EstadoMantenimiento.VENCIDO
                    proximoKm || proximoFecha -> EstadoMantenimiento.PROXIMO
                    else -> EstadoMantenimiento.AL_DIA
                }
                prog.copy(estado = nuevoEstado)
            } else {
                prog
            }
        }
        _programados.value = currentList
    }
}
