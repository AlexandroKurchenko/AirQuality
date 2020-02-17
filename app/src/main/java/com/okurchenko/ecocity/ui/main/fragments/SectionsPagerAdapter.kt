package com.okurchenko.ecocity.ui.main.fragments

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.okurchenko.ecocity.R
import com.okurchenko.ecocity.ui.main.fragments.map.MapFragment
import com.okurchenko.ecocity.ui.main.fragments.stations.StationsFragment

private val TAB_TITLES = arrayOf(
    R.string.list_tab_text,
    R.string.map_tab_text
)

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> StationsFragment.newInstance()
            else -> MapFragment.newInstance()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return TAB_TITLES.size
    }
}