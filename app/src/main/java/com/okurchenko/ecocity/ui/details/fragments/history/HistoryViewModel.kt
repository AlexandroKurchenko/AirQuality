package com.okurchenko.ecocity.ui.details.fragments.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.okurchenko.ecocity.repository.model.StationHistoryItem
import com.okurchenko.ecocity.repository.utils.DetailsToPreviewConverter
import com.okurchenko.ecocity.ui.base.BaseViewAction
import com.okurchenko.ecocity.ui.base.BaseViewModel
import com.okurchenko.ecocity.ui.base.NavigationEvents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val ID_KEY = "ID_KEY"
private const val NETWORK_PAGE_SIZE = 4

class HistoryViewModel(private val savedStateHandle: SavedStateHandle) : BaseViewModel() {

    private lateinit var historyData: Flow<PagingData<StationHistoryItem>>

    // NOTE paging source factory, remoteMediator must all use same type(in my case it was StationDetails)
    fun getAllHistoryItems(queryStationId: Int): Flow<PagingData<StationHistoryItem>> {
        val lastStationId = savedStateHandle.get<Int>(ID_KEY)
        if (lastStationId != null && queryStationId == lastStationId && ::historyData.isInitialized) {
            return historyData
        }
        historyData = Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = true),
            remoteMediator = HistoryPagingRemoteMediator(queryStationId, repository),
            pagingSourceFactory = { repository.getAllHistory(queryStationId) }
        )
            .flow
            .map { historyDetails -> historyDetails.map { DetailsToPreviewConverter.convertData(it) } }
            .cachedIn(viewModelScope)
        savedStateHandle.set(ID_KEY, queryStationId)
        return historyData
    }

    override fun takeAction(action: BaseViewAction) {
        when (val historyAction = action as HistoryListViewAction) {
            is HistoryListViewAction.DetailsClick ->
                processNavigationEvent(NavigationEvents.OpenDetailsFragment(historyAction.timeShift))
            is HistoryListViewAction.OpenMain -> processNavigationEvent(NavigationEvents.OpenMainActivity)
        }
    }
}