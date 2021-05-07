package com.okurchenko.ecocity.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.okurchenko.ecocity.R
import com.okurchenko.ecocity.databinding.ActivityMainBinding
import com.okurchenko.ecocity.ui.base.EventProcessor
import com.okurchenko.ecocity.ui.base.NavigationEvents
import com.okurchenko.ecocity.ui.details.HistoryDetailsActivity

class MainActivity : AppCompatActivity(), EventProcessor {

    private val tabTitles = arrayOf(R.string.map_tab_text, R.string.list_tab_text)
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPager()
    }

    override fun processEvent(event: NavigationEvents) {
        if (event is NavigationEvents.OpenHistoryActivity) {
            openStationDetailsScreen(event.id)
        }
    }

    fun setViewPager2UserInputEnabled(isUserInputEnabled: Boolean) {
        binding.viewPager.isUserInputEnabled = isUserInputEnabled
    }

    private fun initPager() {
        binding.viewPager.apply {
            adapter = ViewPagerAdapter(this@MainActivity, tabTitles)
        }
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab: TabLayout.Tab, position: Int ->
            tab.setText(tabTitles[position])
        }.attach()
    }

    private fun openStationDetailsScreen(id: Int) {
        Intent(this@MainActivity, HistoryDetailsActivity::class.java)
            .apply { putExtra(HistoryDetailsActivity.HISTORY_DETAILS_STATION_ID_EXT, id) }
            .also { startActivity(it) }
        overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
    }
}