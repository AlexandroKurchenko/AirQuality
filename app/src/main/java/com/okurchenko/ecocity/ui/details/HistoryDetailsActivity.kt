package com.okurchenko.ecocity.ui.details

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.okurchenko.ecocity.R
import com.okurchenko.ecocity.ui.base.NavigationEvents
import com.okurchenko.ecocity.ui.details.fragments.details.DetailsFragment
import com.okurchenko.ecocity.ui.details.fragments.history.HistoryFragment
import timber.log.Timber

class HistoryDetailsActivity : AppCompatActivity(), EventProcessor {

    companion object {
        const val HISTORY_DETAILS_STATION_ID_EXT = "HISTORY_DETAILS_STATION_ID_EXT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        val toolbar: Toolbar = findViewById(R.id.toolBar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (savedInstanceState == null) {
            openHistoryFragment()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        savedInstanceState?.getInt(SAVE_INSTANCE_STATION_ID_EXT)?.let {
            intent.extras?.putInt(HISTORY_DETAILS_STATION_ID_EXT, it)
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
            is NavigationEvents.OpenMainActivity -> openMainScreen()
            is NavigationEvents.OpenDetailsFragment -> openDetailsFragment(event.timeShift)
            is NavigationEvents.OpenHistoryFragment -> openHistoryFragment()
        }
    }

    private fun openHistoryFragment() =
        getStationId()?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, HistoryFragment.newInstance(it), CurrentFragment.TAG_HISTORY.tag)
                .commitNow()
        }

    private fun openDetailsFragment(timeShift: Int) =
        getStationId()?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, DetailsFragment.newInstance(it, timeShift), CurrentFragment.TAG_DETAILS.tag)
                .commitNow()
        }

    private fun openMainScreen() = this.finish()

    private fun getStationId(): Int? {
        val args = intent.extras
        if (args != null && args.containsKey(HISTORY_DETAILS_STATION_ID_EXT)) {
            return args.getInt(HISTORY_DETAILS_STATION_ID_EXT)
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