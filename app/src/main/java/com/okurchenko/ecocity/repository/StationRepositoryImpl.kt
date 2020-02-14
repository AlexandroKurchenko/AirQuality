package com.okurchenko.ecocity.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.okurchenko.ecocity.network.StationApi
import com.okurchenko.ecocity.network.model.StationDataResponse
import com.okurchenko.ecocity.network.model.StationResponse
import com.okurchenko.ecocity.repository.db.DataBaseManager
import com.okurchenko.ecocity.repository.model.StationDetails
import com.okurchenko.ecocity.repository.model.StationHistoryItem
import com.okurchenko.ecocity.repository.model.StationItem
import com.okurchenko.ecocity.repository.utils.DetailsAggregator
import com.okurchenko.ecocity.repository.utils.DetailsToPreviewConverter
import com.okurchenko.ecocity.repository.utils.StationItemAggregator
import com.okurchenko.ecocity.utils.diffTimeInMinutes
import com.okurchenko.ecocity.utils.getNowTime
import timber.log.Timber

private const val FETCH_ALL_STATIONS_SYNC_TIME = "FETCH_ALL_STATIONS_SYNC_TIME"
private const val REFRESH_TIME = 10

class StationRepositoryImpl(
    private val api: StationApi,
    private val dataBaseManager: DataBaseManager,
    private val preferences: SharedPreferences
) : BaseNetworkRepository(), StationsRepository {

    override suspend fun fetchStationHistoryItemsById(
        stationId: Int,
        fromTimeShift: Int,
        toTimeShift: Int
    ): List<StationHistoryItem> {
        val periodOfHistory = mutableListOf<StationHistoryItem>()
        for (timeShift in fromTimeShift..toTimeShift) {
            getHistoryForTimePeriod(timeShift, stationId)?.let {
                val historyItem = DetailsToPreviewConverter.convertData(it)
                periodOfHistory.add(historyItem)
            }
        }
        return periodOfHistory
    }

    override suspend fun fetchStationDetailsById(
        stationId: Int,
        fromTimeShift: Int,
        toTimeShift: Int
    ): MutableList<StationDetails> {
        val periodOfHistory = mutableListOf<StationDetails>()
        for (timeShift in fromTimeShift..toTimeShift) {
            getHistoryForTimePeriod(timeShift, stationId)?.let { periodOfHistory.add(it) }
        }
        return periodOfHistory
    }

    override suspend fun fetchAllStations(): List<StationItem> {
        if (isMoreThanRefreshTime(preferences.getLong(FETCH_ALL_STATIONS_SYNC_TIME, 0))) {
            val networkResponse = safeApiCall { api.fetchAllStations().await() }
            processAllStationsFromNetwork(networkResponse)
        }
        return dataBaseManager.getAllStationItems()
    }

    private fun processAllStationsFromNetwork(networkResponse: NetworkResult<List<StationResponse>>) {
        when (networkResponse) {
            is NetworkResult.Success -> {
                val stationItems = StationItemAggregator.convertStationResponseToInstance(networkResponse.data)
                dataBaseManager.storeNewStationItems(stationItems)
                saveAllStationsSyncTime()
            }
            is NetworkResult.Error -> Timber.e("fetchAllStations error ${networkResponse.networkError}")
        }
    }

    private suspend fun getHistoryForTimePeriod(timeShift: Int, stationId: Int): StationDetails? {
        val dbHistory = dataBaseManager.getAllHistory(stationId, timeShift)
        return if (dbHistory != null && isMoreThanRefreshTime(dbHistory.timeToSave)) {
            dbHistory
        } else {
            val networkResponse = safeApiCall { api.fetchStationDataById(stationId, timeShift).await() }
            processStationDetailsFromNetwork(networkResponse, stationId, timeShift)
        }
    }

    private fun processStationDetailsFromNetwork(
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

    private fun isMoreThanRefreshTime(time: Long): Boolean {
        return time.diffTimeInMinutes() >= REFRESH_TIME
    }

    private fun saveAllStationsSyncTime() {
        preferences.edit { putLong(FETCH_ALL_STATIONS_SYNC_TIME, getNowTime()) }
    }
}