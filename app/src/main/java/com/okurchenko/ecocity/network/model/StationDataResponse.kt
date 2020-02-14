package com.okurchenko.ecocity.network.model


data class StationDataResponse(
    val id: String?,
    val name: String?,
    val unit: String?,
    val cr: String?,
    val value: String?,
    val localName: String?,
    val localUnit: String?,
    val time: String?,
    val share: String?,
    val levels: String?,
    val offset: String?,
    val level: Int?,
    val index: Int?,
    val weather: Weather?,
    val owner: Owner?
)

data class Owner(
    val name: String?,
    val url: String?
)

data class Weather(
    val speed: String?,
    val deg: String?,
    val unit: String?,
    val time: String?,
    val localName: String?,
    val id: String?
)