import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "de.sudoq"
    compileSdk = 36

    defaultConfig {
        applicationId = "de.sudoq"
        versionCode = project.property("appVersionCode").toString().toInt()
        versionName = project.property("appVersionName").toString()

        minSdk = 21
        targetSdk = 36
        testApplicationId = "de.sudoq.test"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    androidResources {
        localeFilters.addAll(listOf("en", "de", "fr"))
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }

    sourceSets {
        getByName("main") {
            // this allows us to group resources(layouts, values) by topic
            res.srcDirs(
                "src/main/res/layouts/sudoku",
                "src/main/res/layouts/tutorial",
                "src/main/res/layout",
                "src/main/res-screen/hints/",
                "src/main/res-screen/main_menu/",
                "src/main/res-screen/preferences/"
            )
        }
    }

    signingConfigs {
        create("localRelease")
    }

    buildTypes {
        getByName("debug") {
            enableUnitTestCoverage = true
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

val isLocalSignedReleaseRequested = gradle.startParameter.taskNames.any { it.contains("localSignedRelease") }
if (isLocalSignedReleaseRequested) {
    val keystorePropertiesFile = rootProject.file("keystore.properties")
    require(keystorePropertiesFile.exists()) {
        "keystore.properties file not found at ${keystorePropertiesFile.absolutePath}"
    }

    val keystoreProperties = Properties().also { it.load(keystorePropertiesFile.inputStream()) }
    val storeFilePath = keystoreProperties.getProperty("KEYSTORE_FILE")
    require(!storeFilePath.isNullOrBlank()) { "KEYSTORE_FILE is missing in keystore.properties" }

    val storeFileObj = file(storeFilePath)
    require(storeFileObj.exists()) { "Keystore file not found at ${storeFileObj.absolutePath}" }

    val keyAliasVal = keystoreProperties.getProperty("KEY_ALIAS")
    val storePasswordVal = requireNotNull(System.getenv("KEYSTORE_PASSWORD")) {
        "KEYSTORE_PASSWORD environment variable is required for localSignedRelease"
    }
    val keyPasswordVal = requireNotNull(System.getenv("KEY_PASSWORD")) {
        "KEY_PASSWORD environment variable is required for localSignedRelease"
    }

    android.signingConfigs.getByName("localRelease").apply {
        storeFile = storeFileObj
        keyAlias = keyAliasVal
        storePassword = storePasswordVal
        keyPassword = keyPasswordVal
    }

    android.buildTypes.getByName("release").signingConfig = android.signingConfigs.getByName("localRelease")
}

base {
    archivesName.set("sudoqapp-${project.property("appVersionName")}")
}

tasks.register("localSignedRelease") {
    group = "publishing"
    description = "Builds a signed release bundle locally using keystore.properties and environment variables."
    dependsOn("bundleRelease")
}

dependencies {
    implementation(project(":sudoqmodel"))
    implementation(libs.material)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.kluent)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
}
