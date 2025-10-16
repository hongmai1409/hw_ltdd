plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> 5cbef83aa5a495c8655b787d4555da75026e9944
    alias(libs.plugins.kotlin.compose)
}

android {
<<<<<<< HEAD
    namespace = "com.example.baitaptuan3"
    compileSdk {
        version = release(36)
    }
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    defaultConfig {
        applicationId = "com.example.baitaptuan3"
=======
    namespace = "com.example.jetpackcompose"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.jetpackcompose"
=======
}

android {
<<<<<<< HEAD
    namespace = "com.example.myapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.myapp"
=======
    namespace = "com.example.hw2_bt3"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.hw2_bt3"
>>>>>>> 13268ee (hw2)
>>>>>>> 50aa58d3afa097313323db823eb306d8e4335611
>>>>>>> 5cbef83aa5a495c8655b787d4555da75026e9944
        minSdk = 24
        targetSdk = 36
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
<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> 5cbef83aa5a495c8655b787d4555da75026e9944
    buildFeatures {
        compose = true
    }
}

dependencies {
<<<<<<< HEAD
    implementation("io.coil-kt:coil-compose:2.6.0")

    implementation(platform("androidx.compose:compose-bom:2024.08.00"))

// UI + Material 3
    implementation("androidx.compose.material3:material3")

// Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.8.0")

// Preview support
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

// Activity Compose
    implementation("androidx.activity:activity-compose:1.9.2")

=======
>>>>>>> 5cbef83aa5a495c8655b787d4555da75026e9944
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
<<<<<<< HEAD
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
=======
>>>>>>> 5cbef83aa5a495c8655b787d4555da75026e9944
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
<<<<<<< HEAD
=======
=======
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
>>>>>>> 50aa58d3afa097313323db823eb306d8e4335611
>>>>>>> 5cbef83aa5a495c8655b787d4555da75026e9944
}