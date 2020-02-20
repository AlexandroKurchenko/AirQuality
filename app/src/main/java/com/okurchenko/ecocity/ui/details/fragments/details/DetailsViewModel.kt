package com.okurchenko.ecocity.ui.details.fragments.details

import androidx.lifecycle.viewModelScope
import com.okurchenko.ecocity.repository.model.StationDetails
import com.okurchenko.ecocity.ui.base.BaseStore
import com.okurchenko.ecocity.ui.base.BaseViewAction
import com.okurchenko.ecocity.ui.base.BaseViewModel
import com.okurchenko.ecocity.ui.base.NavigationEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsViewModel : BaseViewModel<DetailsState>() {

    private val store: BaseStore<DetailsState> = BaseStore(DetailsState.Empty, DetailsReducer()).also {
        it.subscribe(viewState::postValue)
    }

    override fun takeAction(action: BaseViewAction) {
        when (val detailsAction = action as DetailsViewAction) {
            is DetailsViewAction.FetchDetailsItem ->
                fetchHistoryDetails(detailsAction.stationId, detailsAction.timeShift)
            is DetailsViewAction.HistoryClick -> handBackToHistoryClickAction()
        }
    }

    private fun handBackToHistoryClickAction() {
        val event = DetailsState.DetailsNavigation(NavigationEvents.OpenHistoryFragment)
        processState(event)
    }

    private fun fetchHistoryDetails(stationId: Int, timeShift: Int) {
        if (viewState.value != DetailsState.DetailsItemLoading) {
            viewModelScope.launch(Dispatchers.IO) {
                store.dispatch(DetailsAction.Loading)
                val stationDetails = repository.fetchStationDetailsById(stationId, timeShift)
                displayStationDetails(stationDetails)
            }
        }
    }

    private fun displayStationDetails(stationDetails: StationDetails?) {
        if (stationDetails != null) {
            store.dispatch(DetailsAction.ItemLoaded(stationDetails))
        } else {
            store.dispatch(DetailsAction.FailLoading)
        }
    }
}