package com.epilog.app.ui.estadisticas

import androidx.lifecycle.*
import com.epilog.app.EpiLogApplication
import com.epilog.app.data.repository.EpiLogRepository
import com.epilog.app.data.entity.Crisis
import java.util.*

class EstadisticasViewModel(private val repository: EpiLogRepository) : ViewModel() {

    val todasLasCrisis: LiveData<List<Crisis>> = repository.todasLasCrisis

    // Transformamos las crisis en datos para la gráfica (Crisis por día en los últimos 7 días)
    val crisisUltimos7Dias: LiveData<Map<Int, Float>> = todasLasCrisis.map { lista ->
        val cal = Calendar.getInstance()
        val hoy = cal.get(Calendar.DAY_OF_YEAR)
        
        val mapa = mutableMapOf<Int, Float>()
        // Inicializar los últimos 7 días con 0
        for (i in 0..6) {
            mapa[hoy - i] = 0f
        }

        lista.forEach { crisis ->
            cal.timeInMillis = crisis.fecha
            val diaCrisis = cal.get(Calendar.DAY_OF_YEAR)
            if (mapa.containsKey(diaCrisis)) {
                mapa[diaCrisis] = (mapa[diaCrisis] ?: 0f) + 1f
            }
        }
        mapa.toSortedMap()
    }

    // Nueva gráfica: Distribución por hora del día
    val crisisPorHora: LiveData<Map<Int, Float>> = todasLasCrisis.map { lista ->
        val mapa = mutableMapOf<Int, Float>()
        // Inicializar las 24 horas con 0
        for (i in 0..23) {
            mapa[i] = 0f
        }

        val cal = Calendar.getInstance()
        lista.forEach { crisis ->
            cal.timeInMillis = crisis.fecha
            val hora = cal.get(Calendar.HOUR_OF_DAY)
            mapa[hora] = (mapa[hora] ?: 0f) + 1f
        }
        mapa.toSortedMap()
    }

    // Nueva estadística: Duración media de las crisis
    val duracionMedia: LiveData<String> = todasLasCrisis.map { lista ->
        if (lista.isEmpty()) return@map "0 min"
        
        var totalSegundos = 0
        var crisisConDuracion = 0
        lista.forEach { crisis ->
            // Intentar parsear "X min Y seg" o "X min"
            val regex = Regex("(\\d+)\\s*min(?:\\s*(\\d+)\\s*seg)?")
            val match = regex.find(crisis.duracion)
            if (match != null) {
                val mins = match.groupValues[1].toInt()
                val segs = match.groupValues[2].takeIf { it.isNotEmpty() && it.isNotBlank() }?.toInt() ?: 0
                totalSegundos += (mins * 60) + segs
                crisisConDuracion++
            }
        }
        
        if (crisisConDuracion == 0) return@map "N/A"
        
        val mediaSegundos = totalSegundos / crisisConDuracion
        val mediaMins = mediaSegundos / 60
        val mediaRestoSegs = mediaSegundos % 60
        
        if (mediaRestoSegs > 0) "${mediaMins}m ${mediaRestoSegs}s" else "${mediaMins} min"
    }
}

class EstadisticasViewModelFactory(private val app: EpiLogApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return EstadisticasViewModel(app.repository) as T
    }
}
