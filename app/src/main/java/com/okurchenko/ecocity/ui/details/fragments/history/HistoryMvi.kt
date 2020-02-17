package com.okurchenko.ecocity.ui.details.fragments.history

import com.okurchenko.ecocity.repository.model.StationHistoryItem
import com.okurchenko.ecocity.ui.base.BaseAction
import com.okurchenko.ecocity.ui.base.BaseState
import com.okurchenko.ecocity.ui.base.BaseViewAction
import com.okurchenko.ecocity.ui.base.Reducer

sealed class HistoryListState : BaseState {
    class HistoryItemLoaded(val items: List<StationHistoryItem>) : HistoryListState()
    object HistoryItemLoading : HistoryListState()
    object FailLoading : HistoryListState()
    object Empty : HistoryListState()
}

class HistoryListReducer : Reducer<HistoryListState>() {
    override fun reduce(action: BaseAction, state: HistoryListState): HistoryListState =
        when (action) {
            is HistoryListAction.Loading -> HistoryListState.HistoryItemLoading
            is HistoryListAction.FailLoading -> HistoryListState.FailLoading
            is HistoryListAction.ItemsLoaded -> HistoryListState.HistoryItemLoaded(action.items)
            else -> state
        }
}

sealed class HistoryListViewAction : BaseViewAction {
    class FetchHistoryList(val id: Int, val fromTimeShift: Int, val toTimeShift: Int) : HistoryListViewAction()
    class DetailsClick(val timeShift: Int) : HistoryListViewAction()
    object OpenMain : HistoryListViewAction()
}

sealed class HistoryListAction : BaseAction {
    object Loading : HistoryListAction()
    object FailLoading : HistoryListAction()
    class ItemsLoaded(val items: List<StationHistoryItem>) : HistoryListAction()
}

class HistoryListActor(private val emit: (HistoryListViewAction) -> Unit) {
    fun fetchHistoryData(id: Int, fromTimeShift: Int, toTimeShift: Int) =
        emit(HistoryListViewAction.FetchHistoryList(id, fromTimeShift, toTimeShift))

    fun clickItem(timeShift: Int) = emit(HistoryListViewAction.DetailsClick(timeShift))
    fun openMain() = emit(HistoryListViewAction.OpenMain)
}
