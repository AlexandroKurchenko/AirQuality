package com.okurchenko.ecocity.ui.details.fragments.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.okurchenko.ecocity.R
import com.okurchenko.ecocity.databinding.FragmentDetailsBinding
import com.okurchenko.ecocity.ui.details.fragments.BaseHistoryDetailsFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

private const val STATION_ID_EXT = "STATION_ID_EXT"
private const val TIME_SHIFT_EXT = "TIME_SHIFT_EXT"

class DetailsFragment : BaseHistoryDetailsFragment() {

    companion object {
        @JvmStatic
        fun newInstance(stationId: Int, timeShift: Int) = DetailsFragment().apply {
            val args = Bundle()
            args.putInt(TIME_SHIFT_EXT, timeShift)
            args.putInt(STATION_ID_EXT, stationId)
            this.arguments = args
        }
    }

    private val viewModel by sharedViewModel<DetailsViewModel>()
    private lateinit var actor: DetailsActor
    private lateinit var errorVew: TextView
    private lateinit var binding: FragmentDetailsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false)
        actor = DetailsActor(viewModel::takeAction)
        setupToolBar()
        subscribeToViewModelUpdate()
        loadItems()
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home && ::actor.isInitialized) {
            actor.backToHistory()
        } else requireActivity().finish()
        return super.onOptionsItemSelected(item)
    }

    private fun setupToolBar() {
        setHasOptionsMenu(true)
        requireActivity().actionBar?.title = getString(R.string.details_text)
    }

    private fun loadItems() {
        val stationId = getStationId()
        val timeShift = getTimeShift()
        if (::actor.isInitialized && stationId != null && timeShift != null) actor.fetchDetails(stationId, timeShift)
    }

    private fun subscribeToViewModelUpdate() {
        viewModel.getState().observe(viewLifecycleOwner, Observer {
            when (it) {
                is DetailsState.DetailsItemLoaded -> binding.item = it.item
                DetailsState.DetailsItemLoading -> {}
                DetailsState.FailLoading -> {}
                is DetailsState.StateEvent -> eventListener?.processEvent(it.event)
            }
        })
    }

    private fun getStationId(): Int? = arguments?.getInt(STATION_ID_EXT, 246)
    private fun getTimeShift(): Int? = arguments?.getInt(TIME_SHIFT_EXT, 0)
}