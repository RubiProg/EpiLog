package com.epilog.app.ui.perfil

import androidx.lifecycle.*
import com.epilog.app.EpiLogApplication
import com.epilog.app.data.entity.Perfil
import com.epilog.app.data.entity.PruebaMedica
import com.epilog.app.data.repository.EpiLogRepository
import kotlinx.coroutines.launch

class PerfilViewModel(private val repository: EpiLogRepository) : ViewModel() {

    val perfil: LiveData<Perfil?> = repository.perfil
    val pruebas = repository.todasLasPruebas

    fun guardar(perfil: Perfil) = viewModelScope.launch {
        repository.guardarPerfil(perfil)
    }

    fun guardarPrueba(prueba: PruebaMedica) = viewModelScope.launch {
        repository.insertarPrueba(prueba)
    }

    fun eliminarPrueba(prueba: PruebaMedica) = viewModelScope.launch {
        repository.eliminarPrueba(prueba)
    }
}

class PerfilViewModelFactory(private val app: EpiLogApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return PerfilViewModel(app.repository) as T
    }
}
