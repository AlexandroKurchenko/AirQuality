package com.okurchenko.ecocity.ui.main.fragments.list

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
import com.okurchenko.ecocity.ui.main.fragments.StationsActor
import com.okurchenko.ecocity.ui.main.fragments.StationsState
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class StationsFragment : Fragment() {

    private val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var adapter: StationsAdapter
    private lateinit var loadingView: ProgressBar
    private lateinit var errorView: AppCompatTextView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_stations_list, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.stationsList)
        loadingView = view.findViewById(R.id.loadingView)
        errorView = view.findViewById(R.id.errorView)
        val actor = StationsActor(viewModel::takeAction)
        adapter = StationsAdapter(actor)
        recyclerView.adapter = adapter
        subscribeToViewModelUpdate()
        return view
    }

    private fun subscribeToViewModelUpdate() {
        viewModel.getState().observe(viewLifecycleOwner, Observer {
            when (it) {
                is StationsState.StationItemsContent -> {
                    Timber.d("Data fetched, size is = ${it.data.size}")
                    if (::adapter.isInitialized) adapter.submitData(it.data)
                    loadingView.visibility = View.GONE
                    errorView.visibility = View.GONE
                }
                is StationsState.Error -> {
                    errorView.visibility = View.VISIBLE
                    loadingView.visibility = View.GONE
                    Timber.e("State is error")
                }
                is StationsState.Loading -> {

                    Timber.d("State is loading")
                    loadingView.visibility = View.VISIBLE
                    errorView.visibility = View.GONE
                }
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = StationsFragment()
    }
}
