package com.cardiary.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "repair_entries",
    foreignKeys = [ForeignKey(
        entity = Car::class,
        parentColumns = ["id"],
        childColumns = ["carId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class RepairEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val carId: Long,
    val date: Long,
    val category: RepairCategory,
    val description: String,
    val cost: Double,
    val odometer: Int,
    val receiptImagePath: String? = null,
    val notes: String? = ""
)

enum class RepairCategory {
    OIL_CHANGE,
    TIRES,
    BRAKES,
    TECHNICAL_INSPECTION,
    INSURANCE,
    OTHER
}