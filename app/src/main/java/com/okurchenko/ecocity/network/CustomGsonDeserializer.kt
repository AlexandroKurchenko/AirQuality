//package com.okurchenko.ecocity.network
//
//import com.google.gson.JsonDeserializationContext
//import com.google.gson.JsonDeserializer
//import com.google.gson.JsonElement
//import com.okurchenko.ecocity.repository.model.Level
//import com.okurchenko.ecocity.repository.model.StationResponse
//import java.lang.reflect.Type
//
//class CustomGsonDeserializer : JsonDeserializer<List<StationResponse>> {
//    override fun deserialize(
//        json: JsonElement,
//        typeOfT: Type,
//        context: JsonDeserializationContext
//    ): List<StationResponse> {
//        val items = mutableListOf<StationResponse>()
//        val jsonObject = json.asJsonArray
//        jsonObject.forEach { jsonStation ->
//            val element = jsonStation.asJsonObject
//            val levels = element.get("levels")
//            val levelsList = if (!levels.isJsonNull && levels.isJsonArray) {
//                val levelsList = mutableListOf<Level>()
//                val jsonLevel = levels.asJsonArray
//                jsonLevel.forEach { level ->
//                    levelsList.add(Level(level.asJsonObject.get("ago").asInt, level.asJsonObject.get("level").asInt))
//                }
//                levelsList
//            } else {
//                emptyList<Level>()
//            }
//            val station = StationResponse(
//                id = element.get("id").asString,
//                cityName = element.get("cityName").asString,
//                stationName = element.get("stationName").asString,
//                localName = element.get("localName").asString,
//                latitude = element.get("latitude").asString,
//                longitude = element.get("longitude").asString,
//                time = element.get("time").asString,
//                level = element.get("level").asString,
//                levels = levelsList,
//                indor = element.get("indor").asString,
//                offset = element.get("offset").asString
//            )
//            items.add(station)
//        }
//        return items
//    }
//}