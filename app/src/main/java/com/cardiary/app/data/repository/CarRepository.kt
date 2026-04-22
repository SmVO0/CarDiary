package com.cardiary.app.data.repository

import androidx.lifecycle.LiveData
import com.cardiary.app.data.db.CarDao
import com.cardiary.app.data.db.FuelEntryDao
import com.cardiary.app.data.db.RepairEntryDao
import com.cardiary.app.data.model.Car
import com.cardiary.app.data.model.FuelEntry
import com.cardiary.app.data.model.RepairEntry

class CarRepository(
    private val carDao: CarDao,
    private val fuelEntryDao: FuelEntryDao,
    private val repairEntryDao: RepairEntryDao
) {
    // ---- Car operations ----
    fun getAllCars(): LiveData<List<Car>> = carDao.getAllCars()

    fun getCarById(carId: Long): LiveData<Car> = carDao.getCarById(carId)

    suspend fun insertCar(car: Car): Long = carDao.insertCar(car)

    suspend fun updateCar(car: Car) = carDao.updateCar(car)

    suspend fun deleteCar(car: Car) = carDao.deleteCar(car)

    // ---- Fuel operations ----
    fun getFuelEntriesForCar(carId: Long): LiveData<List<FuelEntry>> =
        fuelEntryDao.getFuelEntriesForCar(carId)

    suspend fun getLastFuelEntry(carId: Long): FuelEntry? =
        fuelEntryDao.getLastFuelEntry(carId)

    fun getTotalFuelCost(carId: Long): LiveData<Double> =
        fuelEntryDao.getTotalFuelCost(carId)

    fun getAverageConsumption(carId: Long): LiveData<Double> =
        fuelEntryDao.getAverageConsumption(carId)

    suspend fun insertFuelEntry(fuelEntry: FuelEntry): Long {
        val lastEntry = getLastFuelEntry(fuelEntry.carId)
        val consumption = if (lastEntry != null && lastEntry.odometer > 0) {
            val distance = fuelEntry.odometer - lastEntry.odometer
            if (distance > 0) (fuelEntry.liters / distance) * 100 else 0.0
        } else 0.0

        return fuelEntryDao.insertFuelEntry(fuelEntry.copy(consumption = consumption))
    }

    suspend fun updateFuelEntry(fuelEntry: FuelEntry) =
        fuelEntryDao.updateFuelEntry(fuelEntry)

    suspend fun deleteFuelEntry(fuelEntry: FuelEntry) =
        fuelEntryDao.deleteFuelEntry(fuelEntry)

    // ---- Repair operations ----
    fun getRepairEntriesForCar(carId: Long): LiveData<List<RepairEntry>> =
        repairEntryDao.getRepairEntriesForCar(carId)

    fun getTotalRepairCost(carId: Long): LiveData<Double> =
        repairEntryDao.getTotalRepairCost(carId)

    suspend fun insertRepairEntry(repairEntry: RepairEntry): Long =
        repairEntryDao.insertRepairEntry(repairEntry)

    suspend fun updateRepairEntry(repairEntry: RepairEntry) =
        repairEntryDao.updateRepairEntry(repairEntry)

    suspend fun deleteRepairEntry(repairEntry: RepairEntry) =
        repairEntryDao.deleteRepairEntry(repairEntry)
}