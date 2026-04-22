package com.cardiary.app.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.cardiary.app.data.model.FuelEntry

@Dao
interface FuelEntryDao {
    @Query("SELECT * FROM fuel_entries WHERE carId = :carId ORDER BY date DESC")
    fun getFuelEntriesForCar(carId: Long): LiveData<List<FuelEntry>>

    @Query("SELECT * FROM fuel_entries WHERE carId = :carId ORDER BY date DESC LIMIT 1")
    suspend fun getLastFuelEntry(carId: Long): FuelEntry?

    @Query("SELECT SUM(totalPrice) FROM fuel_entries WHERE carId = :carId")
    fun getTotalFuelCost(carId: Long): LiveData<Double>

    @Query("SELECT AVG(consumption) FROM fuel_entries WHERE carId = :carId AND consumption > 0")
    fun getAverageConsumption(carId: Long): LiveData<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelEntry(fuelEntry: FuelEntry): Long

    @Update
    suspend fun updateFuelEntry(fuelEntry: FuelEntry)

    @Delete
    suspend fun deleteFuelEntry(fuelEntry: FuelEntry)
}