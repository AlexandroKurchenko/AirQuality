package com.okurchenko.ecocity.utils

import java.math.RoundingMode
import kotlin.math.*

fun round(input: Double, roundValue: Int): Double {
    return input.toBigDecimal().setScale(roundValue, RoundingMode.HALF_EVEN).toDouble()
}

/**
 * 3958.75 for miles output,
 * 6371 for kilometer output
 */
fun calculateDistanceBetweenTwoLocales(
    lat1: Double,
    lng1: Double,
    lat2: Double,
    lng2: Double
): Double {
    val earthRadius = 6371
    val diffLat = Math.toRadians(lat2 - lat1)
    val diffLng = Math.toRadians(lng2 - lng1)
    val sinDiffLat = sin(diffLat / 2)
    val sinDiffLng = sin(diffLng / 2)
    val a = sinDiffLat.pow(2.0) + (sinDiffLng.pow(2.0)
        * cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)))
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c
}