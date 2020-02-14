package com.okurchenko.ecocity.repository.utils

import com.okurchenko.ecocity.network.model.StationResponse
import com.okurchenko.ecocity.repository.model.StationItem

const val DASH = "-"

object StationItemAggregator {
    fun convertStationResponseToInstance(data: List<StationResponse>): List<StationItem> {
        return data.map { response ->
            val nameBuilder = StringBuilder()
            val locationName = response.localName
            if (checkItem(locationName)) {
                nameBuilder.append("$locationName")
            }
            val cityName = response.cityName
            if (checkItem(cityName)) {
                if (nameBuilder.isNotEmpty()) {
                    nameBuilder.append(", ")
                }
                nameBuilder.append("$cityName")
            }
            val stationName = response.stationName
            if (checkItem(stationName)) {
                if (nameBuilder.isNotEmpty()) {
                    nameBuilder.append(" ($stationName)")
                } else
                    nameBuilder.append("$stationName")
            }
            StationItem(
                id = response.id.toInt(),
                name = nameBuilder.toString(),
                time = response.time,
                lat = response.latitude.toDouble(),
                lon = response.longitude.toDouble()
            )
        }
    }

    private fun checkItem(item: String?): Boolean {
        return item != null && item.isNotBlank() && item != DASH
    }
}