import com.android.build.api.dsl.ApplicationExtension

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.sonar)
    jacoco
}

sonar {
    properties {
        property("sonar.projectKey", "timo-a_SudoQ")
        property("sonar.organization", "timo-a")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.projectVersion", project.property("appVersionName").toString())

        // Fix for "File can't be indexed twice" error with
        // src/main/res/layouts/sudoku/layout-land/sudoku.xml
        // by excluding all XML files
        property("sonar.exclusions", "**/src/main/res/layouts/**/*.xml, **/src/main/res/layout/**/*.xml")
    }
}

subprojects {
    apply(plugin = "jacoco")

    configure<JacocoPluginExtension> {
        toolVersion = "0.8.12"
    }

    sonar {
        properties {
            plugins.withId("com.android.application") {
                property("sonar.android.buildVariant", "debug")
                property("sonar.coverage.jacoco.xmlReportPaths", "${project.layout.buildDirectory.get().asFile}/reports/coverage/test/debug/report.xml")
                property("sonar.junit.reportPaths", "${project.layout.buildDirectory.get().asFile}/test-results/testDebugUnitTest")
                property("sonar.androidLint.reportPaths", "${project.layout.buildDirectory.get().asFile}/reports/lint-results-debug.xml")
            }
            plugins.withId("java-library") {
                property("sonar.coverage.jacoco.xmlReportPaths", "${project.layout.buildDirectory.get().asFile}/reports/jacoco/test/jacocoTestReport.xml")
                property("sonar.junit.reportPaths", "${project.layout.buildDirectory.get().asFile}/test-results/test")
            }
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }


    // Configure Java for the app itself. There are no java classes in main but we keep it just in case
    plugins.withId("com.android.application") {
        extensions.configure<ApplicationExtension> {
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
        }
    }

    // Configure Java for the libraries model and xml
    plugins.withId("java-library") {
        extensions.configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }


    // display deprecation warnings on compilation
    tasks.withType<JavaCompile> {
        options.compilerArgs.add("-Xlint:deprecation")
    }

}
