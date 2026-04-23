package com.cardiary.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.cardiary.app.CarApplication
import com.cardiary.app.data.model.Car
import com.cardiary.app.data.model.FuelEntry
import com.cardiary.app.data.model.RepairEntry
import kotlinx.coroutines.launch

class CarViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as CarApplication).repository

    val allCars: LiveData<List<Car>> = repository.getAllCars()

    fun insertCar(car: Car) = viewModelScope.launch {
        repository.insertCar(car)
    }

    fun updateCar(car: Car) = viewModelScope.launch {
        repository.updateCar(car)
    }

    fun deleteCar(car: Car) = viewModelScope.launch {
        repository.deleteCar(car)
    }

    fun getCarById(carId: Long) = repository.getCarById(carId)

    fun getFuelEntries(carId: Long): LiveData<List<FuelEntry>> =
        repository.getFuelEntriesForCar(carId)

    fun getTotalFuelCost(carId: Long): LiveData<Double> =
        repository.getTotalFuelCost(carId)

    fun getAverageConsumption(carId: Long): LiveData<Double> =
        repository.getAverageConsumption(carId)

    fun insertFuelEntry(fuelEntry: FuelEntry) = viewModelScope.launch {
        repository.insertFuelEntry(fuelEntry)
    }

    fun deleteFuelEntry(fuelEntry: FuelEntry) = viewModelScope.launch {
        repository.deleteFuelEntry(fuelEntry)
    }

    // ---- Repair Entries ----
    fun getRepairEntries(carId: Long): LiveData<List<RepairEntry>> =
        repository.getRepairEntriesForCar(carId)

    fun getTotalRepairCost(carId: Long): LiveData<Double> =
        repository.getTotalRepairCost(carId)

    fun insertRepairEntry(repairEntry: RepairEntry) = viewModelScope.launch {
        repository.insertRepairEntry(repairEntry)
    }

    fun deleteRepairEntry(repairEntry: RepairEntry) = viewModelScope.launch {
        repository.deleteRepairEntry(repairEntry)
    }
}