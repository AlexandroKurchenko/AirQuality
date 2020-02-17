package com.okurchenko.ecocity.ui.details.fragments.history

import androidx.lifecycle.viewModelScope
import com.okurchenko.ecocity.repository.StationRepositoryImpl
import com.okurchenko.ecocity.repository.model.StationHistoryItem
import com.okurchenko.ecocity.ui.BaseStore
import com.okurchenko.ecocity.ui.BaseViewAction
import com.okurchenko.ecocity.ui.BaseViewModel
import com.okurchenko.ecocity.ui.main.fragments.Events
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: StationRepositoryImpl) : BaseViewModel<HistoryListState>() {

    private val store: BaseStore<HistoryListState> =
        BaseStore(HistoryListState.HistoryItemLoading, HistoryListReducer())

    init {
        store.subscribe(viewState::postValue)
    }

    override fun takeAction(action: BaseViewAction) {
        when (val historyAction = action as HistoryListViewAction) {
            is HistoryListViewAction.FetchHistoryList -> fetchHistoryList(
                historyAction.id,
                historyAction.fromTimeShift,
                historyAction.toTimeShift
            )
            is HistoryListViewAction.DetailsClick -> handleDetailsClickAction(historyAction.timeShift)
            is HistoryListViewAction.OpenMain -> handleOpenMainAction()
        }
    }

    private fun fetchHistoryList(id: Int, fromTimeShift: Int, toTimeShift: Int) {
        if (viewState.value != HistoryListState.HistoryItemLoading) {
            viewModelScope.launch(Dispatchers.IO) {
                store.dispatch(HistoryListAction.Loading)
                val items = repository.fetchStationHistoryItemsById(id, fromTimeShift, toTimeShift)
                displayHistoryListResult(items)
            }
        }
    }

    private fun displayHistoryListResult(items: List<StationHistoryItem>) {
        if (items.isNotEmpty()) {
            store.dispatch(HistoryListAction.ItemsLoaded(items))
        } else {
            store.dispatch(HistoryListAction.FailLoading)
        }
    }

    private fun handleDetailsClickAction(timeShift: Int) =
        viewState.postValue(HistoryListState.StationDetailsStateEvent(Events.OpenDetails(timeShift)))

    private fun handleOpenMainAction() =
        viewState.postValue(HistoryListState.StationDetailsStateEvent(Events.OpenMain))

}