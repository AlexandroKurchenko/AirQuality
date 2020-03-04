package com.okurchenko.ecocity.repository.utils

import android.content.SharedPreferences
import android.location.Location
import androidx.core.content.edit
import com.okurchenko.ecocity.utils.diffTimeInMinutes
import com.okurchenko.ecocity.utils.getNowTime
import com.okurchenko.ecocity.utils.round

const val REFRESH_TIME = 30//min

private const val FETCH_ALL_STATIONS_SYNC_TIME = "FETCH_ALL_STATIONS_SYNC_TIME"
private const val CURRENT_LOCATION_LAT = "CURRENT_LOCATION_LAT"
private const val CURRENT_LOCATION_LON = "CURRENT_LOCATION_LON"

class StationPreferencesManager(private val preferences: SharedPreferences) {

    fun saveAllStationsSyncTime() = preferences.edit { putLong(FETCH_ALL_STATIONS_SYNC_TIME, getNowTime()) }

    fun saveCurrentLocation(location: Location) = preferences.edit {
        putFloat(CURRENT_LOCATION_LAT, round(location.latitude, 2).toFloat())
        putFloat(CURRENT_LOCATION_LON, round(location.longitude, 2).toFloat())
    }

    fun getCurrentLat() = preferences.getFloat(CURRENT_LOCATION_LAT, 0F)

    fun getCurrentLon() = preferences.getFloat(CURRENT_LOCATION_LON, 0F)

    fun isLastFetchFresh(): Boolean {
        val lastFetchTime = preferences.getLong(FETCH_ALL_STATIONS_SYNC_TIME, 0)
        return lastFetchTime.diffTimeInMinutes() < REFRESH_TIME
    }
}