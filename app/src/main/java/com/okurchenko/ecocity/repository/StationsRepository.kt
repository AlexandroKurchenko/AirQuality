package com.okurchenko.ecocity.repository

import android.location.Location
import com.okurchenko.ecocity.repository.model.StationDetails
import com.okurchenko.ecocity.repository.model.StationHistoryItem
import com.okurchenko.ecocity.repository.model.StationItem

interface StationsRepository {

    suspend fun getAllStationSortedByDistance(location: Location): List<StationItem>

    suspend fun getAllStationSortedByName(): List<StationItem>

    suspend fun fetchHistoryItemsByStationId(
        stationId: Int,
        fromTimeShift: Int,
        toTimeShift: Int
    ): List<StationHistoryItem>

    suspend fun fetchStationDetailsById(stationId: Int, timeShift: Int): StationDetails?
}