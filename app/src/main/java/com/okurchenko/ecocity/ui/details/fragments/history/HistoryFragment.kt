package com.okurchenko.ecocity.ui.details.fragments.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.okurchenko.ecocity.R
import com.okurchenko.ecocity.ui.details.fragments.BaseHistoryDetailsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


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
        loadItems()
        return rootView
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home && ::actor.isInitialized) {
            actor.openMain()
        } else requireActivity().finish()
        return super.onOptionsItemSelected(item)
    }

    private fun setupToolBar() {
        setHasOptionsMenu(true)
        requireActivity().actionBar?.title = getString(R.string.history_title)
    }

    private fun subscribeToViewModelUpdate() {
        viewModel.getState().observe(viewLifecycleOwner, Observer {
            when (it) {
                is HistoryListState.HistoryItemLoaded -> {
                    if (::adapter.isInitialized) adapter.submitData(it.items)
                    recyclerView.addOnScrollListener(scrollListener)
                    displayErrorView(false)
                }
                is HistoryListState.HistoryItemLoading -> {
                    if (::adapter.isInitialized) {
                        adapter.showLoading()
                        recyclerView.smoothScrollToPosition(adapter.itemCount)
                    }
                    displayErrorView(false)
                }
                is HistoryListState.FailLoading -> {
                    if (::adapter.isInitialized) {
                        adapter.hideLoading()
                        recyclerView.smoothScrollToPosition(adapter.itemCount)
                    }
                    displayErrorView(true)
                    recyclerView.removeOnScrollListener(scrollListener)
                }
                is HistoryListState.StationDetailsStateEvent -> eventListener?.processEvent(it.event)
            }
        })
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
            if (::adapter.isInitialized && linearLayoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount - 1) {
                val plannedTimeToShift = (adapter.itemCount - 1) + MIN_DISPLAY_ITEMS_COUNT
                if (plannedTimeToShift <= MAX_DISPLAY_ITEMS_COUNT) {
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
}