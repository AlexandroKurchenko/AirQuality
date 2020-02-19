package com.okurchenko.ecocity.ui.details

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.okurchenko.ecocity.ui.details.fragments.details.DetailsFragment
import com.okurchenko.ecocity.ui.details.fragments.history.HistoryFragment

class FragmentFactoryImpl : FragmentFactory() {
    var stationId: Int = 0
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            HistoryFragment::class.java.name -> HistoryFragment()
            DetailsFragment::class.java.name -> DetailsFragment()
            else -> super.instantiate(classLoader, className)
        }
    }
}