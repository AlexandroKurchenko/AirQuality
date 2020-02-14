package com.okurchenko.ecocity.ui.details.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.okurchenko.ecocity.R
import com.okurchenko.ecocity.ui.details.HistoryActor
import com.okurchenko.ecocity.ui.details.HistoryViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DetailsFragment : Fragment() {

    companion object {
        const val DETAILS_ID_BUNDLE = "DETAILS_ID_BUNDLE"
        @JvmStatic
        fun newInstance(stationId: Int) = DetailsFragment().apply {
            val args = Bundle()
            args.putInt(DETAILS_ID_BUNDLE, stationId)
            this.arguments = args
        }
    }

    private val viewModel by sharedViewModel<HistoryViewModel>()
    private lateinit var actor: HistoryActor
    private lateinit var recyclerView: RecyclerView
    private lateinit var errorVew: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_details, container, false)
        actor = HistoryActor(viewModel::takeAction)
        setupToolBar(rootView)
        recyclerView = rootView.findViewById(R.id.detailsList)
        errorVew = rootView.findViewById(R.id.errorView)

//        adapter = DetailsAdapter(actor)
//        recyclerView.adapter = adapter
//        subscribeToViewModelUpdate()
//        loadItems()
        return rootView
    }

    private fun setupToolBar(rootView: View) {
        val toolBar: Toolbar = rootView.findViewById(R.id.toolBar)
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolBar.title = getString(R.string.details_text)
        toolBar.setNavigationOnClickListener {
            if (::actor.isInitialized) actor.openHistory() else requireActivity().finish()
        }
    }

    private fun getStationId(): Int? = arguments?.getInt(HistoryFragment.HISTORY_DETAILS_ID_BUNDLE, 246)
}