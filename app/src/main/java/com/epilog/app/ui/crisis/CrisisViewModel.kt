package com.epilog.app.ui.crisis

import androidx.lifecycle.*
import com.epilog.app.EpiLogApplication
import com.epilog.app.data.entity.Crisis
import com.epilog.app.data.repository.EpiLogRepository
import kotlinx.coroutines.launch

class CrisisViewModel(private val repository: EpiLogRepository) : ViewModel() {

    val todasLasCrisis: LiveData<List<Crisis>> = repository.todasLasCrisis

    fun insertar(crisis: Crisis) = viewModelScope.launch {
        repository.insertarCrisis(crisis)
    }

    fun eliminar(crisis: Crisis) = viewModelScope.launch {
        repository.eliminarCrisis(crisis)
    }
}

class CrisisViewModelFactory(private val app: EpiLogApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return CrisisViewModel(app.repository) as T
    }
}
