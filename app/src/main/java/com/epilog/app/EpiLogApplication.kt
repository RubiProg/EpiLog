package com.epilog.app

import android.app.Application
import com.epilog.app.data.database.EpiLogDatabase
import com.epilog.app.data.repository.EpiLogRepository

class EpiLogApplication : Application() {

    val database by lazy { EpiLogDatabase.getDatabase(this) }

    val repository by lazy {
        EpiLogRepository(
            database.crisisDao(),
            database.medicamentoDao(),
            database.pruebaMedicaDao(),
            database.perfilDao()
        )
    }
}
