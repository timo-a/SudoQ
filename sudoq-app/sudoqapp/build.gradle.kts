plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "de.sudoq"
    compileSdk = 34

    defaultConfig {
        applicationId = "de.sudoq"
        minSdk = 21
        targetSdk = 34
        multiDexEnabled = true
        testApplicationId = "de.sudoq.test"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    androidResources {
        localeFilters.addAll(listOf("en", "de", "fr"))
    }

    sourceSets {
        getByName("main") {
            // this allows us to group resources(layouts, values) by topic
            res.srcDirs(
                "src/main/res/layouts/sudoku",
                "src/main/res/layouts/tutorial",
                "src/main/res/layout",
                "src/main/res",
                "src/main/res-screen/hints/",
                "src/main/res-screen/main_menu/",
                "src/main/res-screen/preferences/"
            )
        }
    }

    buildTypes {
        getByName("release") {
            //isMinifyEnabled = true
            //proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(project(":sudoqmodel"))
    implementation(libs.material)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.multidex)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.kluent)
    testImplementation(libs.mockk)
    testImplementation(project(mapOf("path" to ":sudoq-persistence-xml")))
    testRuntimeOnly(libs.junit.jupiter.engine)
}
