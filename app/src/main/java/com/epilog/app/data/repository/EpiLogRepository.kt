package com.epilog.app.data.repository

import androidx.lifecycle.LiveData
import com.epilog.app.data.dao.*
import com.epilog.app.data.entity.*

class EpiLogRepository(
    private val crisisDao: CrisisDao,
    private val medicamentoDao: MedicamentoDao,
    private val pruebaMedicaDao: PruebaMedicaDao,
    private val perfilDao: PerfilDao
) {
    // ── Crisis ──────────────────────────────────────────────────────────────
    val todasLasCrisis: LiveData<List<Crisis>> = crisisDao.getAll()

    fun crisisPorPeriodo(desde: Long, hasta: Long) =
        crisisDao.getByPeriodo(desde, hasta)

    fun crisisEsteMes(inicioMes: Long): LiveData<Int> =
        crisisDao.countEsteMes(inicioMes)

    suspend fun ultimaCrisis(): Crisis? = crisisDao.getUltima()

    suspend fun insertarCrisis(crisis: Crisis) = crisisDao.insert(crisis)

    suspend fun actualizarCrisis(crisis: Crisis) = crisisDao.update(crisis)

    suspend fun eliminarCrisis(crisis: Crisis) = crisisDao.delete(crisis)

    suspend fun crisisPorPeriodoSync(desde: Long, hasta: Long) =
        crisisDao.getByPeriodoSync(desde, hasta)

    // ── Medicamentos ─────────────────────────────────────────────────────────
    val todosMedicamentos: LiveData<List<Medicamento>> = medicamentoDao.getAll()
    val medicamentosActivos: LiveData<List<Medicamento>> = medicamentoDao.getActivos()

    suspend fun insertarMedicamento(medicamento: Medicamento) =
        medicamentoDao.insert(medicamento)

    suspend fun actualizarMedicamento(medicamento: Medicamento) =
        medicamentoDao.update(medicamento)

    fun tomasPorMedicamento(id: Long) = medicamentoDao.getTomasByMedicamento(id)

    suspend fun insertarToma(toma: Toma) = medicamentoDao.insertToma(toma)

    suspend fun actualizarToma(toma: Toma) = medicamentoDao.updateToma(toma)

    suspend fun eliminarTomasDeMedicamento(id: Long) =
        medicamentoDao.deleteTomasByMedicamento(id)

    fun getTomasHoy(): LiveData<List<TomaConMedicamento>> = medicamentoDao.getTomasHoy()

    // ── Pruebas médicas ──────────────────────────────────────────────────────
    val todasLasPruebas: LiveData<List<PruebaMedica>> = pruebaMedicaDao.getAll()

    suspend fun insertarPrueba(prueba: PruebaMedica) = pruebaMedicaDao.insert(prueba)

    suspend fun eliminarPrueba(prueba: PruebaMedica) = pruebaMedicaDao.delete(prueba)

    fun documentosDePrueba(pruebaId: Long) = pruebaMedicaDao.getDocumentos(pruebaId)

    suspend fun insertarDocumento(documento: DocumentoPrueba) =
        pruebaMedicaDao.insertDocumento(documento)

    suspend fun pruebaPorPeriodoSync(desde: Long, hasta: Long) =
        pruebaMedicaDao.getByPeriodoSync(desde, hasta)

    // ── Perfil ───────────────────────────────────────────────────────────────
    val perfil: LiveData<Perfil?> = perfilDao.get()

    suspend fun guardarPerfil(perfil: Perfil) = perfilDao.insert(perfil)

    suspend fun perfilSync(): Perfil? = perfilDao.getSync()
}
