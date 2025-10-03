import java.net.URL

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.ms.lcw"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ms.lcwsamplapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
    }
    packaging {
        pickFirst("**/*.so")
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // Debug specific settings, e.g., enable minification for faster debug builds if needed
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

repositories {
    google()
    mavenCentral()
    // Local 'libs' directory for AARs placed manually
    flatDir {
        dirs("libs")
    }
}

dependencies {
    implementation(files("libs/OmnichannelChatSDK.aar"))
    implementation(files("libs/ContactCenterMessagingWidget.aar"))
    implementation(libs.react.android)
    implementation(libs.jsc.android)
    // Google Flexbox Layout
    implementation(libs.flexbox)

    // Adaptive Cards (if used in your pure Android app)
    implementation (libs.adaptivecards.android)
    implementation(libs.gson)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Kotlin Coroutines (often used with AndroidX libraries)
    implementation(libs.kotlinx.coroutines.android)

    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}