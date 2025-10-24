plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // SỬA LỖI: Plugin này là bắt buộc và phải được giữ lại
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.btappthuvien"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.btappthuvien"
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
        // Nên dùng Java 17 cho các dự án mới
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    // SỬA LỖI: Xóa khối composeOptions, vì plugin 'kotlin.compose'
    // sẽ tự động quản lý phiên bản trình biên dịch (compiler version).
    /* composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    */
}

dependencies {
    // AndroidX và Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM (Quản lý phiên bản)
    implementation(platform(libs.androidx.compose.bom))

    // Thư viện Compose UI và Material 3
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // Chỉ giữ lại một dòng M3 (được quản lý bởi BOM)
    implementation(libs.androidx.compose.material3)

    // Material Icons Extended (Không cần phiên bản vì đã có BOM)
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}