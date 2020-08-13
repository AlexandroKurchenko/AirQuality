package com.okurchenko.ecocity.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.okurchenko.ecocity.ui.main.fragments.map.MapFragment
import com.okurchenko.ecocity.ui.main.fragments.stations.StationsFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity, private val tabTitles: Array<Int>) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> MapFragment()
        else -> StationsFragment()
    }

    override fun getItemCount() = tabTitles.size
}