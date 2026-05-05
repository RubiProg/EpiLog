package com.epilog.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.epilog.app.data.dao.*
import com.epilog.app.data.entity.*

@Database(
    entities = [
        Crisis::class,
        Medicamento::class,
        Toma::class,
        PruebaMedica::class,
        DocumentoPrueba::class,
        Perfil::class
    ],
    version = 2,
    exportSchema = false
)
abstract class EpiLogDatabase : RoomDatabase() {

    abstract fun crisisDao(): CrisisDao
    abstract fun medicamentoDao(): MedicamentoDao
    abstract fun pruebaMedicaDao(): PruebaMedicaDao
    abstract fun perfilDao(): PerfilDao

    companion object {
        @Volatile
        private var INSTANCE: EpiLogDatabase? = null

        fun getDatabase(context: Context): EpiLogDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EpiLogDatabase::class.java,
                    "epilog_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
