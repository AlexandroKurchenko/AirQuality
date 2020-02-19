package com.okurchenko.ecocity.ui.base

sealed class NavigationEvents {
    class OpenHistoryActivity(val id: Int) : NavigationEvents()
    class OpenDetailsFragment(val timeShift: Int) : NavigationEvents()
    object OpenMainActivity : NavigationEvents()
    object OpenHistoryFragment : NavigationEvents()
}