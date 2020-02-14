package com.okurchenko.ecocity.repository.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.okurchenko.ecocity.repository.model.StationDetails

@Dao
interface StationDetailsDao {
    @Query("SELECT * FROM stationdetails ORDER BY hoursAgo ASC")
    fun getAllStationData(): LiveData<List<StationDetails>>

    @Query("SELECT * FROM stationdetails WHERE stationId = :id AND hoursAgo = :timeShift")
    fun getAllHistoryByStationId(id: Int, timeShift: Int): StationDetails?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStationDataItems(stationData: StationDetails): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateStationData(stationList: StationDetails)
}