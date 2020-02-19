package com.okurchenko.ecocity.ui.main

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.okurchenko.ecocity.R
import com.okurchenko.ecocity.databinding.MainActivity2Binding
import com.okurchenko.ecocity.ui.base.NavigationEvents
import com.okurchenko.ecocity.ui.details.EventProcessor
import com.okurchenko.ecocity.ui.details.HistoryDetailsActivity
import com.okurchenko.ecocity.ui.details.OnBackPressed
import com.okurchenko.ecocity.ui.details.fragments.details.DetailsFragment
import com.okurchenko.ecocity.ui.details.fragments.history.HistoryFragment
import com.okurchenko.ecocity.ui.main.fragments.SectionsPagerAdapter
import timber.log.Timber

class MainActivity2 : AppCompatActivity(), EventProcessor {

    private lateinit var binding: MainActivity2Binding
    private var selectedStationId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity_2)
        binding.lifecycleOwner = this
        initPager()
        initActionBar()
        if (savedInstanceState == null) {
            binding.tabsVisible = true
        }
    }

    private fun initActionBar() {
        binding.toolBar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        setSupportActionBar(binding.toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initPager() {
        binding.viewPager.adapter = SectionsPagerAdapter(this, supportFragmentManager)
        binding.tabs.setupWithViewPager(binding.viewPager)
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        savedInstanceState?.getInt(SAVE_INSTANCE_STATION_ID_EXT)?.let {
            intent.extras?.putInt(HistoryDetailsActivity.HISTORY_DETAILS_STATION_ID_EXT, it)
        }
        when (savedInstanceState?.getString(SAVE_INSTANCE_CURRENT_FRAGMENT_EXT)) {
            CurrentFragment.TAG_HISTORY.tag -> openHistoryFragment()
            CurrentFragment.TAG_DETAILS.tag -> openDetailsFragment(0)//TODO refactor
            CurrentFragment.UNKNOWN.tag -> {
                Timber.e("Could not found current fragment, so finish this activity")
                finish()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        getStationId()?.let { outState.putInt(SAVE_INSTANCE_STATION_ID_EXT, it) }
        outState.putString(SAVE_INSTANCE_CURRENT_FRAGMENT_EXT, findCurrentFragment().tag)
    }

    override fun onBackPressed() {
        supportFragmentManager.fragments.forEach {
            (it as? OnBackPressed)?.onBackPressed()
        }
    }

    override fun processEvent(event: NavigationEvents) {
        when (event) {
            is NavigationEvents.OpenMainActivity -> binding.tabsVisible = true
            is NavigationEvents.OpenDetailsFragment -> openDetailsFragment(event.timeShift)
            is NavigationEvents.OpenHistoryFragment -> openHistoryFragment()
            is NavigationEvents.OpenHistoryActivity -> openStationDetailsScreen(event.id)
        }
    }

    private fun openHistoryFragment() {
        binding.tabsVisible = false
//        getStationId()?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, HistoryFragment.newInstance(selectedStationId), CurrentFragment.TAG_HISTORY.tag)
                .commitNow()
//        }
    }

    private fun openDetailsFragment(timeShift: Int) {
        binding.tabsVisible = false
//        getStationId()?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, DetailsFragment.newInstance(selectedStationId, timeShift), CurrentFragment.TAG_DETAILS.tag)
                .commitNow()
//        }
    }

    private fun openStationDetailsScreen(id: Int) {
        binding.tabsVisible = false
        selectedStationId = id
        openHistoryFragment()
//        Intent(this@MainActivity2, HistoryDetailsActivity::class.java)
//            .apply { putExtra(HistoryDetailsActivity.HISTORY_DETAILS_STATION_ID_EXT, id) }
//            .also { startActivity(it) }
    }

    private fun getStationId(): Int? {
        val args = intent.extras
        if (args != null && args.containsKey(HistoryDetailsActivity.HISTORY_DETAILS_STATION_ID_EXT)) {
            return args.getInt(HistoryDetailsActivity.HISTORY_DETAILS_STATION_ID_EXT)
        }
        return null
    }

    private fun findCurrentFragment(): CurrentFragment {
        var currentFragmentTag: CurrentFragment? = null
        CurrentFragment.values().forEach {
            supportFragmentManager.findFragmentByTag(it.tag).let { _ -> currentFragmentTag = it }
        }
        return currentFragmentTag ?: CurrentFragment.UNKNOWN
    }

    private enum class CurrentFragment(val tag: String) {
        TAG_HISTORY("TAG_HISTORY"), TAG_DETAILS("TAG_DETAILS"), UNKNOWN("-1")
    }
}

private const val SAVE_INSTANCE_CURRENT_FRAGMENT_EXT = "SAVE_INSTANCE_CURRENT_FRAGMENT_EXT"
private const val SAVE_INSTANCE_STATION_ID_EXT = "SAVE_INSTANCE_STATION_ID_EXT"
