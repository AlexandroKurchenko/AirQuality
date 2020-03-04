package com.okurchenko.ecocity.utils

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.okurchenko.ecocity.R
import com.okurchenko.ecocity.ui.main.StationListActor
import com.okurchenko.ecocity.ui.main.StationListState
import com.okurchenko.ecocity.ui.main.fragments.stations.StationSort

@BindingAdapter("visibleIfNotNull")
fun visibleIfNotNull(view: TextView, source: String?) {
    view.visibility = if (source.isNullOrBlank()) View.GONE else View.VISIBLE
}

@BindingAdapter("aqiBackground")
fun setAqiBackground(view: TextView, source: Int) {
    val drawable = view.context.resources.getDrawable(R.drawable.circle, null)
    val resource = when (getAqiIndex(source)) {
        0 -> R.color.aqi_good
        1 -> R.color.aqi_moderate
        2 -> R.color.aqi_unhealthy_sensitive_groups
        3 -> R.color.aqi_unhealthy
        4 -> R.color.aqi_very_unhealthy
        5 -> R.color.aqi_hazardous
        else -> android.R.color.darker_gray
    }
    drawable.setTint(ContextCompat.getColor(view.context, resource))
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

@BindingAdapter("manageVisibilityLoadingState")
fun manageVisibilityLoadingState(view: View, state: StationListState?) {
    view.visibility = when (state) {
        is StationListState.Loading -> View.VISIBLE
        else -> View.GONE
    }
}

@BindingAdapter("manageVisibilityErrorState")
fun manageVisibilityErrorState(view: View, state: StationListState?) {
    view.visibility = when (state) {
        is StationListState.Error -> View.VISIBLE
        else -> View.GONE
    }
}

@BindingAdapter("manageVisibilityLoadedState")
fun manageVisibilityLoadedState(view: View, state: StationListState?) {
    view.visibility = when (state) {
        is StationListState.StationItemsLoaded -> View.VISIBLE
        else -> View.GONE
    }
}

@BindingAdapter("sortAction")
fun sortAction(fab: FloatingActionButton, actor: StationListActor?) {
    fab.setOnClickListener {
        when (it.tag) {
            StationSort.SortByName -> actor?.sortByName()
            else -> actor?.sortByDistance()
        }
    }
}

@SuppressLint("StringFormatMatches")
@BindingAdapter("manageVisibilityOfDistance")
fun manageVisibilityOfDistance(view: TextView, distance: Double) {
    if (distance == 0.0) {
        view.visibility = View.GONE
    } else {
        view.text = view.resources.getString(R.string.located_from_user, distance)
        view.visibility = View.VISIBLE
    }
}