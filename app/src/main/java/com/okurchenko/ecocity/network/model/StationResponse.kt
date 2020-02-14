package com.okurchenko.ecocity.network.model

data class StationResponse(
    val id: String,
    val cityName: String?,
    val stationName: String?,
    val localName: String?,
    val latitude: String,
    val longitude: String,
    val time: String,
    val level: String,
//    val levels: String,
    val indor: String,
    val offset: String
)

//data class Level(val ago: Int, val level: Int)
//image is https://eco-city.org.ua/img/levels/0-5.png