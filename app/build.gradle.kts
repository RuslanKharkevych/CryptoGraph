plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.firebaseCrashlytics)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "me.khruslan.cryptograph"
    compileSdk = 34

    defaultConfig {
        applicationId = "me.khruslan.cryptograph"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resourceConfigurations.add("en")
    }

    buildTypes {
        debug {
            versionNameSuffix = "-DEBUG"
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":base"))
    implementation(project(":data"))
    implementation(project(":ui"))

    implementation(libs.coil.compose)
    coreLibraryDesugaring(libs.desugaring)
    implementation(libs.firebase.analytics)
    implementation(libs.koin.android)
    implementation(libs.koin.workManager)
    implementation(libs.material)
}