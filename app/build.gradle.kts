import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    alias(libs.plugins.ksp)
    
    // 🔥 Google Services para Firebase
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.spacius"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.spacius"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // 🔒 Leer Google Maps API Key desde local.properties de forma segura
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            FileInputStream(localPropertiesFile).use { localProperties.load(it) }
        }
        
        val mapsApiKey = localProperties.getProperty("MAPS_API_KEY") ?: System.getenv("MAPS_API_KEY") ?: ""
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
        
        // Verificar que la API Key está configurada
        if (mapsApiKey.isEmpty()) {
            logger.warn("⚠️ MAPS_API_KEY no encontrada. Configúrala en local.properties")
        }
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
        viewBinding = true
    }
}

dependencies {

    // --- AndroidX & UI ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

