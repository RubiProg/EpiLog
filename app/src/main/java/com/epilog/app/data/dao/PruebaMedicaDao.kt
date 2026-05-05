package com.epilog.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.epilog.app.data.entity.DocumentoPrueba
import com.epilog.app.data.entity.PruebaMedica

@Dao
interface PruebaMedicaDao {

    @Query("SELECT * FROM prueba_medica ORDER BY fecha DESC")
    fun getAll(): LiveData<List<PruebaMedica>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(prueba: PruebaMedica): Long

    @Update
    suspend fun update(prueba: PruebaMedica)

    @Delete
    suspend fun delete(prueba: PruebaMedica)

    @Query("SELECT * FROM documento_prueba WHERE pruebaId = :pruebaId")
    fun getDocumentos(pruebaId: Long): LiveData<List<DocumentoPrueba>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocumento(documento: DocumentoPrueba): Long

    @Delete
    suspend fun deleteDocumento(documento: DocumentoPrueba)

    @Query("SELECT * FROM prueba_medica WHERE fecha BETWEEN :desde AND :hasta ORDER BY fecha DESC")
    suspend fun getByPeriodoSync(desde: Long, hasta: Long): List<PruebaMedica>
}
