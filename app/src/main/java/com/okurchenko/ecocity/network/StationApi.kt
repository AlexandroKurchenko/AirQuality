package com.okurchenko.ecocity.network

import com.okurchenko.ecocity.network.model.StationDataResponse
import com.okurchenko.ecocity.network.model.StationResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StationApi {
    @GET("public.json")
    fun fetchAllStations(): Deferred<Response<List<StationResponse>>>

    @GET("public.json")
    fun fetchStationDataById(
        @Query("id") id: Int,
        @Query("timeShift") timeShift:Int
    ): Deferred<Response<List<StationDataResponse>>>
}