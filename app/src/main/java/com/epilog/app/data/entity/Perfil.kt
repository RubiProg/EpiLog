package com.epilog.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "perfil")
data class Perfil(
    @PrimaryKey
    val id: Long = 1,           // Único registro
    val nombre: String = "",
    val email: String = "",
    val tipoEpilepsia: String? = null,
    val neurologo: String? = null,
    val ultimaVisita: Long? = null,
    val contactoNombre: String? = null,
    val contactoTelefono: String? = null
)
