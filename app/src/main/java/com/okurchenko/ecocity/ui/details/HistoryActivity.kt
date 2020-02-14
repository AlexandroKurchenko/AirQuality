package com.okurchenko.ecocity.ui.details

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.okurchenko.ecocity.R
import com.okurchenko.ecocity.ui.details.fragments.DetailsFragment
import com.okurchenko.ecocity.ui.details.fragments.HistoryFragment
import com.okurchenko.ecocity.ui.main.MainActivity
import com.okurchenko.ecocity.ui.main.fragments.Events
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryActivity : AppCompatActivity() {

    companion object {
        const val DETAILS_ID_EXT = "DETAILS_ID_EXT"
    }

    private val viewModel by viewModel<HistoryViewModel>()

    private lateinit var actor: HistoryActor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        actor = HistoryActor(viewModel::takeAction)
        subscribeToViewModelUpdate()
        if (savedInstanceState == null) {
            openHistoryFragment()
        }
    }

    private fun subscribeToViewModelUpdate() {
        viewModel.getState().observe(this, Observer {
            if (it is StationHistoryState.StationDetailsStateEvent) processEvent(it.event)
        })
    }

    private fun processEvent(event: Events) {
        when (event) {
            is Events.OpenMain -> openMainScreen()
            is Events.OpenDetails -> openDetailsFragment()
            is Events.OpenHistory -> openHistoryFragment()
        }
    }

    private fun openHistoryFragment() {
        getStationId()?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, HistoryFragment.newInstance(it))
                .commitNow()
        }
    }

    private fun openDetailsFragment() {
        getStationId()?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, DetailsFragment.newInstance(it))
                .commitNow()
        }
    }

    private fun openMainScreen() = Intent(this@HistoryActivity, MainActivity::class.java).also { startActivity(it) }

    private fun getStationId(): Int? {
        val args = intent.extras
        if (args != null && args.containsKey(DETAILS_ID_EXT)) {
            return args.getInt(DETAILS_ID_EXT)
        }
        return null
    }
}