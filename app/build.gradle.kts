import com.android.build.api.dsl.ApplicationBuildType

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.roborazzi)
}

android {
    val id = project.group.toString()

    namespace = id
    compileSdk = 34

    fun ApplicationBuildType.configureProviders() {
        val contentProviderAuthority = "$id${applicationIdSuffix.orEmpty()}.stickercontentprovider"
        manifestPlaceholders["contentProviderAuthority"] = contentProviderAuthority
        buildConfigField(
            type = "String",
            name = "CONTENT_PROVIDER_AUTHORITY",
            value = "\"${contentProviderAuthority}\""
        )
    }

    defaultConfig {
        applicationId = id
        minSdk = 24
        targetSdk = 34
        versionCode = 3
        versionName = project.version.toString()

        vectorDrawables.useSupportLibrary = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions.unitTests.isIncludeAndroidResources = true

    buildTypes {
        val label = "snpack"

        debug {
            versionNameSuffix = "-DEBUG"
            manifestPlaceholders["appName"] = "$label debug"
            applicationIdSuffix = ".debug"

            configureProviders()
        }
        release {
            isMinifyEnabled = false
            versionNameSuffix = "-RELEASE"
            manifestPlaceholders["appName"] = label
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            configureProviders()
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

    implementation(projects.backup)

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.ktx)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.activity)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.material3)

    implementation(libs.kotlinx.immutable)

    implementation(libs.room)
    implementation(libs.room.ktx)
    ksp(libs.room.ksp)

    implementation(libs.destinations)
    ksp(libs.destinations.ksp)

    implementation(libs.slf4j.android)

    implementation(libs.glide)
    implementation(libs.glide.compose)
    ksp(libs.glide.ksp)

    implementation(libs.color.picker)

    detektPlugins(libs.detekt.compose)
    detektPlugins(libs.detekt.formatting)

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