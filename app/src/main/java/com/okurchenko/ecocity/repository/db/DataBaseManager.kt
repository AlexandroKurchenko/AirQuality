package com.okurchenko.ecocity.repository.db

import com.okurchenko.ecocity.repository.model.StationDetails
import com.okurchenko.ecocity.repository.model.StationItem
import com.okurchenko.ecocity.utils.convertDataFormat
import com.okurchenko.ecocity.utils.isNewItemOlder
import java.util.*

class DataBaseManager(private val db: StationDatabase, private val currentLocale: Locale) {

    fun saveStationDetailsResult(stationDetails: StationDetails) {
        val result = db.stationDataDao().insertStationDataItems(stationDetails)
        if (isNeedUpdateStationData(result)) {
            db.stationDataDao().updateStationData(stationDetails)
        }
    }

    fun getAllHistory(stationId: Int, timeShift: Int) =
        db.stationDataDao().getAllHistoryByStationId(stationId, timeShift)

    fun getAllStationItems() = db.stationDao().getAllStations().map { stationItem ->
        stationItem.copy(time = convertDataFormat(stationItem.time, currentLocale))
    }.sortedBy { it.name }

    fun storeNewStationItems(stationItems: List<StationItem>) {
        stationItems.forEach { item ->
            val result = db.stationDao().insertStationItem(item)
            if (isNeedUpdate(result, item)) {
                db.stationDao().updateStationItem(item)
            }
        }
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

}