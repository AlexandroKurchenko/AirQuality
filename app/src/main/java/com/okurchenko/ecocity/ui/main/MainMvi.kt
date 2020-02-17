package com.okurchenko.ecocity.ui.main

import com.okurchenko.ecocity.repository.model.StationItem
import com.okurchenko.ecocity.ui.base.BaseAction
import com.okurchenko.ecocity.ui.base.BaseState
import com.okurchenko.ecocity.ui.base.BaseViewAction
import com.okurchenko.ecocity.ui.base.Reducer

sealed class StationListState : BaseState {
    object Loading : StationListState()
    object Error : StationListState()
    class StationItemsLoaded(val data: List<StationItem>) : StationListState()
    object Empty : StationListState()
}

class StationListReducer : Reducer<StationListState>() {
    override fun reduce(action: BaseAction, state: StationListState): StationListState {
        return when (action) {
            is StationListAction.Loading -> StationListState.Loading
            is StationListAction.FailLoading -> StationListState.Error
            is StationListAction.ItemLoaded -> StationListState.StationItemsLoaded(
                action.items
            )
            else -> state
        }
    }
}

sealed class StationListViewAction : BaseViewAction {
    class StationItemClick(val id: Int) : StationListViewAction()
    object StationItemsRefresh : StationListViewAction()
}

sealed class StationListAction : BaseAction {
    object Loading : StationListAction()
    object FailLoading : StationListAction()
    class ItemLoaded(val items: List<StationItem>) : StationListAction()
}

class StationListActor(private val emit: (StationListViewAction) -> Unit) {
    fun clickItem(id: Int) = emit(
        StationListViewAction.StationItemClick(
            id
        )
    )

    fun refresh() = emit(StationListViewAction.StationItemsRefresh)
}

