package com.okurchenko.ecocity.ui.main.fragments.map

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import com.okurchenko.ecocity.R
import com.okurchenko.ecocity.databinding.FragmentMapBinding
import com.okurchenko.ecocity.repository.model.StationItem
import com.okurchenko.ecocity.ui.main.MainActivity
import com.okurchenko.ecocity.ui.main.StationListActor
import com.okurchenko.ecocity.ui.main.StationListState
import com.okurchenko.ecocity.ui.main.StationsViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val CLICKED_MARKER = "CLICKED_MARKER"

class MapFragment : BaseMapFragment() {

    private val stationsViewModel by sharedViewModel<StationsViewModel>()
    private val mapLocationViewModel by viewModel<MapLocationViewModel>()
    private lateinit var binding: FragmentMapBinding
    private lateinit var actor: StationListActor
    private lateinit var mapActoractor: StationListActor
    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranded ->
            if (isGranded) setLocationEnabled()
        }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = bindContentView(inflater, R.layout.fragment_map, container)
        binding.lifecycleOwner = this
        binding.mapView.onCreate(savedInstanceState)
        binding.mapInfoWindow.openHistoryDetails.setOnClickListener { view ->
            view.tag?.run { if (::actor.isInitialized) actor.clickItem(view.tag as Int) }
        }
        binding.zoomIn.setOnClickListener { zoomIn() }
        binding.zoomOut.setOnClickListener { zoomOut() }
        binding.mapView.addMapViewTouch(::isViewPagerUserInputEnabled)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actor = StationListActor(stationsViewModel::takeAction)
        mapActoractor = StationListActor (mapLocationViewModel::takeAction )
        binding.mapView.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        stationsViewModel.getNavigationEvents().observe(viewLifecycleOwner, navObserver)
        if (::mapActoractor.isInitialized) {
            mapActoractor.requestLocationUpdate()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapInfoWindow.station?.run {
            outState.putParcelable(CLICKED_MARKER, binding.mapInfoWindow.station)
        }
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null && savedInstanceState.containsKey(CLICKED_MARKER)) {
            binding.mapInfoWindow.station = savedInstanceState.getParcelable(CLICKED_MARKER)
        }
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
        stationsViewModel.getNavigationEvents().removeObserver(navObserver)
        if (::mapActoractor.isInitialized) {
            mapActoractor.stopLocationUpdate()
        }
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun mapReady() {

        subscribeToViewModelUpdate()
        activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun markerClick(station: StationItem) {
        binding.mapInfoWindow.station = station
    }

    private fun subscribeToViewModelUpdate() {
        stationsViewModel.getState().observe(viewLifecycleOwner, { state ->
            binding.state = state
            if (state is StationListState.StationItemsLoaded) {
                displayContent(state.data)
            }
        })
        mapLocationViewModel.getLocationUpdate().observe(viewLifecycleOwner, Observer { updateLocation(it) })
    }

    private fun updateLocation(it: Location) {
        displayCurrentLocationOnMap(it)
        moveCamera(it)
    }

    private fun isViewPagerUserInputEnabled(enabled: Boolean) {
        (activity as? MainActivity)?.setViewPager2UserInputEnabled(enabled)
    }
}