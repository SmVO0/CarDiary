package com.cardiary.app.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.cardiary.app.data.model.Car

@Dao
interface CarDao {
    @Query("SELECT * FROM cars ORDER BY createdAt DESC")
    fun getAllCars(): LiveData<List<Car>>

    @Query("SELECT * FROM cars WHERE id = :carId")
    fun getCarById(carId: Long): LiveData<Car>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: Car): Long

    @Update
    suspend fun updateCar(car: Car)

    @Delete
    suspend fun deleteCar(car: Car)
}