package com.cardiary.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "fuel_entries",
    foreignKeys = [ForeignKey(
        entity = Car::class,
        parentColumns = ["id"],
        childColumns = ["carId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class FuelEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val carId: Long,
    val date: Long,
    val liters: Double,
    val pricePerLiter: Double,
    val totalPrice: Double,
    val odometer: Int,
    val consumption: Double = 0.0,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val receiptImagePath: String? = null,
    val notes: String? = ""
)