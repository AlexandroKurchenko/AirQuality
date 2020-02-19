package com.okurchenko.ecocity.ui.details.fragments.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.okurchenko.ecocity.R
import com.okurchenko.ecocity.repository.model.StationHistoryItem
import com.okurchenko.ecocity.ui.base.BaseHistoryDetailsFragment
import com.okurchenko.ecocity.ui.details.HistoryDetailsActivity
import com.okurchenko.ecocity.ui.main.MainActivity2
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


/**
 * In this view only can be 48 ite,s in recycler view,
 * because api can store data only for 48 hours for particular station.
 * On site it will be called time machine
 */
private const val MAX_DISPLAY_ITEMS_COUNT = 48
private const val MIN_DISPLAY_ITEMS_COUNT = 4
private const val STATION_ID = "STATION_ID"

class HistoryFragment : BaseHistoryDetailsFragment() {

    companion object {

        @JvmStatic
        fun newInstance(stationId: Int) = HistoryFragment().apply {
            val args = Bundle()
            args.putInt(STATION_ID, stationId)
            this.arguments = args
        }
    }

    private val viewModel by viewModel<HistoryViewModel>()
    private lateinit var actor: HistoryListActor
    private lateinit var adapter: DetailsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var errorVew: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_history, container, false)
        actor = HistoryListActor(viewModel::takeAction)
        setupToolBar()
        recyclerView = rootView.findViewById(R.id.historyList)
        errorVew = rootView.findViewById(R.id.errorView)
        adapter = DetailsAdapter(actor)
        recyclerView.adapter = adapter
        subscribeToViewModelUpdate()
        if (savedInstanceState == null) {
            loadItems()
        }
        return rootView
    }

    override fun onBackActionPressed() {
        if (::actor.isInitialized) {
            actor.openMain()
        } else {
            requireActivity().finish()
        }
    }

    private fun setupToolBar() {
        setHasOptionsMenu(true)
        (requireActivity() as MainActivity2).supportActionBar?.title = getString(R.string.history_title)
    }

    private fun subscribeToViewModelUpdate() {
        viewModel.getState().observe(viewLifecycleOwner, Observer {
            when (it) {
                is HistoryListState.HistoryItemLoaded -> handleLoadedState(it.items)
                is HistoryListState.HistoryItemLoading -> handleLoadingState()
                is HistoryListState.FailLoading -> handleErrorState()
                is HistoryListState.StationDetailsNavigation -> eventListener?.processEvent(it.event)
            }
        })
    }

    private fun handleLoadedState(items: List<StationHistoryItem>) {
        if (::adapter.isInitialized) adapter.submitData(items)
        recyclerView.addOnScrollListener(scrollListener)
        displayErrorView(false)
    }

    private fun handleLoadingState() {
        if (::adapter.isInitialized) {
            adapter.showLoading()
            recyclerView.smoothScrollToPosition(adapter.itemCount)
        }
        displayErrorView(false)
    }

    private fun handleErrorState() {
        if (::adapter.isInitialized) {
            adapter.hideLoading()
            recyclerView.smoothScrollToPosition(adapter.itemCount)
        }
        displayErrorView(true)
        removeScrollListener()
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
            if (::adapter.isInitialized && linearLayoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount - 1) {
                val plannedTimeToShift = (adapter.itemCount - 1) + MIN_DISPLAY_ITEMS_COUNT
                if (plannedTimeToShift <= MAX_DISPLAY_ITEMS_COUNT) {
                    removeScrollListener()
                    Timber.d("This onScrolled called ${adapter.itemCount} $plannedTimeToShift")
                    loadItems(fromTimeShift = adapter.itemCount, toTimeShift = plannedTimeToShift)
                }
            }
        }
    }

    private fun loadItems(fromTimeShift: Int = 0, toTimeShift: Int = MIN_DISPLAY_ITEMS_COUNT) {
        val stationId = getStationId()
        if (::actor.isInitialized && stationId != null) actor.fetchHistoryData(stationId, fromTimeShift, toTimeShift)
    }

    private fun getStationId(): Int? = arguments?.getInt(STATION_ID, 246)

    private fun displayErrorView(isError: Boolean) {
        if (isError) {
            recyclerView.visibility = View.GONE
            errorVew.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            errorVew.visibility = View.GONE
        }
    }

    private fun removeScrollListener() {
        recyclerView.removeOnScrollListener(scrollListener)
    }
}