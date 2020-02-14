package com.okurchenko.ecocity.repository.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StationItem(
    @PrimaryKey val id: Int,
    val name: String,
    val time: String,
    val lat: Double,
    val lon: Double
) {
    fun copy(id: Int = this.id, name: String = this.name, time: String = this.time) =
        StationItem(id, name, time, lat, lon)
}