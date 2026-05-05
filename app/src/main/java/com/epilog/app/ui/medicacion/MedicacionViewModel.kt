package com.epilog.app.ui.medicacion

import androidx.lifecycle.*
import com.epilog.app.EpiLogApplication
import com.epilog.app.data.entity.Medicamento
import com.epilog.app.data.entity.Toma
import com.epilog.app.data.repository.EpiLogRepository
import kotlinx.coroutines.launch

class MedicacionViewModel(private val repository: EpiLogRepository) : ViewModel() {

    val todosMedicamentos: LiveData<List<Medicamento>> = repository.todosMedicamentos

    fun insertarMedicamento(medicamento: Medicamento, tomas: List<Toma>) =
        viewModelScope.launch {
            val id = repository.insertarMedicamento(medicamento)
            tomas.forEach { toma ->
                repository.insertarToma(toma.copy(medicamentoId = id))
            }
        }

    fun marcarTomada(toma: Toma) = viewModelScope.launch {
        repository.actualizarToma(
            toma.copy(tomada = true, fechaToma = System.currentTimeMillis())
        )
    }

    fun tomasPorMedicamento(id: Long) = repository.tomasPorMedicamento(id)
}

class MedicacionViewModelFactory(private val app: EpiLogApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MedicacionViewModel(app.repository) as T
    }
}
