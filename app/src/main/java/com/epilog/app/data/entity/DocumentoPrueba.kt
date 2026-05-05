package com.epilog.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "documento_prueba",
    foreignKeys = [ForeignKey(
        entity = PruebaMedica::class,
        parentColumns = ["id"],
        childColumns = ["pruebaId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class DocumentoPrueba(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pruebaId: Long,
    val nombre: String,
    val rutaLocal: String,
    val tipo: String,           // MIME type
    val tamanyo: Long
)
