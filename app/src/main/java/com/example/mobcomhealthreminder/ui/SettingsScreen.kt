package com.example.mobcomhealthreminder.ui

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobcomhealthreminder.viewmodel.TrackingViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.MarkerState
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(viewModel: TrackingViewModel = viewModel()) {
    val locationUpdates by viewModel.locationUpdates.collectAsState()
    val steps by viewModel.steps.collectAsState()
    val calories by viewModel.calories.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "GPS Tracking",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Location Updates
        Text(
            text = locationUpdates,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Steps
        Text(
            text = "Steps: $steps",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Calories
        Text(
            text = "Calories: ${calories.roundToInt()} kcal",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Start Tracking Button
        Button(
            onClick = {
                viewModel.startLocationUpdates()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Tracking")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stop Tracking Button
        Button(
            onClick = {
                viewModel.stopLocationUpdates()
                viewModel.resetLocation()
                viewModel.saveDataToCloud(viewModel.steps.value, viewModel.calories.value, viewModel.totalDistanceInMeters.value)
                viewModel.resetTrackingData()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Stop Tracking and Save")
        }
    }
}