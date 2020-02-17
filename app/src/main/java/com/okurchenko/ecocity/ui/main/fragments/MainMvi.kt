package com.okurchenko.ecocity.ui.main.fragments

import com.okurchenko.ecocity.repository.model.StationItem
import com.okurchenko.ecocity.ui.BaseViewAction

sealed class StationListState {
    object Loading : StationListState()
    object Error : StationListState()
    class StationItemsContent(val data: List<StationItem>) : StationListState()
    class StationEvent(val event: Events) : StationListState()
}

sealed class StationListViewAction:BaseViewAction {
    class StationItemClick(val id: Int) : StationListViewAction()
    object StationItemsRefresh : StationListViewAction()
}

class StationListActor(private val emit: (StationListViewAction) -> Unit) {
    fun clickItem(id: Int) = emit(StationListViewAction.StationItemClick(id))
    fun refresh() = emit(StationListViewAction.StationItemsRefresh)
}

sealed class Events {
    class OpenHistoryActivity(val id: Int) : Events()
    class OpenDetails(val timeShift: Int) : Events()
    object OpenMain : Events()
    object OpenHistory : Events()
}