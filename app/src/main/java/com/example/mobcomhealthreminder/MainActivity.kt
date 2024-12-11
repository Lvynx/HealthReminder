package com.example.mobcomhealthreminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobcomhealthreminder.ui.theme.MobComHealthReminderTheme
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.mobcomhealthreminder.ui.WorkoutScreen
import com.example.mobcomhealthreminder.ui.NutritionScreen
import com.example.mobcomhealthreminder.utils.checkAndRequestNotificationPermission

import com.example.mobcomhealthreminder.ui.SettingsScreen

import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.mobcomhealthreminder.database.AppDatabase
import com.example.mobcomhealthreminder.ui.theme.MobComHealthReminderTheme
import com.example.mobcomhealthreminder.viewmodel.MealScheduleViewModel
import com.example.mobcomhealthreminder.viewmodel.MealScheduleViewModelFactory
import com.example.mobcomhealthreminder.utils.checkAndRequestNotificationPermission
import com.example.mobcomhealthreminder.viewmodel.PreviewMealScheduleViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        // Inisialisasi database dan DAO
        val database = (application as MyApplication).database
        val mealScheduleDao = database.mealScheduleDao()

        // Inisialisasi ViewModel dengan Factory
        val viewModel: MealScheduleViewModel by viewModels {
            MealScheduleViewModelFactory(mealScheduleDao)
        }

        // Periksa dan minta izin notifikasi
        checkAndRequestNotificationPermission()

        // Menampilkan UI
        setContent {
            MobComHealthReminderTheme {
                HealthAppUI(viewModel)
            }
        }
    }
    private val requestPermissionLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fineLocationGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fineLocationGranted || coarseLocationGranted) {
                // Permissions granted, do something
            } else {
                // Permissions denied, handle it
            }
        }
}

@Composable
fun HealthAppUI(viewModel: MealScheduleViewModel) {
    var selectedItem by remember { mutableStateOf("Home") } // Default menu "Home"

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedItem,
                onItemSelected = { selectedItem = it } // Perbarui state saat item di-click
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedItem) {
                "Home" -> HomeScreen()
                "Physical Activity" -> PhysicalActivityScreen()
                "Workout" -> WorkoutScreen()
                "Nutrition" -> NutritionScreen()
                "Settings" -> SettingsScreen()
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Health App",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = "8 December 2024",
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

@Composable
fun CardSection(title: String, items: List<Pair<String, Int>>) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
            items.forEach { (text, icon) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = text, fontSize = 16.sp, color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        HeaderSection()

        // Today Physical Activity Section
        CardSection(
            title = "Today Physical Activity",
            items = listOf(
                "5000 Steps" to R.drawable.ic_steps,
                "3 KM" to R.drawable.ic_physical_activity,
                "1800 Calories burn" to R.drawable.ic_calories
            )
        )

        // Weekly Report Section
        WeeklyReport()

        // Exercise Reminder
        ReminderCard(
            title = "Exercise Reminder",
            time = "Alarm in 2 Hours 30 Minutes\nSun, 8 Dec, 04:00 PM"
        )

        // Eat Reminder
        ReminderCard(
            title = "Eat Reminder",
            time = "Alarm in 3 Hours 30 Minutes\nSun, 8 Dec, 05:00 PM"
        )
    }
}

@Composable
fun PhysicalActivityScreen() {
    Text(
        text = "Physical Activity Screen",
        modifier = Modifier.fillMaxSize(),
        style = MaterialTheme.typography.headlineMedium
    )
}

@Composable
fun WeeklyReport() {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Weekly Report",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.Blue, shape = RoundedCornerShape(50))
            ) {
                Text(
                    text = "R",
                    color = Color.White,
                    fontSize = 24.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun ReminderCard(title: String, time: String) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
            Text(
                text = time,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem("Home", R.drawable.ic_home),
        BottomNavItem("Physical Activity", R.drawable.ic_physical_activity),
        BottomNavItem("Workout", R.drawable.ic_workout),
        BottomNavItem("Nutrition", R.drawable.ic_food),
        BottomNavItem("Settings", R.drawable.ic_settings)
    )

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Black
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.name,
                        modifier = Modifier.size(24.dp)
                    )
                },
                selected = item.name == selectedItem, // Bandingkan dengan item yang dipilih
                onClick = { onItemSelected(item.name) },
                alwaysShowLabel = false
            )
        }
    }
}

data class BottomNavItem(val name: String, val icon: Int)

@Preview(showBackground = true)
@Composable
fun HealthAppUIPreview() {
    MobComHealthReminderTheme {
        HealthAppUI(viewModel = PreviewMealScheduleViewModel())
    }
}