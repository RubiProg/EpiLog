package com.epilog.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.epilog.app.data.entity.Perfil

@Dao
interface PerfilDao {

    @Query("SELECT * FROM perfil WHERE id = 1")
    fun get(): LiveData<Perfil?>

    @Query("SELECT * FROM perfil WHERE id = 1")
    suspend fun getSync(): Perfil?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(perfil: Perfil)

    @Update
    suspend fun update(perfil: Perfil)
}
