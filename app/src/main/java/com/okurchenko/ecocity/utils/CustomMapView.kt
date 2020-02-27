package com.okurchenko.ecocity.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.gms.maps.MapView


class CustomMapView : MapView {
    private lateinit var viewTouch: (Boolean) -> Unit

    constructor(context: Context?) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun addMapViewTouch(viewTouch: (Boolean) -> Unit) {
        this.viewTouch = viewTouch
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_UP -> {
                if (::viewTouch.isInitialized) viewTouch.invoke(true)
            }
            MotionEvent.ACTION_DOWN -> {
                if (::viewTouch.isInitialized) viewTouch.invoke(false)
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
}