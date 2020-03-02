package com.okurchenko.ecocity.utils

import java.math.RoundingMode
import kotlin.math.*

fun round(input: Double, roundValue: Int): Double {
    return input.toBigDecimal().setScale(roundValue, RoundingMode.HALF_EVEN).toDouble()
}
//Usage of or use Location.distanceBetween(lat, lon, currentLat, currentLon, distance);
//    // distance[0] is now the distance between these lat/lon in meters
//    if (distance[0] < 2.0) {
//        // your code...
//    }


/*

  // lat1 and lng1 are the values of a previously stored location
    if (distance(lat1, lng1, lat2, lng2) < 0.1) { // if distance < 0.1 miles we take locations as equal
       //do what you want to do...
    }

 */
/** calculates the distance between two locations in MILES  */
fun distance(
    lat1: Double,
    lng1: Double,
    lat2: Double,
    lng2: Double
): Double {
    val earthRadius = 3958.75 // in miles, change to 6371 for kilometer output
    val diffLat = Math.toRadians(lat2 - lat1)
    val diffLng = Math.toRadians(lng2 - lng1)
    val sinDiffLat = sin(diffLat / 2)
    val sinDiffLng = sin(diffLng / 2)
    val a = sinDiffLat.pow(2.0) + (sinDiffLng.pow(2.0)
        * cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)))
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c
}