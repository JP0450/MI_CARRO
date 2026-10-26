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
                anticipacionKm = 500,
                anticipacionDias = 7,
                estado = EstadoMantenimiento.AL_DIA,
                pausado = false
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
                anticipacionKm = 1000,
                anticipacionDias = 10,
                estado = EstadoMantenimiento.PROXIMO,
                pausado = false
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
                anticipacionKm = 300,
                anticipacionDias = 5,
                estado = EstadoMantenimiento.PROXIMO,
                pausado = false
            )
        )

        _realizados.value = listOf(
            MantenimientoRealizado(
                id = "r1",
                vehiculoId = "v1",
                titulo = "Cambio de Filtro de Aire y Batería",
                categoria = "Filtros",
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
                                anticipacionKm = (doc.getLong("anticipacionKm") ?: 500).toInt(),
                                anticipacionDias = (doc.getLong("anticipacionDias") ?: 7).toInt(),
                                estado = EstadoMantenimiento.valueOf(doc.getString("estado") ?: "AL_DIA"),
                                pospuestoHasta = doc.getLong("pospuestoHasta"),
                                pausado = doc.getBoolean("pausado") ?: false
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
                    "anticipacionKm" to item.anticipacionKm,
                    "anticipacionDias" to item.anticipacionDias,
                    "estado" to item.estado.name,
                    "pospuestoHasta" to item.pospuestoHasta,
                    "pausado" to item.pausado
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
                "anticipacionKm" to item.anticipacionKm,
                "anticipacionDias" to item.anticipacionDias,
                "estado" to item.estado.name,
                "pospuestoHasta" to item.pospuestoHasta,
                "pausado" to item.pausado
            )
        )
    }

    fun posponerAlerta(mantenimientoId: String) {
        val currentList = _programados.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == mantenimientoId }
        if (index >= 0) {
            val item = currentList[index]
            val antDias = item.anticipacionDias.toLong()
            val antKm = item.anticipacionKm
            val nuevaFecha = (item.fechaObjetivo ?: System.currentTimeMillis()) + (antDias * 24 * 60 * 60 * 1000)
            val nuevoKm = (item.kilometrajeObjetivo ?: 0) + antKm
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

    fun pausarAlerta(mantenimientoId: String, pausado: Boolean) {
        val currentList = _programados.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == mantenimientoId }
        if (index >= 0) {
            val item = currentList[index]
            val updated = item.copy(
                pausado = pausado,
                estado = if (pausado) EstadoMantenimiento.AL_DIA else item.estado
            )
            currentList[index] = updated
            _programados.value = currentList

            firestore?.collection("mantenimientos_programados")?.document(mantenimientoId)?.update(
                mapOf(
                    "pausado" to pausado,
                    "estado" to updated.estado.name
                )
            )
        }
    }

    // RF-21: Reprogramación automática transaccional robusta
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
                val nextKm = if (prog.frecuenciaKm != null) item.kilometrajeRealizado + prog.frecuenciaKm else prog.kilometrajeObjetivo
                val nextFecha = if (prog.frecuenciaMeses != null) {
                    item.fecha + (prog.frecuenciaMeses.toLong() * 30 * 24 * 60 * 60 * 1000)
                } else prog.fechaObjetivo

                val updatedProg = prog.copy(
                    kilometrajeObjetivo = nextKm,
                    fechaObjetivo = nextFecha,
                    estado = EstadoMantenimiento.AL_DIA,
                    pospuestoHasta = null,
                    pausado = false
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
                "categoria" to item.categoria,
                "tipo" to item.tipo.name,
                "taller" to item.taller,
                "fecha" to item.fecha,
                "kilometrajeRealizado" to item.kilometrajeRealizado,
                "costoManoDeObra" to item.costoManoDeObra,
                "costoRepuestos" to item.costoRepuestos,
                "costoTotal" to item.costoTotal,
                "observaciones" to item.observaciones,
                "evidenciasUrls" to item.evidenciasUrls,
                "repuestos" to item.repuestos.map { rep ->
                    mapOf(
                        "id" to rep.id,
                        "nombre" to rep.nombre,
                        "marca" to rep.marca,
                        "referencia" to rep.referencia,
                        "cantidad" to rep.cantidad,
                        "proveedor" to rep.proveedor,
                        "costo" to rep.costo,
                        "garantiaMeses" to rep.garantiaMeses,
                        "garantiaKm" to rep.garantiaKm,
                        "fechaInstalacion" to rep.fechaInstalacion,
                        "kilometrajeInstalacion" to rep.kilometrajeInstalacion
                    )
                }
            )
        )
    }

    fun evaluarEstadosMantenimientos(vehiculoId: String, kmActual: Int) {
        val currentList = _programados.value.map { prog ->
            if (prog.vehiculoId == vehiculoId) {
                if (prog.pausado) {
                    prog.copy(estado = EstadoMantenimiento.AL_DIA)
                } else {
                    val ahora = System.currentTimeMillis()
                    val vencidoKm = prog.kilometrajeObjetivo != null && kmActual >= prog.kilometrajeObjetivo
                    val vencidoFecha = prog.fechaObjetivo != null && ahora >= prog.fechaObjetivo

                    val antKm = prog.anticipacionKm
                    val antDias = prog.anticipacionDias.toLong()

                    val proximoKm = prog.kilometrajeObjetivo != null && kmActual >= (prog.kilometrajeObjetivo - antKm)
                    val proximoFecha = prog.fechaObjetivo != null && ahora >= (prog.fechaObjetivo - (antDias * 24 * 60 * 60 * 1000))

                    val nuevoEstado = when {
                        vencidoKm || vencidoFecha -> EstadoMantenimiento.VENCIDO
                        proximoKm || proximoFecha -> EstadoMantenimiento.PROXIMO
                        else -> EstadoMantenimiento.AL_DIA
                    }
                    prog.copy(estado = nuevoEstado)
                }
            } else {
                prog
            }
        }
        _programados.value = currentList
    }
}
