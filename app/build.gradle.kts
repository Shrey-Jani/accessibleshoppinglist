plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.gms.google-services")
}

android {
    namespace = "week11.st910491.finalproject"
    compileSdk = 36

    defaultConfig {
        applicationId = "week11.st910491.finalproject"
        minSdk = 25
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("com.google.firebase:firebase-analytics")
    implementation(platform("com.google.firebase:firebase-bom:34.6.0"))
    // Authentication (no version because BoM manages it)
    implementation("com.google.firebase:firebase-auth")

    // Coroutines Tasks support (for .await())
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")
    implementation("androidx.navigation:navigation-compose:2.8.3")
    implementation("com.google.firebase:firebase-firestore")
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.runtime.saveable)
    implementation(libs.androidx.compose.foundation)
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.foundation.layout)
//    implementation(libs.firebase.firestore.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)


    implementation("androidx.compose.material:material-icons-extended")


}