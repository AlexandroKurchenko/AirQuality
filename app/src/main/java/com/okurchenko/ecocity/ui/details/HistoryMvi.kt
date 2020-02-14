package com.okurchenko.ecocity.ui.details

import com.okurchenko.ecocity.repository.model.StationHistoryItem
import com.okurchenko.ecocity.ui.BaseAction
import com.okurchenko.ecocity.ui.BaseState
import com.okurchenko.ecocity.ui.Reducer
import com.okurchenko.ecocity.ui.main.fragments.Events

sealed class StationHistoryState : BaseState {
    class HistoryItemLoaded(val items: List<StationHistoryItem>) : StationHistoryState()
    object HistoryItemLoading : StationHistoryState()
    object FailLoading : StationHistoryState()
    class StationDetailsStateEvent(val event: Events) : StationHistoryState()
}

sealed class HistoryViewAction {
    class HistoryFetch(val id: Int, val fromTimeShift: Int, val toTimeShift: Int) : HistoryViewAction()
    object DetailsClick : HistoryViewAction()
    object HistoryClick : HistoryViewAction()
    object OpenMain : HistoryViewAction()
}

class HistoryReducer : Reducer<StationHistoryState>() {
    override fun reduce(action: BaseAction, state: StationHistoryState): StationHistoryState {
        return when (action) {
            is HistoryListAction.Loading -> StationHistoryState.HistoryItemLoading
            is HistoryListAction.FailLoading -> StationHistoryState.FailLoading
            is HistoryListAction.ItemsLoaded -> StationHistoryState.HistoryItemLoaded(action.items)
            else -> state
        }
    }
}

sealed class HistoryListAction : BaseAction {
    object Loading : HistoryListAction()
    object FailLoading : HistoryListAction()
    class ItemsLoaded(val items: List<StationHistoryItem>) : HistoryListAction()
}

class HistoryActor(private val emit: (HistoryViewAction) -> Unit) {
    fun fetchHistoryData(id: Int, fromTimeShift: Int, toTimeShift: Int) =
        emit(HistoryViewAction.HistoryFetch(id, fromTimeShift, toTimeShift))
    fun clickItem() = emit(HistoryViewAction.DetailsClick)
    fun openMain() = emit(HistoryViewAction.OpenMain)
    fun openHistory()= emit(HistoryViewAction.HistoryClick)
}