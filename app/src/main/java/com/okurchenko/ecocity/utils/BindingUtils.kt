package com.okurchenko.ecocity.utils

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.okurchenko.ecocity.R
import kotlin.time.ExperimentalTime

@BindingAdapter("visibleIfNotNull")
fun visibleIfNotNull(view: TextView, source: String?) {
    view.visibility = if (source.isNullOrBlank()) View.GONE else View.VISIBLE
}

@BindingAdapter("aqiBackground")
fun setAqiBackground(view: TextView, source: Int) {
    val drawable = view.context.resources.getDrawable(R.drawable.circle)
    when (getAqiIndex(source)) {
        -1 -> drawable.setTint(view.context.resources.getColor(android.R.color.darker_gray))
        0 -> drawable.setTint(view.context.resources.getColor(R.color.aqi_good))
        1 -> drawable.setTint(view.context.resources.getColor(R.color.aqi_moderate))
        2 -> drawable.setTint(view.context.resources.getColor(R.color.aqi_unhealthy_sensitive_groups))
        3 -> drawable.setTint(view.context.resources.getColor(R.color.aqi_unhealthy))
        4 -> drawable.setTint(view.context.resources.getColor(R.color.aqi_very_unhealthy))
        5 -> drawable.setTint(view.context.resources.getColor(R.color.aqi_hazardous))
    }
    view.background = drawable
}

@SuppressLint("StringFormatMatches")
@BindingAdapter("aqiIndex")
fun setAqiIndex(view: TextView, source: Int) {
    if (source == -1) {
        view.text = view.context.getString(R.string.no_aqi_text)
    } else {
        view.text = view.context.getString(R.string.aqi_text, source)
    }
}

@ExperimentalTime
@BindingAdapter("displayTimeShift")
fun setTimeShift(view: TextView, source: Int) {
    val helper = LocaleHelper(view.context)
    val timeToDisplay = getTimeFromTimeShift(source, helper.getLocale())
    if (timeToDisplay.isNotBlank()) {
        view.text = view.context.getString(R.string.details_for_label, timeToDisplay)
    } else {
        view.visibility = View.GONE
    }
}