package com.okurchenko.ecocity.ui.main.fragments.stations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
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

    private val viewModel by viewModel<MainViewModel>()
    private lateinit var adapter: StationsAdapter
    private lateinit var binding: FragmentStationsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = bindContentView(inflater, R.layout.fragment_stations, container)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val actor = StationListActor(viewModel::takeAction)
        binding.swipeToRefreshLayout.setOnRefreshListener {
            actor.refresh()
            binding.swipeToRefreshLayout.isRefreshing = false
        }
        adapter = StationsAdapter(actor)
        binding.stationsList.adapter = adapter
        context?.let { binding.stationsList.addItemDecoration(ItemOffsetDecoration(it)) }
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

    private fun subscribeToViewModelUpdate() {
        viewModel.getState().observe(viewLifecycleOwner, Observer { state ->
            binding.state = state
            if (state is StationListState.StationItemsLoaded) {
                displayContent(state.data)
            }
        })
    }

    private fun displayContent(data: List<StationItem>) {
        if (::adapter.isInitialized) {
            adapter.submitData(data)
            runAfterLoadingLayoutAnimation()
        }
    }

    private fun runAfterLoadingLayoutAnimation() = binding.stationsList.apply {
        val resourceId = R.anim.layout_animation_from_bottom
        layoutAnimation = AnimationUtils.loadLayoutAnimation(context, resourceId)
        adapter?.notifyDataSetChanged()
        scheduleLayoutAnimation()
    }
}