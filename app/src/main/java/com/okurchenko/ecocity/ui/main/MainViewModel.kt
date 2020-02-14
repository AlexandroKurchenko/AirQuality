package com.okurchenko.ecocity.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.okurchenko.ecocity.repository.StationRepositoryImpl
import com.okurchenko.ecocity.ui.main.fragments.Events
import com.okurchenko.ecocity.ui.main.fragments.StationAction
import com.okurchenko.ecocity.ui.main.fragments.StationsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(private val repository: StationRepositoryImpl) : ViewModel() {

    private val _state = MutableLiveData<StationsState>()

    fun getState(): MutableLiveData<StationsState> = _state

    fun takeAction(action: StationAction) {
        when (action) {
            is StationAction.StationItemsRefresh -> handleRefreshAction()
            is StationAction.StationItemClick -> handleItemClickAction(action.id)
        }
    }

    private fun handleItemClickAction(id: Int) {
        _state.postValue(StationsState.StationEvent(Events.OpenHistoryActivity(id)))
    }

    private fun handleRefreshAction() {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("Start fetch data")
            _state.postValue(StationsState.Loading)
            _state.postValue(StationsState.StationItemsContent(repository.fetchAllStations()))
        }
    }


}