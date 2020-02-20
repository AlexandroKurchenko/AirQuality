package com.okurchenko.ecocity.ui.base

interface EventProcessor {
    fun processEvent(event: NavigationEvents)
}