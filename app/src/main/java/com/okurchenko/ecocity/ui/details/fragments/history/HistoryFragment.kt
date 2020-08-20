package com.okurchenko.ecocity.ui.details.fragments.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.okurchenko.ecocity.R
import com.okurchenko.ecocity.ui.base.BaseHistoryDetailsFragment
import com.okurchenko.ecocity.ui.base.ItemOffsetDecoration
import com.okurchenko.ecocity.ui.details.HistoryDetailsActivity
import com.okurchenko.ecocity.ui.details.fragments.history.adapter.HistoryLoadStateAdapter
import com.okurchenko.ecocity.ui.details.fragments.history.adapter.HistoryPagingAdapter
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.coroutines.flow.collectLatest

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

    private val viewModel: HistoryViewModel by viewModels {
        object : AbstractSavedStateViewModelFactory(this, null) {
            override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
                return HistoryViewModel(handle) as T
            }
        }
    }
    private lateinit var actor: HistoryListActor
    private lateinit var adapter: HistoryPagingAdapter//DetailsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_history, container, false)
        setupToolBar()
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actor = HistoryListActor(viewModel::takeAction)
        initAdapter()
        context?.let { context -> historyList.addItemDecoration(ItemOffsetDecoration(context)) }
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

    override fun onBackActionPressed() {
        if (::actor.isInitialized) {
            actor.openMain()
        } else {
            requireActivity().finish()
        }
    }

    private fun initAdapter() {
        adapter = HistoryPagingAdapter(actor)
        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        historyList.adapter = adapter
        historyList.adapter = adapter.withLoadStateFooter(
            footer = HistoryLoadStateAdapter { adapter.retry() }
        )
    }

    private fun setupToolBar() {
        setHasOptionsMenu(true)
        (requireActivity() as? HistoryDetailsActivity)?.supportActionBar?.title = getString(R.string.history_title)
    }

    private fun subscribeToViewModelUpdate() {
        lifecycleScope.launchWhenCreated {
            getStationId()?.let { stationId ->
                viewModel.getAllHistoryItems(stationId).collectLatest {
                    adapter.submitData(it)
                }
            }
        }
    }

    private fun getStationId(): Int? = arguments?.getInt(STATION_ID, 246)
}