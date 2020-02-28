package com.okurchenko.ecocity.network

import com.okurchenko.ecocity.network.model.StationDataResponse
import com.okurchenko.ecocity.network.model.StationResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

//https://eco-city.org.ua/?zoom=13&lat=48.48250300&lng=34.96305200&station=246&random=5167202
interface StationApi {
    @GET("public.json")//https://eco-city.org.ua/public.json
    fun fetchAllStationsAsync(): Deferred<Response<List<StationResponse>>>

    @GET("public.json")//https://eco-city.org.ua/public.json?id=498&timeShift=0
    fun fetchStationDataByIdAsync(
        @Query("id") id: Int,
        @Query("timeShift") timeShift: Int
    ): Deferred<Response<List<StationDataResponse>>>
}