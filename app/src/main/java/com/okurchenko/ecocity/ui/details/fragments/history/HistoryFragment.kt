package com.okurchenko.ecocity.ui.details.fragments.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.okurchenko.ecocity.R
import com.okurchenko.ecocity.repository.model.StationHistoryItem
import com.okurchenko.ecocity.ui.base.BaseHistoryDetailsFragment
import com.okurchenko.ecocity.ui.base.ItemOffsetDecoration
import com.okurchenko.ecocity.ui.details.HistoryDetailsActivity
import kotlinx.android.synthetic.main.fragment_history.*
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_history, container, false)
        setupToolBar()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actor = HistoryListActor(viewModel::takeAction)
        adapter = DetailsAdapter(actor)
        historyList.adapter = adapter
        context?.let { context -> historyList.addItemDecoration(ItemOffsetDecoration(context)) }
        subscribeToViewModelUpdate()
        if (savedInstanceState == null) {
            loadItems()
        }
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
        (requireActivity() as? HistoryDetailsActivity)?.supportActionBar?.title = getString(R.string.history_title)
    }

    private fun subscribeToViewModelUpdate() {
        viewModel.getState().observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is HistoryListState.HistoryItemLoaded -> handleLoadedState(state.items)
                is HistoryListState.HistoryItemLoading -> handleLoadingState()
                is HistoryListState.FailLoading -> handleErrorState()
            }
        })
        viewModel.getNavigationEvents().observe(viewLifecycleOwner, Observer {
            eventListener?.processEvent(it)
        })
    }

    private fun handleLoadedState(items: List<StationHistoryItem>) {
        if (::adapter.isInitialized) adapter.submitData(items)
        historyList.addOnScrollListener(scrollListener)
        displayErrorView(false)
    }

    private fun handleLoadingState() {
        if (::adapter.isInitialized) {
            adapter.showLoading()
            historyList.smoothScrollToPosition(adapter.itemCount)
        }
        displayErrorView(false)
    }

    private fun handleErrorState() {
        if (::adapter.isInitialized) {
            adapter.hideLoading()
            historyList.smoothScrollToPosition(adapter.itemCount)
        }
        displayErrorView(true)
        removeScrollListener()
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
            if (::adapter.isInitialized && linearLayoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount - 1) {
                processEndListAction()
            }
        }
    }

    private fun processEndListAction() {
        val lastItemTimeShift = adapter.getLastItemTimeShift()
        val plannedTimeToShift = lastItemTimeShift + MIN_DISPLAY_ITEMS_COUNT
        if (plannedTimeToShift <= MAX_DISPLAY_ITEMS_COUNT) {
            removeScrollListener()
            loadItems(fromTimeShift = lastItemTimeShift + 1, toTimeShift = plannedTimeToShift)
        }
    }

    private fun loadItems(fromTimeShift: Int = 0, toTimeShift: Int = MIN_DISPLAY_ITEMS_COUNT) {
        val stationId = getStationId()
        if (::actor.isInitialized && stationId != null) actor.fetchHistoryData(stationId, fromTimeShift, toTimeShift)
    }

    private fun getStationId(): Int? = arguments?.getInt(STATION_ID, 246)

    private fun displayErrorView(isError: Boolean) {
        if (isError) {
            historyList.visibility = View.GONE
            errorView.visibility = View.VISIBLE
        } else {
            historyList.visibility = View.VISIBLE
            errorView.visibility = View.GONE
        }
    }

    private fun removeScrollListener() {
        historyList.removeOnScrollListener(scrollListener)
    }
}