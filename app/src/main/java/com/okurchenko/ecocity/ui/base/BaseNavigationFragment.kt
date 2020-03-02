package com.okurchenko.ecocity.ui.base

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.okurchenko.ecocity.ui.details.HistoryDetailsActivity
import com.okurchenko.ecocity.ui.main.MainActivity

open class BaseNavigationFragment : Fragment() {

    private var eventListener: EventProcessor? = null
    protected val navObserver = Observer<NavigationEvents> { event -> eventListener?.processEvent(event) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            val activity = requireActivity()
            eventListener = activity as? HistoryDetailsActivity ?: activity as MainActivity
        } catch (ex: ClassCastException) {
            throw ClassCastException("${requireActivity()} must implement EventProcessor")
        }
    }

    override fun onDetach() {
        super.onDetach()
        eventListener = null
    }

    protected open fun <T : ViewDataBinding> bindContentView(
        layoutInflater: LayoutInflater,
        @LayoutRes layoutId: Int,
        parent: ViewGroup?
    ): T = DataBindingUtil.inflate(layoutInflater, layoutId, parent, false)

}