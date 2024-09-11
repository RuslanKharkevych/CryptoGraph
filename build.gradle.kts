buildscript {
    dependencies {
        classpath(libs.objectbox)
    }
}

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kapt) apply false
    alias(libs.plugins.kotlinAndroid) apply false
}