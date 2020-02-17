package com.okurchenko.ecocity.utils

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("visibleIfNotNull")
fun visibleIfNotNull(view: TextView, source: String?) {
    view.visibility = if (source.isNullOrBlank()) View.GONE else View.VISIBLE
}