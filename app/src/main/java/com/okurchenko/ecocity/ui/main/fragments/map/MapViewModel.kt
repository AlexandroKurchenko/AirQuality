package com.okurchenko.ecocity.ui.main.fragments.map

import com.okurchenko.ecocity.repository.LocationListener
import com.okurchenko.ecocity.ui.base.BaseViewAction
import com.okurchenko.ecocity.ui.main.MainViewModel
import com.okurchenko.ecocity.ui.main.StationListViewAction
import org.koin.core.context.GlobalContext

class MapViewModel : MainViewModel() {

    private val location: LocationListener by lazy { GlobalContext.get().koin.get<LocationListener>() }

    override fun takeAction(action: BaseViewAction) {
        super.takeAction(action)
        when (action) {
            is StationListViewAction.StationProvideLocationUpdate -> location.initLocationUpdates()
            is StationListViewAction.StationStopProvideLocationUpdate -> location.destroyLocationUpdate()
        }
    }

    fun getLocationUpdate(): LocationListener = location
}