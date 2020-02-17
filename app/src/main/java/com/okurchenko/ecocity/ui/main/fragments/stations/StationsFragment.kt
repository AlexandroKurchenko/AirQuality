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
import com.okurchenko.ecocity.R
import com.okurchenko.ecocity.ui.main.MainViewModel
import com.okurchenko.ecocity.ui.main.fragments.StationListActor
import com.okurchenko.ecocity.ui.main.fragments.StationListState
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class StationsFragment : Fragment() {

    private val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var adapter: StationsAdapter
    private lateinit var loadingView: ProgressBar
    private lateinit var errorView: AppCompatTextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_stations_list, container, false)
        recyclerView = view.findViewById(R.id.stationsList)
        loadingView = view.findViewById(R.id.loadingView)
        errorView = view.findViewById(R.id.errorView)
        val actor = StationListActor(viewModel::takeAction)
        adapter = StationsAdapter(actor)
        recyclerView.adapter = adapter
        subscribeToViewModelUpdate()
        return view
    }

    private fun subscribeToViewModelUpdate() {
        viewModel.getState().observe(viewLifecycleOwner, Observer {
            if (it is StationListState.StationItemsContent && ::adapter.isInitialized) {
                adapter.submitData(it.data)
            }
            manageElementsVisibility(it)
        })
    }

    private fun manageElementsVisibility(stationState: StationListState) {
        when (stationState) {
            is StationListState.StationItemsContent -> {
                Timber.d("Data fetched, size is = ${stationState.data.size}")
                loadingView.visibility = View.GONE
                errorView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            is StationListState.Error -> {
                Timber.e("State is error")
                errorView.visibility = View.VISIBLE
                loadingView.visibility = View.GONE
                recyclerView.visibility = View.GONE
            }
            is StationListState.Loading -> {
                Timber.d("State is loading")
                recyclerView.visibility = View.GONE
                loadingView.visibility = View.VISIBLE
                errorView.visibility = View.GONE
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = StationsFragment()
    }
}
