plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt") // Aktifkan KAPT untuk Room
}

android {
    namespace = "com.example.mobcomhealthreminder"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mobcomhealthreminder"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.ui) // Untuk elemen UI Compose
    implementation(libs.androidx.ui.graphics) // Untuk menggunakan painterResource
    implementation(libs.androidx.ui.tooling.preview) // Untuk preview
    implementation(libs.androidx.material3) // Untuk Material Design 3
    implementation("androidx.compose.runtime:runtime:1.5.2") // Untuk remember dan mutableStateOf

    // Jetpack Compose
    implementation("androidx.compose.ui:ui:1.5.2")
    implementation("androidx.compose.material3:material3:1.1.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1") // ViewModel untuk Compose
    implementation("androidx.compose.runtime:runtime:1.5.2")

    // Kotlin extensions
    implementation("androidx.compose.runtime:runtime-livedata:1.5.2")

    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1") // Gunakan KAPT untuk Kotlin
    implementation("androidx.room:room-ktx:2.6.1") // Opsional, untuk coroutine

    implementation("androidx.compose.material:material-icons-extended:1.5.2")
    implementation("androidx.compose.material3:material3:1.1.0")

    implementation(platform("androidx.compose:compose-bom:2023.08.00"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}