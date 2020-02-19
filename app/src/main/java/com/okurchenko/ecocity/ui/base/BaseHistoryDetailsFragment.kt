package com.okurchenko.ecocity.ui.base

import android.content.Context
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.okurchenko.ecocity.ui.details.EventProcessor
import com.okurchenko.ecocity.ui.details.HistoryDetailsActivity
import com.okurchenko.ecocity.ui.details.OnBackPressed

abstract class BaseHistoryDetailsFragment : Fragment(), OnBackPressed {

    protected var eventListener: EventProcessor? = null

    abstract fun onBackActionPressed()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            eventListener = requireActivity() as HistoryDetailsActivity
        } catch (ex: ClassCastException) {
            throw ClassCastException("${requireActivity()} must implement EventProcessor")
        }
    }

    override fun onDetach() {
        super.onDetach()
        eventListener = null
    }

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