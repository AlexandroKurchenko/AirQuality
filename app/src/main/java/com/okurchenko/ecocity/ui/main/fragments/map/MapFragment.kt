package com.okurchenko.ecocity.ui.main.fragments.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.okurchenko.ecocity.R
import com.okurchenko.ecocity.databinding.FragmentMapBinding
import com.okurchenko.ecocity.repository.model.StationItem
import com.okurchenko.ecocity.ui.main.MainActivity
import com.okurchenko.ecocity.ui.main.StationListActor
import com.okurchenko.ecocity.ui.main.StationListState
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val REQUEST_CODE_FOREGROUND = 1567

class MapFragment2 : BaseMapFragment() {

    private val viewModel by viewModel<MapViewModel>()
    private lateinit var binding: FragmentMapBinding
    private lateinit var actor: StationListActor

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = bindContentView(inflater, R.layout.fragment_map, container)
        binding.lifecycleOwner = this
        binding.mapView.onCreate(savedInstanceState)
        binding.openHistoryDetails.setOnClickListener { view -> view.tag?.run { actor.clickItem(view.tag as Int) } }
        binding.zoomIn.setOnClickListener { zoomIn() }
        binding.zoomOut.setOnClickListener { zoomOut() }
        binding.mapView.addMapViewTouch(::isViewPagerUserInputEnabled)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.getMapAsync(this)

    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        viewModel.getNavigationEvents().observe(viewLifecycleOwner, navObserver)
        if (::actor.isInitialized) actor.requestLocationUpdate()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //TODO save selected binding item from markerClick()
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
        viewModel.getNavigationEvents().removeObserver(navObserver)
        if (::actor.isInitialized) actor.stopLocationUpdate()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_FOREGROUND) {
            handlePermission(grantResults)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun mapReady() {
        actor = StationListActor(viewModel::takeAction)
        subscribeToViewModelUpdate()
        requestPermission()
    }

    override fun markerClick(station: StationItem) {
        binding.station = station
    }

    private fun subscribeToViewModelUpdate() {
        viewModel.getState().observe(viewLifecycleOwner, Observer { state ->
            binding.state = state
            if (state is StationListState.StationItemsLoaded) {
                displayContent(state.data)
            }
        })
        viewModel.getLocationUpdate().observe(viewLifecycleOwner, Observer { updateLocation(it) })
    }

    private fun requestPermission() {
        if (hasLocationPermission()) {
            permissionGranted()
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_FOREGROUND)
        }
    }

    private fun hasLocationPermission(): Boolean =
        context?.run {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } ?: false

    private fun handlePermission(grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
            permissionGranted()
        }
    }

    private fun permissionGranted() {
        setLocationEnabled()
    }

    private fun updateLocation(it: Location) {
        val currentLatLon = "${it.latitude}, ${it.longitude}"
        binding.currentLocationLabel.text =
            context?.resources?.getString(R.string.current_location_label, currentLatLon)
        displayCurrentLocation(it)
        moveCamera(it)
    }

    private fun isViewPagerUserInputEnabled(enabled: Boolean) {
        (activity as? MainActivity)?.getViewPager2()?.isUserInputEnabled = enabled
    }
}