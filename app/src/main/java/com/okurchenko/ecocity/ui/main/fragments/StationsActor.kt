package com.okurchenko.ecocity.ui.main.fragments

class StationsActor(private val emit: (StationAction) -> Unit) {
    fun clickItem(id: Int) = emit(StationAction.StationItemClick(id))
    fun refresh() = emit(StationAction.StationItemsRefresh)
}