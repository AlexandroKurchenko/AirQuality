package com.okurchenko.ecocity.repository.utils

import com.okurchenko.ecocity.repository.model.StationDetails
import com.okurchenko.ecocity.repository.model.StationHistoryItem

object DetailsToPreviewConverter {
    fun convertData(details: StationDetails): StationHistoryItem {
        val sources = findSourceToDisplay(details)
        var source2: String? = null
        var source3: String? = null
        if (sources.isNotEmpty() && sources.size > 2) {
            source2 = sources.elementAt(0)
            source3 = sources.elementAt(1)
        }
        return StationHistoryItem(
            timeAgo = details.hoursAgo,
            aqi = details.aqi,
            owner = details.owner ?: "Not provided",
            source2 = source2,
            source3 = source3
        )
    }

    private fun findSourceToDisplay(details: StationDetails): MutableSet<String> {
        val sources = mutableSetOf<String>()
        details.pressure?.let { sources.add(it) }
        details.temp?.let { sources.add(it) }
        details.pm25?.let { sources.add(it) }
        details.pm10?.let { sources.add(it) }
        details.humidity?.let { sources.add(it) }
        details.solarRadiation?.let { sources.add(it) }
        details.yRadiation?.let { sources.add(it) }
        details.o3?.let { sources.add(it) }
        details.nh3?.let { sources.add(it) }
        details.no2?.let { sources.add(it) }
        details.so2?.let { sources.add(it) }
        details.h2s?.let { sources.add(it) }
        details.co?.let { sources.add(it) }
        details.windSpeed?.let { sources.add(it) }
        return sources
    }
}