package com.epilog.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "toma",
    foreignKeys = [ForeignKey(
        entity = Medicamento::class,
        parentColumns = ["id"],
        childColumns = ["medicamentoId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Toma(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val medicamentoId: Long,
    val hora: String,               // "08:00"
    val alarmaActivada: Boolean = true,
    val tomada: Boolean = false,
    val fechaToma: Long? = null
)
