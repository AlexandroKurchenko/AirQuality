package com.okurchenko.ecocity.ui.main

import android.location.Location
import com.okurchenko.ecocity.repository.model.StationItem
import com.okurchenko.ecocity.ui.base.BaseAction
import com.okurchenko.ecocity.ui.base.BaseState
import com.okurchenko.ecocity.ui.base.BaseViewAction
import com.okurchenko.ecocity.ui.base.Reducer
import com.okurchenko.ecocity.ui.main.fragments.stations.StationSort

sealed class StationListState : BaseState {
    object Loading : StationListState()
    object Error : StationListState()
    class StationItemsLoaded(val data: List<StationItem>, val sort: StationSort) : StationListState()
    class LocationUpdated(val location: Location) : StationListState()
    object Empty : StationListState()
}

class StationListReducer : Reducer<StationListState>() {
    override fun reduce(action: BaseAction, state: StationListState): StationListState {
        return when (action) {
            is StationListAction.Loading -> StationListState.Loading
            is StationListAction.FailLoading -> StationListState.Error
            is StationListAction.ItemLoaded -> StationListState.StationItemsLoaded(action.items, action.sort)
            is StationListAction.LocationUpdate -> StationListState.LocationUpdated(action.location)
            else -> state
        }
    }
}

sealed class StationListViewAction : BaseViewAction {
    class StationItemClick(val id: Int) : StationListViewAction()
    class StationItemsRefresh(val sorting: StationSort) : StationListViewAction()
    object StationProvideLocationUpdate : StationListViewAction()
    object StationStopProvideLocationUpdate : StationListViewAction()
    object StationSortByDistance : StationListViewAction()
    object StationSortByName : StationListViewAction()
}

sealed class StationListAction : BaseAction {
    object Loading : StationListAction()
    object FailLoading : StationListAction()
    class ItemLoaded(val items: List<StationItem>, val sort: StationSort) : StationListAction()
    class LocationUpdate(val location: Location) : StationListAction()
}

class StationListActor(private val emit: (StationListViewAction) -> Unit) {
    fun clickItem(id: Int) = emit(StationListViewAction.StationItemClick(id))
    fun requestLocationUpdate() = emit(StationListViewAction.StationProvideLocationUpdate)
    fun stopLocationUpdate() = emit(StationListViewAction.StationStopProvideLocationUpdate)
    fun refresh(sorting: StationSort) = emit(StationListViewAction.StationItemsRefresh(sorting))
    fun sortByName() = emit(StationListViewAction.StationSortByName)
    fun sortByDistance() = emit(StationListViewAction.StationSortByDistance)
}

sealed class MainSortingState {
    object CurrentLocationIsNotAvailable : MainSortingState()
    object SortingByDistanceSuccess : MainSortingState()
    object SortingByNameSuccess : MainSortingState()
}