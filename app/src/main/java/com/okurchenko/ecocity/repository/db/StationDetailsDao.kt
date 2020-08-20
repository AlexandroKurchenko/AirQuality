package com.okurchenko.ecocity.repository.db

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.okurchenko.ecocity.repository.model.StationDetails

@Dao
interface StationDetailsDao {
    @Query("SELECT * FROM stationdetails ORDER BY hoursAgo ASC")
    fun getAllStationData(): LiveData<List<StationDetails>>

    @Query("SELECT * FROM stationdetails WHERE stationId = :id AND hoursAgo = :timeShift")
    fun getHistoryByStationIdAndTimeShift(id: Int, timeShift: Int): StationDetails?

    @Query("SELECT * FROM stationdetails WHERE stationId = :id")
    fun getAllHistoryByStationIdPaging(id: Int): PagingSource<Int, StationDetails>

    @Query("SELECT COUNT (*) FROM stationdetails WHERE stationId = :id")
    fun getHistoryCountByStationId(id: Int): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStationDataItems(stationData: StationDetails): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateStationData(stationList: StationDetails)
}