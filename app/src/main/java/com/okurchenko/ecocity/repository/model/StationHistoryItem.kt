package com.okurchenko.ecocity.repository.model

open class BaseItem

class EmptyItem : BaseItem()

data class StationHistoryItem(
    val timeAgo: Int,
    val aqi: Int,
    val source2: String?,
    val source3: String?,
    val owner: String
) : BaseItem() {
    fun isSource2Exist() = source2 != null
    fun isSource3Exist() = source3 != null
}