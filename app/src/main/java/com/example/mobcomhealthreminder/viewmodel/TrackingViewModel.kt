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
import java.util.Date

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Calendar

class TrackingViewModel(application: Application) : AndroidViewModel(application) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    private val _locationUpdates = MutableStateFlow("No location data")
    val locationUpdates: StateFlow<String> get() = _locationUpdates

    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> get() = _steps

    private val _calories = MutableStateFlow(0.0)
    val calories: StateFlow<Double> get() = _calories

    private val _totalDistanceInMeters = MutableStateFlow(0.0)
    val totalDistanceInMeters: StateFlow<Double> get() = _totalDistanceInMeters

    private val strideLength = 0.8 // Average stride length in meters
    private var previousLocation: Location? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val currentLocation: Location? = locationResult.lastLocation
            if (currentLocation != null) {
                if (previousLocation != null) {
                    val distanceInMeters: Float = previousLocation!!.distanceTo(currentLocation)
                    _totalDistanceInMeters.value += distanceInMeters
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
        val calculatedSteps = (distanceInMeters / (1000.0 / 1250.0)).roundToInt() // Updated stride length calculation
        _steps.value += calculatedSteps

        val caloriesPerStep = 0.04 // Assume 0.04 calories burned per step
        _calories.value += calculatedSteps * caloriesPerStep
    }

    fun saveDataToCloud(steps: Int, calories: Double, distanceMeters: Double) {
        val firestore = FirebaseFirestore.getInstance()
        val data = hashMapOf(
            "steps" to steps,
            "calories" to calories,
            "distance_meters" to distanceMeters,
            "date" to Date()
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

    //Fetch
    private val firestore = FirebaseFirestore.getInstance()
    // MutableStateFlow untuk menyimpan data yang diambil
    private val _trackingData = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val trackingData: StateFlow<List<Map<String, Any>>> get() = _trackingData
    // Fungsi untuk mengambil data dari Firestore
    fun fetchTrackingData() {
        firestore.collection("tracking_data")
            .get()
            .addOnSuccessListener { documents ->
                val dataList = documents.map { it.data }
                _trackingData.value = dataList
            }
            .addOnFailureListener { exception ->
                // Handle error, misalnya log error
                _trackingData.value = emptyList()
            }
    }

    fun fetchTrackingDataForCurrentWeek() {
        val calendar = Calendar.getInstance()

        // Tentukan hari pertama minggu ini (misal, Senin)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfWeek = calendar.time

        // Tentukan hari terakhir minggu ini (Minggu, akhir pekan)
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfWeek = calendar.time

        // Query Firestore berdasarkan tanggal dalam rentang minggu ini
        firestore.collection("tracking_data")
            .whereGreaterThanOrEqualTo("date", startOfWeek)
            .whereLessThanOrEqualTo("date", endOfWeek)
            .orderBy("date", Query.Direction.ASCENDING) // Urutkan berdasarkan tanggal
            .get()
            .addOnSuccessListener { documents ->
                val dataList = documents.map { it.data }
                _trackingData.value = dataList // Tetapkan semua data ke _trackingData

                // Menjumlahkan data selama seminggu
                val totalSteps = dataList.sumOf { (it["steps"] as? Number)?.toInt() ?: 0 }
                val totalDistance = dataList.sumOf { (it["distance_meters"] as? Number)?.toDouble() ?: 0.0 }.toInt()
                val totalCalories = dataList.sumOf { (it["calories"] as? Number)?.toDouble() ?: 0.0 }.toInt()

                // Buat hasil total yang bisa ditampilkan
                val weeklySummary = mapOf(
                    "totalSteps" to totalSteps,
                    "totalDistance" to totalDistance,
                    "totalCalories" to totalCalories
                )
                _trackingData.value = listOf(weeklySummary) // Atur ringkasan data minggu ini
            }
            .addOnFailureListener { exception ->
                // Handle error, misalnya log error
                _trackingData.value = emptyList()
            }
    }
}
