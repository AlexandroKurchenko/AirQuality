package com.okurchenko.ecocity.repository.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["stationId", "hoursAgo"], unique = true)])
data class StationDetails(
    @PrimaryKey val hoursAgo: Int,
    val timeToSave: Long,
    val stationId: Int,
    val aqi: Int,
    val pm25: String?,
    val pm10: String?,
    val temp: String?,
    val humidity: String?,
    val pressure: String?,
    val solarRadiation: String?,
    val yRadiation: String?,
    val o3: String?,
    val nh3: String?,
    val no2: String?,
    val so2: String?,
    val h2s: String?,
    val co: String?,
    val windSpeed: String?,
    val owner: String?,
    val ownerUrl: String?
)