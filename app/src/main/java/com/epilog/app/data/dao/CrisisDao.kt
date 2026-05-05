package com.epilog.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.epilog.app.data.entity.Crisis

@Dao
interface CrisisDao {

    @Query("SELECT * FROM crisis ORDER BY fecha DESC")
    fun getAll(): LiveData<List<Crisis>>

    @Query("SELECT * FROM crisis WHERE fecha BETWEEN :desde AND :hasta ORDER BY fecha DESC")
    fun getByPeriodo(desde: Long, hasta: Long): LiveData<List<Crisis>>

    @Query("SELECT COUNT(*) FROM crisis WHERE fecha >= :inicioMes")
    fun countEsteMes(inicioMes: Long): LiveData<Int>

    @Query("SELECT * FROM crisis WHERE fecha = :id")
    suspend fun getById(id: Long): Crisis?

    @Query("SELECT * FROM crisis ORDER BY fecha DESC LIMIT 1")
    suspend fun getUltima(): Crisis?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(crisis: Crisis): Long

    @Update
    suspend fun update(crisis: Crisis)

    @Delete
    suspend fun delete(crisis: Crisis)

    @Query("SELECT * FROM crisis WHERE fecha BETWEEN :desde AND :hasta ORDER BY fecha DESC")
    suspend fun getByPeriodoSync(desde: Long, hasta: Long): List<Crisis>
}
