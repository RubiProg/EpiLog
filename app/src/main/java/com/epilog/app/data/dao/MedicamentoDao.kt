package com.epilog.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.epilog.app.data.entity.Medicamento
import com.epilog.app.data.entity.Toma

@Dao
interface MedicamentoDao {

    @Query("SELECT * FROM medicamento ORDER BY activo DESC, nombre ASC")
    fun getAll(): LiveData<List<Medicamento>>

    @Query("SELECT * FROM medicamento WHERE activo = 1")
    fun getActivos(): LiveData<List<Medicamento>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(medicamento: Medicamento): Long

    @Update
    suspend fun update(medicamento: Medicamento)

    @Delete
    suspend fun delete(medicamento: Medicamento)

    // Tomas
    @Query("SELECT * FROM toma WHERE medicamentoId = :medicamentoId")
    fun getTomasByMedicamento(medicamentoId: Long): LiveData<List<Toma>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToma(toma: Toma): Long

    @Update
    suspend fun updateToma(toma: Toma)

    @Query("DELETE FROM toma WHERE medicamentoId = :medicamentoId")
    suspend fun deleteTomasByMedicamento(medicamentoId: Long)

    @Query("SELECT * FROM medicamento WHERE fecha_inicio BETWEEN :desde AND :hasta")
    suspend fun getByPeriodoSync(desde: Long, hasta: Long): List<Medicamento>

    @Transaction
    @Query("""
        SELECT * FROM toma 
        INNER JOIN medicamento ON toma.medicamentoId = medicamento.id 
        WHERE medicamento.activo = 1
    """)
    fun getTomasHoy(): LiveData<List<TomaConMedicamento>>
}

data class TomaConMedicamento(
    @Embedded val toma: Toma,
    @Relation(
        parentColumn = "medicamentoId",
        entityColumn = "id"
    )
    val medicamento: Medicamento
)
