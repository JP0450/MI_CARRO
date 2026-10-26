package com.unaula.proyecto_final.model

enum class TipoVehiculo(val label: String) {
    AUTOMOVIL("Automóvil"),
    CAMIONETA("Camioneta"),
    MOTOCICLETA("Motocicleta")
}

enum class TipoCombustible(val label: String) {
    GASOLINA("Gasolina"),
    DIESEL("Diésel"),
    GAS("Gas (GLP/GNV)"),
    ELECTRICO("Eléctrico"),
    HIBRIDO("Híbrido")
}

data class Vehiculo(
    val id: String = "",
    val placa: String = "",
    val marca: String = "",
    val modelo: String = "",
    val anio: Int = 0,
    val tipo: TipoVehiculo = TipoVehiculo.AUTOMOVIL,
    val kilometrajeActual: Int = 0,
    val fechaSeguroSoat: Long? = null,
    val fechaTecnoMecanica: Long? = null,
    val fotoUrl: String? = null,
    val fechaActualizacionKm: Long = System.currentTimeMillis(),
    val esPrincipal: Boolean = false,
    val color: String = "",
    val vin: String = "",
    val tipoCombustible: TipoCombustible = TipoCombustible.GASOLINA,
    val cilindraje: String = "",
    val sincronizado: Boolean = true
) {
    val tieneDocumentacionCompleta: Boolean
        get() = fechaSeguroSoat != null && fechaTecnoMecanica != null

    val documentacionVencidaOFaltante: Boolean
        get() {
            if (fechaSeguroSoat == null || fechaTecnoMecanica == null) return true
            val ahora = System.currentTimeMillis()
            return fechaSeguroSoat < ahora || fechaTecnoMecanica < ahora
        }

    val mensajeDocumentacion: String
        get() = when {
            fechaSeguroSoat == null || fechaTecnoMecanica == null ->
                "⚠️ No puede circular por falta de documentación (SOAT / Técnico-Mecánica no ingresados)."
            fechaSeguroSoat < System.currentTimeMillis() && fechaTecnoMecanica < System.currentTimeMillis() ->
                "⚠️ No puede circular: SOAT y Técnico-Mecánica vencidos."
            fechaSeguroSoat < System.currentTimeMillis() ->
                "⚠️ No puede circular: Seguro SOAT vencido."
            fechaTecnoMecanica < System.currentTimeMillis() ->
                "⚠️ No puede circular: Revisión Técnico-Mecánica vencida."
            else -> "✅ Documentación al día (SOAT y Tecno-Mecánica vigentes)."
        }
}

data class HistorialKilometraje(
    val id: String = "",
    val vehiculoId: String = "",
    val kilometraje: Int = 0,
    val fecha: Long = System.currentTimeMillis(),
    val nota: String = "",
    val sincronizado: Boolean = true
)

data class CategoriaMantenimiento(
    val id: String = "",
    val nombre: String = "",
    val predeterminada: Boolean = true
)

enum class CriterioAlerta(val label: String) {
    FECHA("Por Fecha"),
    KILOMETRAJE("Por Kilometraje"),
    AMBOS("Por Fecha y Kilometraje")
}

enum class EstadoMantenimiento(val label: String) {
    AL_DIA("Al día"),
    PROXIMO("Próximo a vencer"),
    VENCIDO("Vencido"),
    REALIZADO("Realizado")
}

data class MantenimientoProgramado(
    val id: String = "",
    val vehiculoId: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: String = "Otros",
    val criterio: CriterioAlerta = CriterioAlerta.AMBOS,
    val kilometrajeObjetivo: Int? = null,
    val fechaObjetivo: Long? = null,
    val frecuenciaKm: Int? = null,
    val frecuenciaMeses: Int? = null,
    val estado: EstadoMantenimiento = EstadoMantenimiento.AL_DIA,
    val pospuestoHasta: Long? = null,
    val sincronizado: Boolean = true
)

enum class TipoMantenimiento(val label: String) {
    PREVENTIVO("Preventivo"),
    CORRECTIVO("Correctivo")
}

data class Repuesto(
    val id: String = "",
    val nombre: String = "",
    val marca: String = "",
    val referencia: String = "",
    val cantidad: Int = 1,
    val proveedor: String = "",
    val costo: Double = 0.0,
    val garantiaMeses: Int = 0,
    val garantiaKm: Int = 0,
    val fechaInstalacion: Long = System.currentTimeMillis(),
    val kilometrajeInstalacion: Int = 0
)

data class MantenimientoRealizado(
    val id: String = "",
    val vehiculoId: String = "",
    val mantenimientoProgramadoId: String? = null,
    val titulo: String = "",
    val tipo: TipoMantenimiento = TipoMantenimiento.PREVENTIVO,
    val taller: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val kilometrajeRealizado: Int = 0,
    val costoManoDeObra: Double = 0.0,
    val costoRepuestos: Double = 0.0,
    val costoTotal: Double = 0.0,
    val observaciones: String = "",
    val evidenciasUrls: List<String> = emptyList(),
    val repuestos: List<Repuesto> = emptyList(),
    val sincronizado: Boolean = true
)
