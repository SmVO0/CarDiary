package com.cardiary.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cardiary.app.data.model.Car
import com.cardiary.app.data.model.FuelEntry
import com.cardiary.app.data.model.RepairEntry

@Database(
    entities = [Car::class, FuelEntry::class, RepairEntry::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun carDao(): CarDao
    abstract fun fuelEntryDao(): FuelEntryDao
    abstract fun repairEntryDao(): RepairEntryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "car_diary_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}