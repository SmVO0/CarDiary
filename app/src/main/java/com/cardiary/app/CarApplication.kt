package com.cardiary.app

import android.app.Application
import com.cardiary.app.data.db.AppDatabase
import com.cardiary.app.data.repository.CarRepository
import com.cardiary.app.utils.NotificationHelper

class CarApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }

    val repository by lazy {
        CarRepository(
            database.carDao(),
            database.fuelEntryDao(),
            database.repairEntryDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
        NotificationHelper.scheduleInspectionReminder(this)
    }
}