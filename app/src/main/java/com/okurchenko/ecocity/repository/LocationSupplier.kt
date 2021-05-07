package com.okurchenko.ecocity.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LocationListener(applicationContext: Context) :
    OnSuccessListener<Location>,
    OnFailureListener,
    LiveData<Location>() {

    private var locationProvider: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(applicationContext)


    @SuppressLint("MissingPermission")
    suspend fun getLastLocation(): Location? {
        return suspendCoroutine { continuation ->
            locationProvider.lastLocation
                .addOnSuccessListener { continuation.resume(it) }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }
    }

    @SuppressLint("MissingPermission")
    @MainThread
    fun initLocationUpdates() {
        locationProvider.lastLocation
            .addOnSuccessListener { location -> location?.run { value = location } }
            .addOnFailureListener { ex -> Timber.i(ex.toString()) }
        val request = createLocationRequest()
        Looper.myLooper()?.let {
            locationProvider.requestLocationUpdates(request, locationCallBack, it)
        }

    }

    fun destroyLocationUpdate() {
        locationProvider.removeLocationUpdates(locationCallBack)
    }


    private val locationCallBack = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            value = result.lastLocation
        }
    }

    override fun onSuccess(location: Location?) {
        processSuccessLocation(location)
    }

    override fun onFailure(ex: Exception) {
        Timber.e(ex)
    }


    private fun processSuccessLocation(location: Location?) {
        if (location != null) {
            value = location
        }
    }

    private fun createLocationRequest(): LocationRequest {
        val mLocationRequest = LocationRequest.create()
        mLocationRequest.interval = 10 * 1000
        mLocationRequest.fastestInterval = 1 * 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        return mLocationRequest
    }
}



