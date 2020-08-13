package com.okurchenko.ecocity.ui.details.fragments.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.okurchenko.ecocity.repository.model.StationHistoryItem
import com.okurchenko.ecocity.ui.base.BaseStore
import com.okurchenko.ecocity.ui.base.BaseViewAction
import com.okurchenko.ecocity.ui.base.NavigationEvents
import com.okurchenko.ecocity.ui.base.ViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryViewModel : ViewModelState<HistoryListState>() {

    private val tempHistoryItems = MutableLiveData<MutableSet<StationHistoryItem>>(mutableSetOf())
    private val store: BaseStore<HistoryListState> = BaseStore(HistoryListState.Empty, HistoryListReducer())
        .also { it.subscribe(viewState::postValue) }

    override fun takeAction(action: BaseViewAction) {
        when (val historyAction = action as HistoryListViewAction) {
            is HistoryListViewAction.FetchHistoryList ->
                fetchHistoryList(historyAction.id, historyAction.fromTimeShift, historyAction.toTimeShift)
            is HistoryListViewAction.DetailsClick ->
                processNavigationEvent(NavigationEvents.OpenDetailsFragment(historyAction.timeShift))
            is HistoryListViewAction.OpenMain -> processNavigationEvent(NavigationEvents.OpenMainActivity)
        }
    }

    private fun fetchHistoryList(id: Int, fromTimeShift: Int, toTimeShift: Int) {
        if (viewState.value != HistoryListState.HistoryItemLoading) {
            viewModelScope.launch {
                store.dispatch(HistoryListAction.Loading)
                val items = withContext(Dispatchers.IO) {
                    repository.fetchHistoryItemsByStationId(
                        id,
                        fromTimeShift,
                        toTimeShift
                    )
                }
                displayHistoryListResult(items)
            }
        }
    }

    private fun displayHistoryListResult(items: List<StationHistoryItem>) {
        if (items.isNotEmpty()) {
            getAllHistoryItems(items)?.let { store.dispatch(HistoryListAction.ItemsLoaded(it)) }
        } else {
            store.dispatch(HistoryListAction.FailLoading)
        }
    }

    private fun getAllHistoryItems(items: List<StationHistoryItem>): List<StationHistoryItem>? {
        val allItems = tempHistoryItems.value
        allItems?.addAll(items)
        return allItems?.toList()
    }
}