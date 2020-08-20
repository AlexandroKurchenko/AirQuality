package com.okurchenko.ecocity.ui.details.fragments.history

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.okurchenko.ecocity.network.model.StationDataResponse
import com.okurchenko.ecocity.repository.NetworkResult
import com.okurchenko.ecocity.repository.StationsRepository
import com.okurchenko.ecocity.repository.model.StationDetails
import com.okurchenko.ecocity.repository.utils.DetailsAggregator
import retrofit2.HttpException
import java.io.IOException

private const val GITHUB_STARTING_PAGE_INDEX = 0
private const val MAX_DISPLAY_ITEMS_COUNT = 48

@OptIn(ExperimentalPagingApi::class)
class HistoryPagingRemoteMediator(
    private val stationId: Int,
    private val repository: StationsRepository
) : RemoteMediator<Int, StationDetails>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, StationDetails>): MediatorResult {
        return try {
            val timeShift = when (loadType) {
                LoadType.REFRESH -> getRemoteKeyClosestToCurrentPosition(state)
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> getRemoteKeyForLastItem(state) ?: return MediatorResult.Success(
                    endOfPaginationReached = true
                )
            }
            val pageToLoad = if (isFirstTimeLoading(state)) timeShift else timeShift.plus(1)
            if (isLimitOfData(pageToLoad)) {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            val networkResponse = repository.fetchStationDetails(stationId, pageToLoad)
            return parseResponse(networkResponse, pageToLoad)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun parseResponse(
        networkResponse: NetworkResult<List<StationDataResponse>>,
        timePeriod: Int
    ): MediatorResult =
        when (networkResponse) {
            is NetworkResult.Success -> {
                processSuccessResponse(networkResponse.data, timePeriod)
            }
            is NetworkResult.Error -> {
                MediatorResult.Error(networkResponse.networkError)
            }
        }

    private suspend fun processSuccessResponse(
        networkResponseData: List<StationDataResponse>,
        timePeriod: Int
    ): MediatorResult {
        val stationDetails = DetailsAggregator.prepareStationDetail(
            networkResponseData,
            stationId,
            timePeriod
        )
        return stationDetails?.let {
            repository.saveStationDetails(it)
            MediatorResult.Success(endOfPaginationReached = false)
        } ?: kotlin.run {
            MediatorResult.Error(NullPointerException("FetchStationDataById=$stationId, for timeShift=$timePeriod, error stationDetails is empty"))
        }
    }

    private fun getRemoteKeyForLastItem(state: PagingState<Int, StationDetails>): Int? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.hoursAgo
    }

    private fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StationDetails>): Int {
        return state.anchorPosition ?: GITHUB_STARTING_PAGE_INDEX
    }

    /**
     * Api can store data only for 48 hours for particular station.
     * On site it will be called time machine
     */
    private fun isLimitOfData(pageToLoad: Int): Boolean = pageToLoad > MAX_DISPLAY_ITEMS_COUNT

    private fun isFirstTimeLoading(state: PagingState<Int, StationDetails>): Boolean =
        state.anchorPosition == null && state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull() == null
}

/**
 * LoadType.REFRESH -> {
 * Gets called when it's the first time we're loading data, or when PagingDataAdapter.refresh() is called;
 * so now the point of reference for loading our data is the state.anchorPosition.
 * If this is the first load, then the anchorPosition is null. When PagingDataAdapter.refresh() is called,
 * the anchorPosition is the first visible position in the displayed list, so we will need to load the page that contains that specific item.
 * }
 * LoadType.PREPEND -> {
 * When we need to load data at the beginning of the currently loaded data set
 * or
 * if user scroll to top
 * }
 * LoadType.APPEND -> {
 * When we need to load data at the end of the currently loaded data set
 * or
 * if user scroll to bottom
 * }
 */