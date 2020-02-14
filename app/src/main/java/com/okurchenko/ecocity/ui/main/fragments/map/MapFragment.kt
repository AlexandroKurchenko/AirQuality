package com.okurchenko.ecocity.ui.main.fragments.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.okurchenko.ecocity.R
import com.okurchenko.ecocity.ui.main.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MapFragment : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance(): MapFragment {
            return MapFragment()
        }
    }

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_map, container, false)
        subscribeToViewModelUpdate()
        return root
    }


    private fun subscribeToViewModelUpdate() {

    }
}