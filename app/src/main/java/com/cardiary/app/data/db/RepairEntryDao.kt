package com.cardiary.app.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.cardiary.app.data.model.RepairEntry

@Dao
interface RepairEntryDao {
    @Query("SELECT * FROM repair_entries WHERE carId = :carId ORDER BY date DESC")
    fun getRepairEntriesForCar(carId: Long): LiveData<List<RepairEntry>>

    @Query("SELECT SUM(cost) FROM repair_entries WHERE carId = :carId")
    fun getTotalRepairCost(carId: Long): LiveData<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepairEntry(repairEntry: RepairEntry): Long

    @Update
    suspend fun updateRepairEntry(repairEntry: RepairEntry)

    @Delete
    suspend fun deleteRepairEntry(repairEntry: RepairEntry)
}