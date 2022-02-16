package com.okurchenko.ecocity.network

import com.okurchenko.ecocity.network.model.StationDataResponse
import com.okurchenko.ecocity.network.model.StationDataWrapperResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StationApi {
    @GET("public.json")
    fun fetchAllStationsAsync(
        @Query("key") key: Double,
        @Query("coords") coords: String
    ): Deferred<Response<StationDataWrapperResponse>>

    @GET("public.json")
    fun fetchStationDataByIdAsync(
        @Query("key") key: Double,
        @Query("id") id: Int,
        @Query("timeShift") timeShift: Int
    ): Deferred<Response<List<StationDataResponse>>>
}
