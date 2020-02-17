package com.okurchenko.ecocity.ui.details.fragments

import android.content.Context
import androidx.fragment.app.Fragment
import com.okurchenko.ecocity.ui.details.EventProcessor
import com.okurchenko.ecocity.ui.details.HistoryDetailsActivity

open class BaseHistoryDetailsFragment : Fragment() {

    protected var eventListener: EventProcessor? = null

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
}