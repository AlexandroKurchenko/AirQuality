package com.okurchenko.ecocity.ui.main.fragments.map

import com.okurchenko.ecocity.repository.LocationListener
import com.okurchenko.ecocity.ui.base.BaseViewAction
import com.okurchenko.ecocity.ui.base.BaseViewModel
import com.okurchenko.ecocity.ui.main.StationListViewAction
import org.koin.core.context.KoinContextHandler

class MapLocationViewModel : BaseViewModel() {

    private val location: LocationListener by lazy {
        KoinContextHandler.get().get<LocationListener>()
    }

    override fun takeAction(action: BaseViewAction) {
        when (action) {
            is StationListViewAction.StationProvideLocationUpdate -> location.initLocationUpdates()
            is StationListViewAction.StationStopProvideLocationUpdate -> location.destroyLocationUpdate()
        }
    }

    fun getLocationUpdate(): LocationListener = location
}