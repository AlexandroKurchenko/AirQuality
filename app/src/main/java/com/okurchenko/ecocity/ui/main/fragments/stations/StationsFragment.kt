package com.okurchenko.ecocity.ui.main.fragments.stations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.okurchenko.ecocity.R
import com.okurchenko.ecocity.databinding.FragmentStationsBinding
import com.okurchenko.ecocity.repository.model.StationItem
import com.okurchenko.ecocity.ui.base.BaseNavigationFragment
import com.okurchenko.ecocity.ui.base.ItemOffsetDecoration
import com.okurchenko.ecocity.ui.main.MainViewModel
import com.okurchenko.ecocity.ui.main.StationListActor
import com.okurchenko.ecocity.ui.main.StationListState
import org.koin.androidx.viewmodel.ext.android.viewModel

class StationsFragment : BaseNavigationFragment() {

    internal val viewModel by viewModel<MainViewModel>()
    private lateinit var stationsAdapter: StationsAdapter
    private lateinit var binding: FragmentStationsBinding
    private lateinit var actor: StationListActor

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = bindContentView(inflater, R.layout.fragment_stations, container)
        binding.lifecycleOwner = this@StationsFragment.viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actor = StationListActor(viewModel::takeAction)
        binding.stationContent.actor = actor
        binding.stationContent.swipeToRefreshLayout.setOnRefreshListener { onRefresh() }
        stationsAdapter = StationsAdapter(actor)
        binding.stationContent.stationsList.apply {
            adapter = stationsAdapter
            addItemDecoration(ItemOffsetDecoration())
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    updateFabState(newState)
                }
            })
        }
        subscribeToViewModelUpdate()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getNavigationEvents().observe(viewLifecycleOwner, navObserver)
    }

    override fun onPause() {
        super.onPause()
        viewModel.getNavigationEvents().removeObserver(navObserver)
    }

    private fun onRefresh() {
        val sorting = when (binding.stationContent.floatingActionButton.tag) {
            StationSort.SortByName -> StationSort.SortByName
            else -> StationSort.SortByDistance
        }
        actor.refresh(sorting)
        binding.stationContent.swipeToRefreshLayout.isRefreshing = false
    }

    private fun subscribeToViewModelUpdate() {
        viewModel.getState().observe(viewLifecycleOwner, Observer { state ->
            binding.state = state
            if (state is StationListState.StationItemsLoaded) {
                displayContent(state.data)
                displayFabResources(state.sort)
            }
        })
    }

    private fun displayFabResources(sort: StationSort) {
        when (sort) {
            StationSort.SortByName -> {
                binding.stationContent.floatingActionButton.tag = StationSort.SortByDistance
                binding.stationContent.floatingActionButton.setImageResource(R.drawable.ic_sort_black_24dp)
            }
            else -> {
                binding.stationContent.floatingActionButton.tag = StationSort.SortByName
                binding.stationContent.floatingActionButton.setImageResource(R.drawable.ic_sort_by_alpha_black_24dp)
            }
        }
    }

    internal fun updateFabState(newState: Int) {
        when (newState) {
            RecyclerView.SCROLL_STATE_IDLE -> binding.stationContent.floatingActionButton.show()
            RecyclerView.SCROLL_STATE_DRAGGING -> binding.stationContent.floatingActionButton.hide()
        }
    }

    private fun displayContent(data: List<StationItem>) {
        if (::stationsAdapter.isInitialized) {
            stationsAdapter.submitData(data)
            runAfterLoadingLayoutAnimation()
        }
    }

    private fun runAfterLoadingLayoutAnimation() = binding.stationContent.stationsList.apply {
        val resourceId = R.anim.layout_animation_from_bottom
        layoutAnimation = AnimationUtils.loadLayoutAnimation(this@StationsFragment.requireContext(), resourceId)
        adapter?.notifyDataSetChanged()
        scheduleLayoutAnimation()
    }
}