package com.okurchenko.ecocity.ui.details

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.okurchenko.ecocity.R
import com.okurchenko.ecocity.ui.base.EventProcessor
import com.okurchenko.ecocity.ui.base.NavigationEvents
import com.okurchenko.ecocity.ui.base.OnBackPressed
import com.okurchenko.ecocity.ui.details.fragments.details.DetailsFragment
import com.okurchenko.ecocity.ui.details.fragments.history.HistoryFragment
import kotlinx.android.synthetic.main.detail_list.*

class HistoryDetailsActivity : AppCompatActivity(), EventProcessor {

    companion object {
        const val HISTORY_DETAILS_STATION_ID_EXT = "HISTORY_DETAILS_STATION_ID_EXT"
    }

    private var tabletMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_detail)
        val toolbar: Toolbar = findViewById(R.id.toolBar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (fragmentDetails != null) {
            tabletMode = true
        }
        if (savedInstanceState == null) {
            openHistoryFragment()
        }
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
        getStationId()?.let { stationId ->
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(
                    R.id.fragmentContainer,
                    HistoryFragment.newInstance(stationId),
                    HistoryFragment::class.java.name
                )
                .commitNow()
        }

    private fun openDetailsFragment(timeShift: Int = 0) =
        getStationId()?.let { stationId ->
            val container = if (tabletMode) R.id.fragmentDetails else R.id.fragmentContainer
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(container, DetailsFragment.newInstance(stationId, timeShift), DetailsFragment::class.java.name)
                .commitNow()
        }

    private fun openMainScreen() {
        this@HistoryDetailsActivity.finish()
        overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
    }

    private fun getStationId(): Int? {
        val args: Bundle? = intent.extras
        if (args != null && args.containsKey(HISTORY_DETAILS_STATION_ID_EXT)) {
            return args.getInt(HISTORY_DETAILS_STATION_ID_EXT)
        }
        return null
    }
}