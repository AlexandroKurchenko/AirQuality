package com.okurchenko.ecocity.repository

import androidx.paging.PagingSource
import com.okurchenko.ecocity.network.model.StationDataResponse
import com.okurchenko.ecocity.repository.model.StationDetails
import com.okurchenko.ecocity.repository.model.StationItem

interface StationsRepository {

    suspend fun fetchAllStations(): List<StationItem>

    suspend fun fetchStationDetailsById(stationId: Int, timeShift: Int): StationDetails?

    suspend fun fetchStationDetails(stationId: Int, page: Int): NetworkResult<List<StationDataResponse>>

    suspend fun saveStationDetails(stationDetails: StationDetails)

    fun getAllHistory(stationId: Int): PagingSource<Int, StationDetails>
}