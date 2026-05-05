package com.epilog.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crisis")
data class Crisis(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fecha: Long,
    val duracion: String,
    @ColumnInfo(name = "origen_crisis")
    val origenCrisis: String,
    @ColumnInfo(name = "consciencia_preservada")
    val conscienciaPreservada: Boolean?,
    val manifestaciones: String,
    val sintomas: String,
    val desencadenantes: String,
    val notas: String? = null
)
