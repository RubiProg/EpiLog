package com.epilog.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prueba_medica")
data class PruebaMedica(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tipo: String,               // "RM" | "TAC" | "Analítica" | "EEG" | "ECG" | "Otra"
    val descripcion: String? = null,
    val fecha: Long,
    val notas: String? = null,
    val uriPdf: String? = null      // Ruta o URI del archivo PDF subido
)
