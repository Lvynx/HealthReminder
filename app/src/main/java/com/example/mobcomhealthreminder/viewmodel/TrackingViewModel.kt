package com.example.mobcomhealthreminder.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

import com.google.firebase.firestore.FirebaseFirestore

class TrackingViewModel(application: Application) : AndroidViewModel(application) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    private val _locationUpdates = MutableStateFlow("No location data")
    val locationUpdates: StateFlow<String> get() = _locationUpdates

    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> get() = _steps

    private val _calories = MutableStateFlow(0.0)
    val calories: StateFlow<Double> get() = _calories

    private val _totalDistanceInKm = MutableStateFlow(0.0)
    val totalDistanceInKm: StateFlow<Double> get() = _totalDistanceInKm

    private val strideLength = 0.8 // Average stride length in meters
    private var previousLocation: Location? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val currentLocation: Location? = locationResult.lastLocation
            if (currentLocation != null) {
                if (previousLocation != null) {
                    val distanceInMeters: Float = previousLocation!!.distanceTo(currentLocation)
                    updateSteps(distanceInMeters.toDouble())
                }
                // Update current location as the previous location for next calculation
                previousLocation = currentLocation

                // Update the displayed location
                _locationUpdates.value =
                    "Lat: ${currentLocation.latitude}, Lng: ${currentLocation.longitude}"
            }
        }
    }

    private val locationRequest = LocationRequest.create().apply {
        interval = 5000
        fastestInterval = 2000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    fun startLocationUpdates() {
        if (checkPermissions()) {
            try {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            } catch (e: SecurityException) {
                _locationUpdates.value = "SecurityException: ${e.message}"
            }
        } else {
            _locationUpdates.value = "Permission not granted"
        }
    }

    fun resetLocation() {
        _locationUpdates.value = "No location data"
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun resetTrackingData() {
        _steps.value = 0
        _calories.value = 0.0
        previousLocation = null
    }

    private fun checkPermissions(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            getApplication(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            getApplication(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocationPermission || coarseLocationPermission
    }

    private fun updateSteps(distanceInMeters: Double) {
        val calculatedSteps = (distanceInMeters / strideLength).roundToInt()
        _steps.value += calculatedSteps

        val caloriesPerStep = 0.04 // Assume 0.04 calories burned per step
        _calories.value += calculatedSteps * caloriesPerStep
    }

    fun saveDataToCloud(steps: Int, calories: Double) {
        val firestore = FirebaseFirestore.getInstance()
        val data = hashMapOf(
            "steps" to steps,
            "calories" to calories,
            "distance_km" to totalDistanceInKm.value,
        )

        firestore.collection("tracking_data")
            .add(data)
            .addOnSuccessListener { documentReference ->
                _locationUpdates.value = "Data saved successfully"
            }
            .addOnFailureListener { e ->
                _locationUpdates.value = "Error saving data: ${e.message}"
            }
    }




}
