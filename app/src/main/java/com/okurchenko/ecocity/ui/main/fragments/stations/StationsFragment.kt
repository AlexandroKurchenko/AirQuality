package com.okurchenko.ecocity.ui.main.fragments.stations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.okurchenko.ecocity.R
import com.okurchenko.ecocity.repository.model.StationItem
import com.okurchenko.ecocity.ui.base.BaseNavigationFragment
import com.okurchenko.ecocity.ui.base.ItemOffsetDecoration
import com.okurchenko.ecocity.ui.main.MainViewModel
import com.okurchenko.ecocity.ui.main.StationListActor
import com.okurchenko.ecocity.ui.main.StationListState
import org.koin.androidx.viewmodel.ext.android.viewModel

class StationsFragment : BaseNavigationFragment() {

    private val viewModel by viewModel<MainViewModel>()
    private lateinit var adapter: StationsAdapter
    private lateinit var loadingView: ProgressBar
    private lateinit var errorView: AppCompatTextView
    private lateinit var swipeToRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_stations, container, false)
        recyclerView = view.findViewById(R.id.stationsList)
        swipeToRefreshLayout = view.findViewById(R.id.swipeToRefreshLayout)
        loadingView = view.findViewById(R.id.loadingView)
        errorView = view.findViewById(R.id.errorView)
        val actor = StationListActor(viewModel::takeAction)
        swipeToRefreshLayout.setOnRefreshListener {
            actor.refresh()
            swipeToRefreshLayout.isRefreshing = false
        }
        adapter = StationsAdapter(actor)
        recyclerView.adapter = adapter
        context?.let { recyclerView.addItemDecoration(ItemOffsetDecoration(it)) }
        subscribeToViewModelUpdate()
        return view
    }

    private fun subscribeToViewModelUpdate() {
        viewModel.getState().observe(viewLifecycleOwner, Observer { state ->
            if (isViewElementsInitialized()) {
                when (state) {
                    is StationListState.StationItemsLoaded -> displayContent(state.data)
                    is StationListState.Error -> displayError()
                    is StationListState.Loading -> displayLoading()
                    is StationListState.StationEvent -> eventListener?.processEvent(state.event)
                }
            }
        })
    }

    private fun displayContent(data: List<StationItem>) {
        adapter.submitData(data)
        runLayoutAnimation()
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

    private fun isViewElementsInitialized(): Boolean =
        ::loadingView.isInitialized && ::errorView.isInitialized && ::swipeToRefreshLayout.isInitialized && ::adapter.isInitialized

    private fun runLayoutAnimation() = recyclerView.apply {
        layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_bottom)
        adapter?.notifyDataSetChanged()
        scheduleLayoutAnimation()

    }
}