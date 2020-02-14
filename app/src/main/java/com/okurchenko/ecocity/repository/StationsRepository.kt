package com.okurchenko.ecocity.repository

import com.okurchenko.ecocity.repository.model.StationDetails
import com.okurchenko.ecocity.repository.model.StationHistoryItem
import com.okurchenko.ecocity.repository.model.StationItem

interface StationsRepository {

    suspend fun fetchAllStations(): List<StationItem>

    suspend fun fetchStationHistoryItemsById(
        stationId: Int,
        fromTimeShift: Int,
        toTimeShift: Int
    ): List<StationHistoryItem>

    suspend fun fetchStationDetailsById(
        stationId: Int,
        fromTimeShift: Int,
        toTimeShift: Int
    ): List<StationDetails>
}