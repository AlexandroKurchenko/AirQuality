package com.okurchenko.ecocity.ui.main

import androidx.lifecycle.viewModelScope
import com.okurchenko.ecocity.repository.StationRepositoryImpl
import com.okurchenko.ecocity.ui.BaseViewAction
import com.okurchenko.ecocity.ui.BaseViewModel
import com.okurchenko.ecocity.ui.main.fragments.Events
import com.okurchenko.ecocity.ui.main.fragments.StationListState
import com.okurchenko.ecocity.ui.main.fragments.StationListViewAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(private val repository: StationRepositoryImpl) : BaseViewModel<StationListState>() {

    override fun takeAction(action: BaseViewAction) {
        when (val stationsViewAction = action as StationListViewAction) {
            is StationListViewAction.StationItemsRefresh -> if (viewState.value != StationListState.Loading)
                handleRefreshAction()
            is StationListViewAction.StationItemClick -> handleItemClickAction(stationsViewAction.id)
        }
    }

    private fun handleItemClickAction(id: Int) {
        viewState.postValue(StationListState.StationEvent(Events.OpenHistoryActivity(id)))
    }

    private fun handleRefreshAction() {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("Start fetch data")
            viewState.postValue(StationListState.Loading)
            val result = repository.fetchAllStations()
            if (result.isNotEmpty()) {
                viewState.postValue(StationListState.StationItemsContent(repository.fetchAllStations()))
            } else {
                viewState.postValue(StationListState.Error)
            }
        }
    }

}