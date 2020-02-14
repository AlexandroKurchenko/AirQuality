package com.okurchenko.ecocity.ui.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.okurchenko.ecocity.repository.StationRepositoryImpl
import com.okurchenko.ecocity.ui.BaseStore
import com.okurchenko.ecocity.ui.main.fragments.Events
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: StationRepositoryImpl) : ViewModel() {

    private val _state = MutableLiveData<StationHistoryState>()
    private val store: BaseStore<StationHistoryState> =
        BaseStore(StationHistoryState.HistoryItemLoading, HistoryReducer())

    init {
        store.subscribe(_state::postValue)
    }

    fun getState(): MutableLiveData<StationHistoryState> = _state

    fun takeAction(action: HistoryViewAction) {
        when (action) {
            is HistoryViewAction.HistoryFetch -> fetchStationsById(action.id, action.fromTimeShift, action.toTimeShift)
            is HistoryViewAction.DetailsClick -> handleDetailsClickAction()
            is HistoryViewAction.OpenMain -> handleOpenMainAction()
            is HistoryViewAction.HistoryClick -> handleHistoryClickAction()
        }
    }

    private fun fetchStationsById(id: Int, fromTimeShift: Int, toTimeShift: Int) {
        if (_state.value != StationHistoryState.HistoryItemLoading) {
            viewModelScope.launch(Dispatchers.IO) {
                store.dispatch(HistoryListAction.Loading)
                val data = repository.fetchStationHistoryItemsById(id, fromTimeShift, toTimeShift)
                if (data.isNotEmpty()) {
                    store.dispatch(HistoryListAction.ItemsLoaded(data))
                } else {
                    store.dispatch(HistoryListAction.FailLoading)
                }
            }
        }
    }

    private fun handleDetailsClickAction() =
        _state.postValue(StationHistoryState.StationDetailsStateEvent(Events.OpenDetails))

    private fun handleOpenMainAction() =
        _state.postValue(StationHistoryState.StationDetailsStateEvent(Events.OpenMain))

    private fun handleHistoryClickAction() =
        _state.postValue(StationHistoryState.StationDetailsStateEvent(Events.OpenHistory))

}