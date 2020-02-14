package com.okurchenko.ecocity.repository.db

import androidx.room.*
import com.okurchenko.ecocity.repository.model.StationItem

@Dao
interface StationDao {
    @Query("SELECT * FROM stationitem ORDER BY time DESC")
    fun getAllStations(): List<StationItem>

    @Query("SELECT * FROM stationitem WHERE id= :stationId")
    fun getStationById(stationId: Int): StationItem

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStationItems(stationList: List<StationItem>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStationItem(station: StationItem): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateStationItem(stationList: StationItem)
}
