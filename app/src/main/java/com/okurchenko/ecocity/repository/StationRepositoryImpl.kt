package com.okurchenko.ecocity.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.paging.PagingSource
import com.okurchenko.ecocity.network.StationApi
import com.okurchenko.ecocity.network.model.StationDataResponse
import com.okurchenko.ecocity.network.model.StationResponse
import com.okurchenko.ecocity.repository.db.DataBaseManager
import com.okurchenko.ecocity.repository.model.StationDetails
import com.okurchenko.ecocity.repository.model.StationItem
import com.okurchenko.ecocity.repository.utils.DetailsAggregator
import com.okurchenko.ecocity.repository.utils.StationItemAggregator
import com.okurchenko.ecocity.utils.diffTimeInMinutes
import com.okurchenko.ecocity.utils.getNowTime
import timber.log.Timber

private const val FETCH_ALL_STATIONS_SYNC_TIME = "FETCH_ALL_STATIONS_SYNC_TIME"
private const val REFRESH_TIME = 30//min

class StationRepositoryImpl(
    private val api: StationApi,
    private val dataBaseManager: DataBaseManager,
    private val preferences: SharedPreferences
) : BaseNetworkRepository(), StationsRepository {

    override suspend fun fetchStationDetailsById(stationId: Int, timeShift: Int): StationDetails? {
        val dbHistory = dataBaseManager.getAllHistory(stationId, timeShift)
        return if (dbHistory != null && dbHistory.timeToSave.diffTimeInMinutes() < REFRESH_TIME) {
            dbHistory
        } else {
            val networkResponse = safeApiCall { api.fetchStationDataByIdAsync(stationId, timeShift).await() }
            processHistoryDetailsNetworkResponse(networkResponse, stationId, timeShift)
        }
    }

    override suspend fun fetchAllStations(): List<StationItem> {
        val dbItems = dataBaseManager.getAllStationItems()
        val lastFetchTime = preferences.getLong(FETCH_ALL_STATIONS_SYNC_TIME, 0)
        return if (dbItems.isNotEmpty() && lastFetchTime.diffTimeInMinutes() < REFRESH_TIME) {
            dataBaseManager.sortAllStation(dbItems)
        } else {
            val networkResponse = safeApiCall { api.fetchAllStationsAsync().await() }
            processAllStationsNetworkResponse(networkResponse)
        }
    }

    override suspend fun fetchStationDetails(stationId: Int, page: Int): NetworkResult<List<StationDataResponse>> =
        safeApiCall { api.fetchStationDataByIdAsync(stationId, page).await() }

    override suspend fun saveStationDetails(stationDetails: StationDetails) {
        dataBaseManager.loadInTransactionContext {
            dataBaseManager.saveStationDetailsResult(stationDetails)
        }
    }

    override fun getAllHistory(stationId: Int): PagingSource<Int, StationDetails> =
        dataBaseManager.getAllHistoryPaging(stationId)


    private fun processAllStationsNetworkResponse(networkResponse: NetworkResult<List<StationResponse>>): List<StationItem> {
        when (networkResponse) {
            is NetworkResult.Success -> {
                val stationItems = StationItemAggregator.convertStationResponseToInstance(networkResponse.data)
                dataBaseManager.storeNewStationItems(stationItems)
                saveAllStationsSyncTime()
                return dataBaseManager.sortAllStation(stationItems)
            }
            is NetworkResult.Error -> Timber.e("fetchAllStations error ${networkResponse.networkError}")
        }
        return emptyList()
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

    private fun saveAllStationsSyncTime() = preferences.edit { putLong(FETCH_ALL_STATIONS_SYNC_TIME, getNowTime()) }
}