plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    // Actualizado de 18.2.0 a 19.2.0 diego
    implementation("com.google.android.gms:play-services-maps:19.2.0")
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Se usan las referencias del Catálogo de Versiones (libs.*)
    testImplementation(libs.junit) // Reemplaza "junit:junit:4.13.2"
    androidTestImplementation(libs.androidx.junit) // Reemplaza "androidx.test.ext:junit:1.1.5"
    androidTestImplementation(libs.androidx.espresso.core) // Reemplaza "androidx.test.espresso:espresso-core:3.5.1"

    // Pruebas instrumentadas restantes (Actualizadas a las últimas versiones)
    androidTestImplementation("androidx.test:rules:1.7.0")
    androidTestImplementation("androidx.test:runner:1.7.0")

}