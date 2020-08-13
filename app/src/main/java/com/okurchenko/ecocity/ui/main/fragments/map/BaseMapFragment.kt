package com.okurchenko.ecocity.ui.main.fragments.map

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import coil.Coil
import coil.api.get
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.okurchenko.ecocity.R
import com.okurchenko.ecocity.repository.model.StationItem
import com.okurchenko.ecocity.ui.base.BaseNavigationFragment
import kotlinx.coroutines.*
import timber.log.Timber

abstract class BaseMapFragment : BaseNavigationFragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var markerJob: Job
    private var isCameraMovedToMyLocation: Boolean = false
    private lateinit var currentMarker: Marker

    abstract fun mapReady()
    abstract fun markerClick(station: StationItem)

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        this.map.setOnMarkerClickListener { marker ->
            markerClick(marker.tag as StationItem)
            true
        }
        setMapStyle()
        mapReady()
    }

    override fun onPause() {
        super.onPause()
        if (::markerJob.isInitialized && !markerJob.isCancelled) markerJob.cancel()
    }

    protected fun displayCurrentLocationOnMap(location: Location) {
        val bitmapDescriptor: BitmapDescriptor? = bitmapDescriptorFromVector()
        val currentLocation = LatLng(location.latitude, location.longitude)
        val options = MarkerOptions().position(currentLocation).apply {
            bitmapDescriptor?.run { icon(bitmapDescriptor) }
        }
        if (::map.isInitialized) {
            if (::currentMarker.isInitialized) currentMarker.remove()
            currentMarker = map.addMarker(options)
        }
    }

    protected fun zoomIn() {
        if (::map.isInitialized) map.animateCamera(CameraUpdateFactory.zoomIn())
    }

    protected fun zoomOut() {
        if (::map.isInitialized) map.animateCamera(CameraUpdateFactory.zoomOut())
    }

    protected fun moveCamera(location: Location) {
        if (!isCameraMovedToMyLocation) {
            val zoomLevel = 5f
            val currentLocation = LatLng(location.latitude, location.longitude)
            if (::map.isInitialized) map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel))
            isCameraMovedToMyLocation = true
        }
    }

    protected fun displayContent(items: List<StationItem>) {
        markerJob = GlobalScope.launch(Dispatchers.Default) {
            items.forEach { station -> addMarker(station) }
        }
    }

    private suspend fun addMarker(station: StationItem) {
        val stationPosition = LatLng(station.lat, station.lon)
        val image = withContext(Dispatchers.IO) {
            Coil.get("https://eco-city.org.ua/img/levels/0-1.png").toBitmap(getMapIconSize(), getMapIconSize())
        }
        val options = MarkerOptions()
            .position(stationPosition)
            .title(station.name)
            .icon(BitmapDescriptorFactory.fromBitmap(image))
        withContext(Dispatchers.Main) {
            if (::map.isInitialized) map.addMarker(options).tag = station
        }
    }

    private fun setMapStyle() {
        try {
            val success = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style))
            if (!success) Timber.e("Style parsing failed.")
        } catch (e: Resources.NotFoundException) {
            Timber.e(e)
        }
    }

    @SuppressLint("MissingPermission")
    protected fun setLocationEnabled() {
        if (::map.isInitialized) map.isMyLocationEnabled = true
    }

    private fun bitmapDescriptorFromVector(): BitmapDescriptor? {
        context?.run {
            val vectorDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_location_city)
            val possibleBitmap = vectorDrawable?.run {
                DrawableCompat.wrap(vectorDrawable).mutate()
                val bitmap = Bitmap.createBitmap(
                    getMapIconSize(), getMapIconSize(),
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
                vectorDrawable.draw(canvas)
                bitmap
            }
            return BitmapDescriptorFactory.fromBitmap(possibleBitmap)
        } ?: return null
    }

    private fun getMapIconSize(): Int = context?.resources?.getDimensionPixelSize(R.dimen.map_icon_size) ?: 30
}