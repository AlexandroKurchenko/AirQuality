package com.okurchenko.ecocity.utils

fun getAqiIndex(aqi: Int): Int {
    return when {
        aqi.isGood() -> 0
        aqi.isModerate() -> 1
        aqi.isUnhealthySensitiveGroups() -> 2
        aqi.isUnhealthy() -> 3
        aqi.isVeryUnhealthy() -> 4
        aqi.isHazardous() -> 5
        else -> -1
    }
}

private fun Int.isGood(): Boolean = this in 1..50
private fun Int.isModerate(): Boolean = this in 51..100
private fun Int.isUnhealthySensitiveGroups(): Boolean = this in 101..150
private fun Int.isUnhealthy(): Boolean = this in 151..200
private fun Int.isVeryUnhealthy(): Boolean = this in 201..300
private fun Int.isHazardous(): Boolean = this in 301..500