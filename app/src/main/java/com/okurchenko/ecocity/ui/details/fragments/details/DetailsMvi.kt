package com.okurchenko.ecocity.ui.details.fragments.details

import com.okurchenko.ecocity.repository.model.StationDetails
import com.okurchenko.ecocity.ui.base.*

sealed class DetailsState : BaseState {
    class DetailsItemLoaded(val item: StationDetails) : DetailsState()
    object DetailsItemLoading : DetailsState()
    object FailLoading : DetailsState()
    object Empty : DetailsState()
}

class DetailsReducer : Reducer<DetailsState>() {
    override fun reduce(action: BaseAction, state: DetailsState): DetailsState {
        return when (action) {
            is DetailsAction.Loading -> DetailsState.DetailsItemLoading
            is DetailsAction.FailLoading -> DetailsState.FailLoading
            is DetailsAction.ItemLoaded -> DetailsState.DetailsItemLoaded(action.item)
            else -> state
        }
    }
}

sealed class DetailsViewAction : BaseViewAction {
    class FetchDetailsItem(val stationId: Int, val timeShift: Int) : DetailsViewAction()
    object HistoryClick : DetailsViewAction()
}

sealed class DetailsAction : BaseAction {
    object Loading : DetailsAction()
    object FailLoading : DetailsAction()
    class ItemLoaded(val item: StationDetails) : DetailsAction()
}

class DetailsActor(private val emit: (DetailsViewAction) -> Unit) {
    fun fetchDetails(stationId: Int, timeShift: Int) = emit(DetailsViewAction.FetchDetailsItem(stationId, timeShift))
    fun backToHistory() = emit(DetailsViewAction.HistoryClick)
}