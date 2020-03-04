package com.okurchenko.ecocity.repository

import android.location.Location
import com.okurchenko.ecocity.network.StationApi
import com.okurchenko.ecocity.network.model.StationDataResponse
import com.okurchenko.ecocity.network.model.StationResponse
import com.okurchenko.ecocity.repository.db.DataBaseManager
import com.okurchenko.ecocity.repository.model.StationDetails
import com.okurchenko.ecocity.repository.model.StationHistoryItem
import com.okurchenko.ecocity.repository.model.StationItem
import com.okurchenko.ecocity.repository.utils.*
import com.okurchenko.ecocity.ui.main.fragments.stations.StationSort
import com.okurchenko.ecocity.utils.diffTimeInMinutes
import com.okurchenko.ecocity.utils.round
import timber.log.Timber

class StationRepositoryImpl(
    private val api: StationApi,
    private val dataBaseManager: DataBaseManager,
    private val preferencesManager: StationPreferencesManager
) : BaseNetworkRepository(), StationsRepository {

    override suspend fun fetchStationDetailsById(stationId: Int, timeShift: Int): StationDetails? =
        getHistoryForTimePeriod(timeShift, stationId)

    override suspend fun getAllStationSortedByName(): List<StationItem> {
        val stations: List<StationItem> = getAllStations()
        return dataBaseManager.sortAllStations(stations, StationSort.SortByName)
    }

    override suspend fun getAllStationSortedByDistance(location: Location): List<StationItem> {
        val stations: List<StationItem> = getAllStations()
        val isStationsContainsDistance: Boolean = isItemsDistanceValid(stations)
        val isLocationSame: Boolean = compareLocations(location)
        val stationsToSort = if (isStationsContainsDistance && isLocationSame) {
            stations
        } else {
            val sortedItems: List<StationItem> = dataBaseManager.updateItemsWithDistance(location, stations)
            preferencesManager.saveCurrentLocation(location)
            sortedItems
        }
        return dataBaseManager.sortAllStations(stationsToSort, StationSort.SortByDistance)
    }

    private fun isItemsDistanceValid(stations: List<StationItem>): Boolean = stations.any { it.distance != 0.0 }

    private suspend fun getAllStations(): List<StationItem> {
        val dbItems = dataBaseManager.getAllStationItems()
        return if (dbItems.isNotEmpty() && preferencesManager.isLastFetchFresh()) {
            dbItems
        } else {
            val networkResponse = safeApiCall { api.fetchAllStationsAsync().await() }
            processAllStationsNetworkResponse(networkResponse)
        }
    }

    private fun processAllStationsNetworkResponse(networkResponse: NetworkResult<List<StationResponse>>)
        : List<StationItem> {
        when (networkResponse) {
            is NetworkResult.Success -> {
                val stationItems = StationItemAggregator.convertStationResponseToInstance(networkResponse.data)
                dataBaseManager.storeNewStationItems(stationItems)
                preferencesManager.saveAllStationsSyncTime()
                return stationItems
            }
            is NetworkResult.Error -> Timber.e("fetchAllStations error ${networkResponse.networkError}")
        }
        return emptyList()
    }

    private fun compareLocations(location: Location): Boolean =
        round(location.latitude, 2).toFloat() == preferencesManager.getCurrentLat()
            && round(location.longitude, 2).toFloat() == preferencesManager.getCurrentLon()


    override suspend fun fetchHistoryItemsByStationId(
        stationId: Int,
        fromTimeShift: Int,
        toTimeShift: Int
    ): List<StationHistoryItem> {
        val periodOfHistory = mutableListOf<StationHistoryItem>()
        for (timeShift in fromTimeShift..toTimeShift) {
            getHistoryForTimePeriod(timeShift, stationId)?.let { stationDetails ->
                val historyItem = DetailsToPreviewConverter.convertData(stationDetails)
                periodOfHistory.add(historyItem)
            }
        }
        return periodOfHistory
    }

    private suspend fun getHistoryForTimePeriod(timePeriod: Int, stationId: Int): StationDetails? {
        val dbHistory = dataBaseManager.getAllHistory(stationId, timePeriod)
        return if (dbHistory != null && dbHistory.timeToSave.diffTimeInMinutes() < REFRESH_TIME) {
            dbHistory
        } else {
            val networkResponse = safeApiCall { api.fetchStationDataByIdAsync(stationId, timePeriod).await() }
            processHistoryDetailsNetworkResponse(networkResponse, stationId, timePeriod)
        }
    }

    private fun processHistoryDetailsNetworkResponse(
        networkResponse: NetworkResult<List<StationDataResponse>>,
        id: Int,
        timeShift: Int
    ): StationDetails? {
        when (networkResponse) {
            is NetworkResult.Success -> {
                val stationDetails = DetailsAggregator.prepareStationDetail(networkResponse.data, id, timeShift)
                if (stationDetails != null) {
                    dataBaseManager.saveStationDetailsResult(stationDetails)
                    return stationDetails
                } else {
                    Timber.e("fetchStationDataById=$id, for timeShift=$timeShift, error stationDetails is empty")
                }
            }
            is NetworkResult.Error -> Timber.e("fetchStationDataById=$id, for timeShift=$timeShift, error ${networkResponse.networkError}")
        }
        return null
    }
}