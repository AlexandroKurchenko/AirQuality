package com.okurchenko.ecocity.repository.utils

import com.okurchenko.ecocity.network.model.Owner
import com.okurchenko.ecocity.network.model.StationDataResponse
import com.okurchenko.ecocity.repository.model.StationDetails
import com.okurchenko.ecocity.utils.round
import timber.log.Timber

private const val NOT_PROVIDED_VALUE = -1

object DetailsAggregator {

    fun prepareStationDetail(data: List<StationDataResponse>, stationId: Int, timeShift: Int): StationDetails? {
        val builder =
            StationDetailsBuilder(hoursAgo = timeShift, stationId = stationId, timeToSave = System.currentTimeMillis())
        data.forEach { response ->
            when {
                response.index != null -> {
                    addProperResponse(builder, response)
                    builder.aqi = response.index
                }
                response.name != null -> addProperResponse(builder, response)
                response.owner != null -> addOwner(builder, response.owner)
            }
        }
        return if (builder.aqi != NOT_PROVIDED_VALUE) builder.build() else null
    }

    private fun addProperResponse(builder: StationDetailsBuilder, response: StationDataResponse) {
        when (response.name) {
            Details.PM25.detailsName -> prepareValue(response, Details.PM25.detailsName)?.let { builder.pm25 = it }
            Details.PM10.detailsName -> prepareValue(response, Details.PM10.detailsName)?.let { builder.pm10 = it }
            Details.Temperature.detailsName ->
                prepareValue(response, Details.Temperature.detailsName)?.let { builder.temp = it }
            Details.Humidity.detailsName ->
                prepareValue(response, Details.Humidity.detailsName)?.let { builder.humidity = it }
            Details.Pressure.detailsName ->
                prepareValue(response, Details.Pressure.detailsName)?.let { builder.pressure = it }
            Details.YRadiation.detailsName ->
                prepareValue(response, Details.YRadiation.detailsName)?.let { builder.yRadiation = it }
            Details.SolarRadiation.detailsName ->
                prepareValue(response, Details.SolarRadiation.detailsName)?.let { builder.solarRadiation = it }
            Details.O3.detailsName -> prepareValue(response, Details.O3.detailsName)?.let { builder.o3 = it }
            Details.NH3.detailsName -> prepareValue(response, Details.NH3.detailsName)?.let { builder.nh3 = it }
            Details.NO.detailsName -> prepareValue(response, Details.NO.detailsName)?.let { builder.no2 = it }
            Details.SO.detailsName -> prepareValue(response, Details.SO.detailsName)?.let { builder.so2 = it }
            Details.CO.detailsName -> prepareValue(response, Details.CO.detailsName)?.let { builder.co = it }
            Details.H.detailsName -> prepareValue(response, Details.H.detailsName)?.let { builder.h2s = it }
            Details.WindSpeed.detailsName ->
                prepareValue(response, Details.WindSpeed.detailsName)?.let { builder.windSpeed = it }
        }
    }

    private fun addOwner(builder: StationDetailsBuilder, owner: Owner?) {
        owner?.name?.run { builder.owner = owner.name }
        owner?.url?.run { builder.ownerUrl = owner.url }
    }

    private fun prepareValue(response: StationDataResponse, defName: String): String? {
        try {
            val unit = response.localUnit ?: response.unit
            if (response.value != null && unit != null && response.cr != null) {
                val name = response.localName ?: defName
                val value = response.value.toDouble() * response.cr.toDouble()
                return "$name: ${round(value, 1)} $unit"
            }
        } catch (ex: NumberFormatException) {
            Timber.e(ex)
        }
        return null
    }

    private data class StationDetailsBuilder(
        val hoursAgo: Int,
        val stationId: Int,
        var aqi: Int = NOT_PROVIDED_VALUE,
        val timeToSave: Long,
        var pm25: String? = null,
        var pm10: String? = null,
        var temp: String? = null,
        var humidity: String? = null,
        var pressure: String? = null,
        var solarRadiation: String? = null,
        var yRadiation: String? = null,
        var o3: String? = null,
        var nh3: String? = null,
        var no2: String? = null,
        var so2: String? = null,
        var h2s: String? = null,
        var co: String? = null,
        var windSpeed: String? = null,
        var owner: String? = null,
        var ownerUrl: String? = null
    ) {
        fun build() = StationDetails(
            hoursAgo = hoursAgo,
            stationId = stationId,
            aqi = aqi,
            pm25 = pm25,
            pm10 = pm10,
            temp = temp,
            humidity = humidity,
            pressure = pressure,
            solarRadiation = solarRadiation,
            yRadiation = yRadiation,
            o3 = o3,
            nh3 = nh3,
            no2 = no2,
            so2 = so2,
            h2s = h2s,
            co = co,
            windSpeed = windSpeed,
            owner = owner,
            ownerUrl = ownerUrl,
            timeToSave = timeToSave
        )
    }

    private enum class Details(val detailsName: String) {
        PM25("PM2.5"),
        PM10("PM10"),
        Temperature("Temperature"),
        Humidity("Humidity"),
        Pressure("Pressure"),
        YRadiation("y-Radiation"),
        SolarRadiation("Solar Radiation"),
        O3("O\u2083"),
        NH3("NH\u2083"),
        NO("NO\u2082"),
        SO("SO\u2082"),
        CO("CO"),
        H("H\u2082S"),
        WindSpeed("Wind Speed")
    }
}