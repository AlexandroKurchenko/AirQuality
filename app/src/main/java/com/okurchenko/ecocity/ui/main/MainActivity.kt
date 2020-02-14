package com.okurchenko.ecocity.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.okurchenko.ecocity.R
import com.okurchenko.ecocity.ui.details.HistoryActivity
import com.okurchenko.ecocity.ui.main.fragments.Events
import com.okurchenko.ecocity.ui.main.fragments.SectionsPagerAdapter
import com.okurchenko.ecocity.ui.main.fragments.StationsActor
import com.okurchenko.ecocity.ui.main.fragments.StationsState
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initPager()
        subscribeToViewModelUpdate()
        startFetchData()
    }

    private fun initPager() {
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = SectionsPagerAdapter(
            this,
            supportFragmentManager
        )
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }

    private fun startFetchData() {
        StationsActor(viewModel::takeAction).refresh()
    }

    private fun subscribeToViewModelUpdate() {
        viewModel.getState().observe(this, Observer { state ->
            if (state is StationsState.StationEvent) {
                when (state.event) {
                    is Events.OpenHistoryActivity -> openStationDetailsScreen(state.event.id)
                }
            }
        })
    }

    private fun openStationDetailsScreen(id: Int) {
        Intent(this@MainActivity, HistoryActivity::class.java)
            .apply { putExtra(HistoryActivity.DETAILS_ID_EXT, id) }
            .also { startActivity(it) }
    }
}