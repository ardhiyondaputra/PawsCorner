plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)

    id("kotlin-parcelize")
}

android {
    namespace = "kelompok4.uasmobile2.pawscorner"
    compileSdk = 35

    defaultConfig {
        applicationId = "kelompok4.uasmobile2.pawscorner"
        minSdk = 24
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

    implementation ("com.google.firebase:firebase-dynamic-links-ktx")

    // DataStore & ViewModel
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // Firebase BoM - Update ke versi terbaru untuk menghindari konflik
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))

    // Firebase dependencies (tanpa versi - diatur oleh BOM)
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-database-ktx")

    //API alamat
    implementation ("com.squareup.retrofit2:retrofit:2.11.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    //material
    implementation ("androidx.compose.ui:ui:1.8.2") // Jetpack Compose UI
    implementation ("androidx.compose.material:material:1.8.2") // Material Components untuk Compose
    implementation ("androidx.compose.material3:material3:1.3.2") // Material3 (jika Anda ingin menggunakannya)
    implementation ("androidx.compose.ui:ui-tooling-preview:1.8.2") // Untuk preview composable functions
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.9.1") // Untuk lifecycle-aware components
    implementation ("androidx.navigation:navigation-compose:2.4.0") // Untuk navigation di Compose
    implementation ("androidx.compose.material:material-icons-extended:1.7.8")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
    implementation ("com.github.bumptech.glide:compose:1.0.0-beta01")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.androidx.navigation.compose.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}