package com.unaula.proyecto_final.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.unaula.proyecto_final.data.ConfiguracionRepository
import kotlinx.coroutines.flow.StateFlow

class ConfiguracionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ConfiguracionRepository(application)

    val notificacionesActivas: StateFlow<Boolean> = repository.notificacionesActivas
    val anticipacionKm: StateFlow<Int> = repository.anticipacionKm
    val anticipacionDias: StateFlow<Int> = repository.anticipacionDias

    fun setNotificacionesActivas(activo: Boolean) {
        repository.setNotificacionesActivas(activo)
    }

    fun setAnticipacionKm(km: Int) {
        repository.setAnticipacionKm(km)
    }

    fun setAnticipacionDias(dias: Int) {
        repository.setAnticipacionDias(dias)
    }

    fun factoryReset(onReset: () -> Unit) {
        val appContext = getApplication<Application>()
        appContext.getSharedPreferences("micarro_config", Context.MODE_PRIVATE).edit().clear().apply()
        onReset()
    }
}
