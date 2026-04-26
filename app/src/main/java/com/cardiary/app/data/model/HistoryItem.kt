package com.cardiary.app.data.model

sealed class HistoryItem {
    data class Fuel(val entry: FuelEntry) : HistoryItem()
    data class Repair(val entry: RepairEntry) : HistoryItem()

    fun getDate(): Long = when (this) {
        is Fuel -> entry.date
        is Repair -> entry.date
    }

    fun getCost(): Double = when (this) {
        is Fuel -> entry.totalPrice
        is Repair -> entry.cost
    }

    fun getOdometer(): Int = when (this) {
        is Fuel -> entry.odometer
        is Repair -> entry.odometer
    }
}