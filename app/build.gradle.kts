plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.roborazzi)
}

android {
    namespace = project.group.toString()
    compileSdk = 34

    defaultConfig {
        applicationId = project.group.toString()
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = project.version.toString()

        vectorDrawables.useSupportLibrary = true

        val contentProviderAuthority = "$applicationId.stickercontentprovider"
        manifestPlaceholders["contentProviderAuthority"] = contentProviderAuthority
        buildConfigField(
            type = "String",
            name = "CONTENT_PROVIDER_AUTHORITY",
            value = "\"${contentProviderAuthority}\""
        )

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions.unitTests.isIncludeAndroidResources = true

    buildTypes {
        val label = "snpack"
        debug {
            versionNameSuffix = "-DEBUG"
            manifestPlaceholders["appName"] = "$label debug"
        }
        release {
            isMinifyEnabled = false
            versionNameSuffix = "-RELEASE"
            manifestPlaceholders["appName"] = label
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf("-Xcontext-receivers")
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    coreLibraryDesugaring(libs.desugaring)

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(libs.activity.ktx)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.material3)

    implementation(libs.room)
    implementation(libs.room.ktx)
    ksp(libs.room.ksp)

    implementation(libs.destinations)
    ksp(libs.destinations.ksp)

    implementation(libs.slf4j.android)

    implementation(libs.glide)
    implementation(libs.glide.compose)

    implementation(libs.color.picker)

    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.junit)
    testImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.test.manifest)
}

ksp {
    arg("room.schemaLocation", projectDir.resolve("src/main/room/schemas").absolutePath)
}