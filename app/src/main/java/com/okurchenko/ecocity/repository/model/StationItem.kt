package com.okurchenko.ecocity.repository.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class StationItem(
    @PrimaryKey val id: Int,
    val name: String,
    val time: String,
    val lat: Double,
    val lon: Double,
    val distance: Double
) : Parcelable {
    fun copy(
        id: Int = this.id,
        name: String = this.name,
        time: String = this.time,
        distance: Double = this.distance
    ) =
        StationItem(id, name, time, lat, lon, distance)
}