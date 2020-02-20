package com.okurchenko.ecocity.ui.main

import androidx.lifecycle.viewModelScope
import com.okurchenko.ecocity.repository.StationRepositoryImpl
import com.okurchenko.ecocity.repository.model.StationItem
import com.okurchenko.ecocity.ui.base.BaseStore
import com.okurchenko.ecocity.ui.base.BaseViewAction
import com.okurchenko.ecocity.ui.base.BaseViewModel
import com.okurchenko.ecocity.ui.base.NavigationEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel : BaseViewModel<StationListState>() {

    private val store: BaseStore<StationListState> = BaseStore(
        StationListState.Empty,
        StationListReducer()
    )

    init {
        store.subscribe(viewState::postValue)
        handleRefreshAction()
    }

    override fun takeAction(action: BaseViewAction) {
        when (val stationsViewAction = action as StationListViewAction) {
            is StationListViewAction.StationItemsRefresh -> handleRefreshAction()
            is StationListViewAction.StationItemClick -> handleItemClickAction(stationsViewAction.id)
        }
    }

    private fun handleItemClickAction(id: Int) {
        processState(StationListState.StationEvent(NavigationEvents.OpenHistoryActivity(id)))
    }

    private fun handleRefreshAction() {
        if (viewState.value != StationListState.Loading) {
            store.dispatch(StationListAction.Loading)
            viewModelScope.launch(Dispatchers.IO) {
                val stationItems = repository.fetchAllStations()
                displayResults(stationItems)
            }
        }
    }

    private fun displayResults(result: List<StationItem>) {
        if (result.isNotEmpty()) {
            store.dispatch(StationListAction.ItemLoaded(result))
        } else {
            store.dispatch(StationListAction.FailLoading)
        }
    }
}