import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.plugins.JavaPluginExtension

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
}

subprojects {
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
