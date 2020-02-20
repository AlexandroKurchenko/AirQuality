package com.okurchenko.ecocity.ui.base

import android.view.MenuItem

abstract class BaseHistoryDetailsFragment : BaseNavigationFragment(),
    OnBackPressed {

    abstract fun onBackActionPressed()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackActionPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        onBackActionPressed()
    }
}