package com.okurchenko.ecocity.repository.utils

import com.okurchenko.ecocity.network.model.StationResponse
import com.okurchenko.ecocity.repository.model.StationItem

private const val DASH = "-"

object StationItemAggregator {
    fun convertStationResponseToInstance(data: List<StationResponse>): List<StationItem> {
        return data.filter { response -> isNameNotEmpty(response) }
            .map { response ->
                StationItem(
                    id = response.id.toInt(),
                    name = getStationItemName(response),
                    time = response.time,
                    lat = response.latitude.toDouble(),
                    lon = response.longitude.toDouble()
                )
            }
    }

    private fun isNameNotEmpty(response: StationResponse): Boolean =
        !response.localName.isNullOrEmpty() && !response.cityName.isNullOrEmpty() && !response.stationName
            .isNullOrEmpty()

    private fun getStationItemName(response: StationResponse): String {
        val nameBuilder = StringBuilder()
        val locationName = response.localName
        if (checkItem(locationName)) {
            nameBuilder.append("$locationName".trim())
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
        return nameBuilder.toString()
    }

    private fun checkItem(item: String?): Boolean {
        return item != null && item.isNotBlank() && item != DASH
    }
}