package com.okurchenko.ecocity.ui.main.fragments.stations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.okurchenko.ecocity.R
import com.okurchenko.ecocity.ui.main.MainViewModel
import com.okurchenko.ecocity.ui.main.fragments.StationListActor
import com.okurchenko.ecocity.ui.main.fragments.StationListState
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class StationsFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = StationsFragment()
    }

    private val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var adapter: StationsAdapter
    private lateinit var loadingView: ProgressBar
    private lateinit var errorView: AppCompatTextView
    private lateinit var swipeToRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val actor = StationListActor(viewModel::takeAction)
        val view = inflater.inflate(R.layout.fragment_stations, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.stationsList)
        swipeToRefreshLayout = view.findViewById(R.id.swipeToRefreshLayout)
        swipeToRefreshLayout.setOnRefreshListener {
            actor.refresh()
            swipeToRefreshLayout.isRefreshing = false
        }
        loadingView = view.findViewById(R.id.loadingView)
        errorView = view.findViewById(R.id.errorView)
        adapter = StationsAdapter(actor)
        recyclerView.adapter = adapter
        subscribeToViewModelUpdate()
        return view
    }

    private fun subscribeToViewModelUpdate() {
        viewModel.getState().observe(viewLifecycleOwner, Observer { state ->
            if (state is StationListState.StationItemsLoaded && ::adapter.isInitialized)
                adapter.submitData(state.data)
            manageElementsVisibility(state)
        })
    }

    private fun manageElementsVisibility(stationState: StationListState) {
        if (::loadingView.isInitialized && ::errorView.isInitialized && ::swipeToRefreshLayout.isInitialized) {
            when (stationState) {
                is StationListState.StationItemsLoaded -> displayContent()
                is StationListState.Error -> displayError()
                is StationListState.Loading -> displayLoading()
            }
        }
    }

    private fun displayContent() {
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
}
