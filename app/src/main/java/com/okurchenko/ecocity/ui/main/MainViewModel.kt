package com.okurchenko.ecocity.ui.main

import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import com.okurchenko.ecocity.repository.LocationListener
import com.okurchenko.ecocity.repository.model.StationItem
import com.okurchenko.ecocity.ui.base.BaseStore
import com.okurchenko.ecocity.ui.base.BaseViewAction
import com.okurchenko.ecocity.ui.base.BaseViewModel
import com.okurchenko.ecocity.ui.base.NavigationEvents
import com.okurchenko.ecocity.ui.main.fragments.stations.StationSort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

open class MainViewModel : BaseViewModel<StationListState>() {

    protected val location: LocationListener by lazy { GlobalContext.get().koin.get<LocationListener>() }
    private val store: BaseStore<StationListState> = BaseStore(StationListState.Empty, StationListReducer())
    private val sortingState = LiveEvent<MainSortingState>()

    init {
        store.subscribe(viewState::postValue)
        sortByName()
    }

    override fun takeAction(action: BaseViewAction) {
        when (val stationsViewAction = action as StationListViewAction) {
            is StationListViewAction.StationItemsRefresh -> handleRefreshAction(stationsViewAction.sorting)
            is StationListViewAction.StationItemClick ->
                processNavigationEvent(NavigationEvents.OpenHistoryActivity(stationsViewAction.id))
            is StationListViewAction.StationSortByDistance -> sortByDistance()
            is StationListViewAction.StationSortByName -> sortByName()
        }
    }

    private fun sortByName() {
        if (viewState.value != StationListState.Loading) {
            store.dispatch(StationListAction.Loading)
            viewModelScope.launch(Dispatchers.Default) {
                val stationItems = repository.getAllStationSortedByName()
                displayResults(stationItems, StationSort.SortByName)
            }
        }
    }

    private fun sortByDistance() {
        if (viewState.value != StationListState.Loading) {
            store.dispatch(StationListAction.Loading)
            viewModelScope.launch(Dispatchers.Default) {
                val location = location.getLastLocation()
                location?.run {
                    val stationItems = repository.getAllStationSortedByDistance(location)
                    displayResults(stationItems, StationSort.SortByDistance)
                } ?: processSortingState(MainSortingState.CurrentLocationIsNotAvailable)
            }
        }
    }

    private fun handleRefreshAction(sorting: StationSort) {
        when (sorting) {
            StationSort.SortByName -> sortByName()
            else -> sortByDistance()
        }
    }

    private fun displayResults(
        result: List<StationItem>,
        sort: StationSort
    ) {
        if (result.isNotEmpty()) {
            store.dispatch(StationListAction.ItemLoaded(result, sort))
        } else {
            store.dispatch(StationListAction.FailLoading)
        }
    }

    private fun processSortingState(state: MainSortingState) {
        sortingState.postValue(state)
    }
}