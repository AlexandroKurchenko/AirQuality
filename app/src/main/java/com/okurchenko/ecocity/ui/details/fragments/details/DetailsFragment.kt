package com.okurchenko.ecocity.ui.details.fragments.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.okurchenko.ecocity.R
import com.okurchenko.ecocity.databinding.FragmentDetailsBinding
import com.okurchenko.ecocity.ui.base.BaseHistoryDetailsFragment
import com.okurchenko.ecocity.ui.details.HistoryDetailsActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

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

    private val viewModel by viewModel<DetailsViewModel>()
    private lateinit var actor: DetailsActor
    private lateinit var binding: FragmentDetailsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_details, container, false)
        setupToolBar()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actor = DetailsActor(viewModel::takeAction)
        subscribeToViewModelUpdate()
        if (savedInstanceState == null) {
            loadItems()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getNavigationEvents().observe(viewLifecycleOwner, navObserver)
    }

    override fun onPause() {
        super.onPause()
        viewModel.getNavigationEvents().removeObserver(navObserver)
    }

    override fun onBackActionPressed() {
        if (::actor.isInitialized) {
            actor.backToHistory()
        } else requireActivity().finish()
    }

    private fun setupToolBar() {
        setHasOptionsMenu(true)
        (requireActivity() as? HistoryDetailsActivity)?.supportActionBar?.title = getString(R.string.details_title)
    }

    private fun loadItems() {
        val stationId = getStationId()
        val timeShift = getTimeShift()
        if (::actor.isInitialized && stationId != null && timeShift != null) actor.fetchDetails(stationId, timeShift)
    }

    private fun subscribeToViewModelUpdate() {
        viewModel.getState().observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is DetailsState.DetailsItemLoaded -> binding.item = state.item
            }
        })
    }

    private fun getStationId(): Int? = arguments?.getInt(STATION_ID_EXT, 246)
    private fun getTimeShift(): Int? = arguments?.getInt(TIME_SHIFT_EXT, 0)
}