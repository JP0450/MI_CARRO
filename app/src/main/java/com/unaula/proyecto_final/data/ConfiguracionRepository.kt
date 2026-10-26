package com.unaula.proyecto_final.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConfiguracionRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("micarro_config", Context.MODE_PRIVATE)

    private val _notificacionesActivas = MutableStateFlow(prefs.getBoolean("notificaciones_activas", true))
    val notificacionesActivas: StateFlow<Boolean> = _notificacionesActivas.asStateFlow()

    private val _anticipacionKm = MutableStateFlow(prefs.getInt("anticipacion_km", 500))
    val anticipacionKm: StateFlow<Int> = _anticipacionKm.asStateFlow()

    private val _anticipacionDias = MutableStateFlow(prefs.getInt("anticipacion_dias", 7))
    val anticipacionDias: StateFlow<Int> = _anticipacionDias.asStateFlow()

    fun setNotificacionesActivas(activo: Boolean) {
        _notificacionesActivas.value = activo
        prefs.edit().putBoolean("notificaciones_activas", activo).apply()
    }

    fun setAnticipacionKm(km: Int) {
        _anticipacionKm.value = km
        prefs.edit().putInt("anticipacion_km", km).apply()
    }

    fun setAnticipacionDias(dias: Int) {
        _anticipacionDias.value = dias
        prefs.edit().putInt("anticipacion_dias", dias).apply()
    }
}
