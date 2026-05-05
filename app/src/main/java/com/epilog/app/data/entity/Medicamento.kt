package com.epilog.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicamento")
data class Medicamento(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val dosis: String,
    @ColumnInfo(name = "tomas_al_dia")
    val tomasAlDia: Int,
    @ColumnInfo(name = "fecha_inicio")
    val fechaInicio: Long,
    val activo: Boolean = true
)
