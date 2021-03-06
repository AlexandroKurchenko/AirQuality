package com.okurchenko.ecocity.utils

import android.os.Build
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.time.ExperimentalTime
import kotlin.time.hours
import kotlin.time.toDuration

private const val MATCH_PATTERN = "yyyy-MM-dd hh:mm:ss"
private const val DISPLAY_DAY_MIN_PATTERN = "dd MMMM yyyy, HH:mm"
private const val DISPLAY_MIN_PATTERN = "HH:mm"
private const val DISPLAY_12_HOURS_PATTERN = "HH:mm a"

fun isNewItemOlder(newTime: String, oldTime: String, locale: Locale?): Boolean {
    val dateFormat: DateFormat = locale.getFormat(MATCH_PATTERN)
    val newDate = dateFormat.parse(newTime)
    val oldDate = dateFormat.parse(oldTime)
    return if (newDate != null && oldDate != null) {
        newDate.after(oldDate)
    } else {
        true
    }
}

fun convertDataFormat(inputDate: String, locale: Locale?): String {
    val date = locale.getFormat(MATCH_PATTERN).parse(inputDate)
    val diffDays = date?.time?.diffTimeInDays()
    return diffDays?.run {
        if (diffDays > 1) formatDayMin(locale, date) else formatMin(locale, date)
    } ?: run {
        inputDate
    }
}

fun getNowTime(): Long {
    return Date(System.currentTimeMillis()).time
}

fun Long.diffTimeInMinutes(): Long = TimeUnit.MILLISECONDS.toMinutes(abs(getNowTime() - this))

@OptIn(ExperimentalTime::class)
fun getTimeFromTimeShift(timeShift: Int, locale: Locale?): String {
    val timeToDisplay = getNowTime().hours - timeShift.toDuration(TimeUnit.HOURS)
    return locale.getFormat(DISPLAY_12_HOURS_PATTERN).format(Date(timeToDisplay.toLongMilliseconds()))
}

private fun Long.diffTimeInDays(): Long = TimeUnit.MILLISECONDS.toDays(abs(getNowTime() - this))

private fun formatDayMin(locale: Locale?, date: Date) = locale.getFormat(DISPLAY_DAY_MIN_PATTERN).format(date)

private fun formatMin(locale: Locale?, date: Date) = locale.getFormat(DISPLAY_MIN_PATTERN).format(date)

private fun Locale?.getFormat(pattern: String): DateFormat = this?.let {
    SimpleDateFormat(pattern, it)
} ?: run {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        SimpleDateFormat(pattern, Locale.getDefault(Locale.Category.FORMAT))
    } else {
        SimpleDateFormat.getDateTimeInstance()
    }
}