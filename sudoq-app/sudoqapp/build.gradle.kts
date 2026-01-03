plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "de.sudoq"
    compileSdk = 34

    defaultConfig {
        applicationId = "de.sudoq"
        minSdk = 19
        targetSdk = 34
        multiDexEnabled = true
        resConfigs("en", "de", "fr")
        testApplicationId = "de.sudoq.test"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    implementation("com.google.android.material:material:1.12.0")
    //implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.multidex:multidex:2.0.1")
    //implementation("androidx.recyclerview:recyclerview:1.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.4.0")
    testImplementation("org.amshove.kluent:kluent:1.67")
    testImplementation("io.mockk:mockk:1.9")
    testImplementation(project(mapOf("path" to ":sudoq-persistence-xml")))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.0")

    //androidTestImplementation("com.android.support.test.espresso:espressocore:3.0.2")

    //compile("com.android.support:support-v4:21.0.0")
    //androidTestImplementation("com.jayway.android.robotium:robotium-solo:5.4.1")
    //androidTestImplementation(files("libs/robotium-solo-4.1.jar"))
}
