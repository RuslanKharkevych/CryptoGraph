plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kapt)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.secrets)
    id("io.objectbox")
}

android {
    namespace = "me.khruslan.cryptograph.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
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

    implementation(libs.coroutines)
    implementation(libs.koin.android)
    implementation(libs.okhttp)
    implementation(libs.okhttp.loggingInterceptor)
    implementation(libs.serializationJson)

    testImplementation(libs.coroutinesTest)
    testImplementation(libs.junit)
    testImplementation(libs.okhttp.mockWebServer)
    testImplementation(libs.truth)
    testImplementation(libs.turbine)
}

secrets {
    propertiesFileName = "secrets.properties"
}