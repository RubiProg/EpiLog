package com.epilog.app.ui.inicio

import androidx.lifecycle.*
import com.epilog.app.EpiLogApplication
import com.epilog.app.data.dao.TomaConMedicamento
import com.epilog.app.data.entity.Crisis
import com.epilog.app.data.entity.Perfil
import com.epilog.app.data.repository.EpiLogRepository
import java.util.Calendar

class InicioViewModel(private val repository: EpiLogRepository) : ViewModel() {

    val perfil: LiveData<Perfil?> = repository.perfil
    val todasLasCrisis: LiveData<List<Crisis>> = repository.todasLasCrisis
    val tomasHoy: LiveData<List<TomaConMedicamento>> = repository.getTomasHoy()

    val crisisEsteMes: LiveData<Int> = repository.crisisEsteMes(inicioMesActual())

    private fun inicioMesActual(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}

class InicioViewModelFactory(private val app: EpiLogApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return InicioViewModel(app.repository) as T
    }
}
