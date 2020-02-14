package com.okurchenko.ecocity.ui.main.fragments

sealed class StationAction {
    class StationItemClick(val id: Int) : StationAction()
    object StationItemsRefresh : StationAction()
}