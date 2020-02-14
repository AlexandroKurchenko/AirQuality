package com.okurchenko.ecocity.ui.main.fragments

import com.okurchenko.ecocity.repository.model.StationItem

sealed class StationsState {
    object Loading : StationsState()
    object Error : StationsState()
    class StationItemsContent(val data: List<StationItem>) : StationsState()
    class StationEvent(val event: Events) : StationsState()
}

sealed class Events {
    class OpenHistoryActivity(val id: Int) : Events()
    object OpenDetails : Events()
    object OpenMain : Events()
    object OpenHistory : Events()
    object Refresh : Events()
    //Maybe other events
}