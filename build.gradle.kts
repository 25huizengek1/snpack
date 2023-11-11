plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.roborazzi) apply false
}

allprojects {
    group = "me.huizengek.snpack"
    version = "1.0.0"

    repositories {
        mavenCentral()
        google()
    }
}