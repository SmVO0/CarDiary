package com.cardiary.app

import android.app.Application
import com.cardiary.app.data.db.AppDatabase
import com.cardiary.app.data.repository.CarRepository

class CarApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }

    val repository by lazy {
        CarRepository(
            database.carDao(),
            database.fuelEntryDao(),
            database.repairEntryDao()
        )
    }
}