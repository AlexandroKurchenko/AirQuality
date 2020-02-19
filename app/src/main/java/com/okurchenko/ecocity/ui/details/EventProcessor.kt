package com.okurchenko.ecocity.ui.details

import com.okurchenko.ecocity.ui.base.NavigationEvents

interface EventProcessor {
    fun processEvent(event: NavigationEvents)
}