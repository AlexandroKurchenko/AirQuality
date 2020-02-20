package com.okurchenko.ecocity.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.okurchenko.ecocity.R
import com.okurchenko.ecocity.ui.base.EventProcessor
import com.okurchenko.ecocity.ui.base.NavigationEvents
import com.okurchenko.ecocity.ui.details.HistoryDetailsActivity

class MainActivity : AppCompatActivity(), EventProcessor {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initPager()
    }

    private fun initPager() {
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        val tabs: TabLayout = findViewById(R.id.tabs)
        viewPager.adapter = MainPagerAdapter(this, supportFragmentManager)
        tabs.setupWithViewPager(viewPager)
    }

    override fun processEvent(event: NavigationEvents) {
        if (event is NavigationEvents.OpenHistoryActivity) {
            openStationDetailsScreen(event.id)
        }
    }

    private fun openStationDetailsScreen(id: Int) {
        Intent(this@MainActivity, HistoryDetailsActivity::class.java)
            .apply { putExtra(HistoryDetailsActivity.HISTORY_DETAILS_STATION_ID_EXT, id) }
            .also {
                startActivity(it)
                this@MainActivity.finish()
            }
    }
}