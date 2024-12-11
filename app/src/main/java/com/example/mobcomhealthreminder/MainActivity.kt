package com.example.mobcomhealthreminder

import android.os.Build
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
import com.example.mobcomhealthreminder.ui.PhysicalActivityScreen

import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mobcomhealthreminder.database.AppDatabase
import com.example.mobcomhealthreminder.ui.theme.MobComHealthReminderTheme
import com.example.mobcomhealthreminder.viewmodel.MealScheduleViewModel
import com.example.mobcomhealthreminder.viewmodel.MealScheduleViewModelFactory
import com.example.mobcomhealthreminder.utils.checkAndRequestNotificationPermission
import com.example.mobcomhealthreminder.viewmodel.PreviewMealScheduleViewModel
import com.example.mobcomhealthreminder.viewmodel.TrackingViewModel
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Daftar semua izin yang diperlukan
        val permissions = mutableListOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )

        // Tambahkan izin notifikasi untuk Android 13+ (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        // Minta semua izin
        requestPermissionLauncher.launch(permissions.toTypedArray())

        // Inisialisasi database dan DAO
        val database = (application as MyApplication).database
        val mealScheduleDao = database.mealScheduleDao()

        // Inisialisasi ViewModel dengan Factory
        val viewModel: MealScheduleViewModel by viewModels {
            MealScheduleViewModelFactory(mealScheduleDao)
        }

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
            val notificationsGranted = permissions[android.Manifest.permission.POST_NOTIFICATIONS] ?: false

            if (fineLocationGranted && coarseLocationGranted && notificationsGranted) {
                // Semua izin diberikan
            } else {
                // Beberapa izin ditolak
            }
        }
}

@Composable
fun HealthAppUI(
    mealScheduleViewModel: MealScheduleViewModel // Tambahkan parameter untuk MealScheduleViewModel
) {
    val trackingViewModel: TrackingViewModel = hiltViewModel()
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
                "Home" -> HomeScreen(
                    trackingViewModel = trackingViewModel,
                    mealScheduleViewModel = mealScheduleViewModel // Berikan MealScheduleViewModel
                )
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
    // Ambil tanggal saat ini dalam format yang diinginkan
    val currentDate = remember {
        val calendar = Calendar.getInstance()
        val formatter = java.text.SimpleDateFormat("d MMMM yyyy", java.util.Locale.getDefault())
        formatter.format(calendar.time)
    }

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
            text = currentDate,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

@Composable
fun CardSection(
    title: String,
    trackingViewModel: TrackingViewModel // Tambahkan parameter ViewModel
) {
    val trackingData by trackingViewModel.trackingData.collectAsState(initial = emptyList())

    // Panggil fetchTrackingDataForCurrentWeek saat CardSection ditampilkan
    LaunchedEffect(Unit) {
        trackingViewModel.fetchTrackingDataForCurrentWeek()
    }

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

            if (trackingData.isEmpty()) {
                Text(
                    text = "Loading data...",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            } else {
                // Ambil ringkasan total dari minggu ini
                val weeklySummary = trackingData.firstOrNull() as? Map<String, Any>

                weeklySummary?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_steps),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Steps: ${it["totalSteps"] ?: 0}",
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_physical_activity),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Distance: ${it["totalDistance"] ?: 0} Meters",
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calories),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Calories: ${it["totalCalories"] ?: 0} kcal",
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }
                } ?: run {
                    Text(
                        text = "No data available for this week.",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen(trackingViewModel: TrackingViewModel, mealScheduleViewModel: MealScheduleViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        HeaderSection()

        // This Week Physical Activity Section
        CardSection(
            title = "This Week Physical Activity",
            trackingViewModel = trackingViewModel // Melewatkan ViewModel
        )

        // Weekly Report Section
//        WeeklyReport()

        // Exercise Reminder

        // Eat Reminder
        ReminderCard(viewModel = mealScheduleViewModel) // Pass MealScheduleViewModel
    }
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
fun ReminderCard(viewModel: MealScheduleViewModel) {
    val closestSchedule = viewModel.getClosestSchedule()

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
            if (closestSchedule != null) {
                // Ambil waktu sekarang
                val currentTime = Calendar.getInstance()

                // Ambil waktu dari jadwal terdekat
                val scheduleTime = closestSchedule.time.split(":").let { timeParts ->
                    val hour = timeParts[0].toInt()
                    val minute = timeParts[1].toInt()
                    Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                }

                // Hitung selisih waktu dalam milidetik
                val timeDifferenceMillis = scheduleTime.timeInMillis - currentTime.timeInMillis

                // Konversi selisih waktu ke jam dan menit
                val hours = (timeDifferenceMillis / (1000 * 60 * 60)).toInt()
                val minutes = ((timeDifferenceMillis / (1000 * 60)) % 60).toInt()

                Text(
                    text = "Next Meal Reminder",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Text(
                    text = "Date: ${closestSchedule.date}",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    text = if (timeDifferenceMillis > 0) {
                        "Alarm in $hours Hours $minutes Minutes"
                    } else {
                        "Alarm has passed!"
                    },
                    fontSize = 14.sp,
                    color = if (timeDifferenceMillis > 0) Color.Black else Color.Red
                )
                Text(
                    text = "Details: ${closestSchedule.description}",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            } else {
                Text(
                    text = "No upcoming reminders.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
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
        HealthAppUI(mealScheduleViewModel = PreviewMealScheduleViewModel())
    }
}