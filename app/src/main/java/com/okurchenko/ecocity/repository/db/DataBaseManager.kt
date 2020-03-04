package com.okurchenko.ecocity.repository.db

import android.location.Location
import com.okurchenko.ecocity.repository.model.StationDetails
import com.okurchenko.ecocity.repository.model.StationItem
import com.okurchenko.ecocity.ui.main.fragments.stations.StationSort
import com.okurchenko.ecocity.utils.calculateDistanceBetweenTwoLocales
import com.okurchenko.ecocity.utils.convertDataFormat
import com.okurchenko.ecocity.utils.isNewItemOlder
import com.okurchenko.ecocity.utils.round
import java.util.*
import kotlin.math.roundToInt

class DataBaseManager(private val db: StationDatabase, private val currentLocale: Locale) {

    fun saveStationDetailsResult(stationDetails: StationDetails) {
        val result = db.stationDataDao().insertStationDataItems(stationDetails)
        if (isNeedUpdateStationData(result)) {
            db.stationDataDao().updateStationData(stationDetails)
        }
    }

    fun getAllHistory(stationId: Int, timeShift: Int) =
        db.stationDataDao().getAllHistoryByStationId(stationId, timeShift)

    fun getAllStationItems() = db.stationDao().getAllStations()

    fun sortAllStations(stations: List<StationItem>, sorting: StationSort): List<StationItem> {
        val items = stations.map { stationItem ->
            stationItem.copy(time = convertDataFormat(stationItem.time, currentLocale))
        }
        return when (sorting) {
            StationSort.SortByName -> items.sortedBy { it.name }
            else -> items.sortedWith(kotlin.Comparator { o1, o2 -> (o1.distance - o2.distance).roundToInt() })
        }
    }

    fun storeNewStationItems(stationItems: List<StationItem>) {
        stationItems.forEach { item ->
            val result = db.stationDao().insertStationItem(item)
            if (isNeedUpdate(result, item)) {
                db.stationDao().updateStationItem(item)
            }
        }
    }

    fun updateItemsWithDistance(
        location: Location,
        stations: List<StationItem>
    ): List<StationItem> {
        val stationWithDistance = mutableListOf<StationItem>()
        stations.forEach { item ->
            val distance = getDistance(item.lat, item.lon, location)
            val roundedDistance = round(input = distance, roundValue = 2)
            stationWithDistance.add(item.copy(distance = roundedDistance))
        }
        storeNewStationItems(stationWithDistance)
        return stationWithDistance
    }

    private fun isNeedUpdateStationData(insertedResultValue: Long): Boolean = insertedResultValue == (-1).toLong()

    private fun isNeedUpdate(insertedResultValue: Long, networkItem: StationItem): Boolean {
        return if (insertedResultValue == (-1).toLong()) {
            val oldItemTime = db.stationDao().getStationById(networkItem.id).time
            val newItemTime = networkItem.time
            isNewItemOlder(newItemTime, oldItemTime, currentLocale)
        } else {
            false
        }
    }


    private fun getDistance(lat: Double, lon: Double, location: Location) =
        calculateDistanceBetweenTwoLocales(lat1 = location.latitude, lng1 = location.longitude, lng2 = lon, lat2 = lat)


}