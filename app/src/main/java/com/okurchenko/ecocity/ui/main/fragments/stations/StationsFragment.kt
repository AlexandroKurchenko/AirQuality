package com.okurchenko.ecocity.ui.main.fragments.stations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import com.okurchenko.ecocity.R
import com.okurchenko.ecocity.repository.model.StationItem
import com.okurchenko.ecocity.ui.base.BaseNavigationFragment
import com.okurchenko.ecocity.ui.base.ItemOffsetDecoration
import com.okurchenko.ecocity.ui.main.MainViewModel
import com.okurchenko.ecocity.ui.main.StationListActor
import com.okurchenko.ecocity.ui.main.StationListState
import kotlinx.android.synthetic.main.fragment_stations.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class StationsFragment : BaseNavigationFragment() {

    private val viewModel by viewModel<MainViewModel>()
    private lateinit var adapter: StationsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_stations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val actor = StationListActor(viewModel::takeAction)
        swipeToRefreshLayout.setOnRefreshListener {
            actor.refresh()
            swipeToRefreshLayout.isRefreshing = false
        }
        adapter = StationsAdapter(actor)
        stationsList.adapter = adapter
        context?.let { stationsList.addItemDecoration(ItemOffsetDecoration(it)) }
        subscribeToViewModelUpdate()
    }

    private fun subscribeToViewModelUpdate() {
        viewModel.getState().observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is StationListState.StationItemsLoaded -> displayContent(state.data)
                is StationListState.Error -> displayError()
                is StationListState.Loading -> displayLoading()
            }
        })
        viewModel.getNavigationEvents().observe(viewLifecycleOwner, Observer { eventListener?.processEvent(it) })
    }

    private fun displayContent(data: List<StationItem>) {
        if (::adapter.isInitialized) {
            adapter.submitData(data)
        }
        runAfterLoadingLayoutAnimation()
        loadingView.visibility = View.GONE
        errorView.visibility = View.GONE
        swipeToRefreshLayout.visibility = View.VISIBLE
    }

    private fun displayError() {
        errorView.visibility = View.VISIBLE
        loadingView.visibility = View.GONE
        swipeToRefreshLayout.visibility = View.GONE
    }

    private fun displayLoading() {
        swipeToRefreshLayout.visibility = View.GONE
        loadingView.visibility = View.VISIBLE
        errorView.visibility = View.GONE
    }

    private fun runAfterLoadingLayoutAnimation() = stationsList.apply {
        val resourceId = R.anim.layout_animation_from_bottom
        layoutAnimation = AnimationUtils.loadLayoutAnimation(context, resourceId)
        adapter?.notifyDataSetChanged()
        scheduleLayoutAnimation()
    }
}