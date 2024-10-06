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
    compileSdk = 35
    resourcePrefix = "data_"

    defaultConfig {
        minSdk = 23
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":base"))

    implementation(libs.coroutines)
    coreLibraryDesugaring(libs.desugaring)
    implementation(libs.firebase.config)
    implementation(libs.koin.android)
    implementation(libs.koin.workManager)
    implementation(libs.okhttp)
    implementation(libs.okhttp.loggingInterceptor)
    implementation(libs.serializationJson)
    implementation(libs.workRuntime)

    testImplementation(libs.coroutinesTest)
    testImplementation(libs.junit)
    testImplementation(libs.okhttp.mockWebServer)
    testImplementation(libs.truth)
    testImplementation(libs.turbine)
}

secrets {
    propertiesFileName = "secrets.properties"
}