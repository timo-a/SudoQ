import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties

/**
 * Increments the version code by one, renames the Fastlane changelog files.
 */

// Path Configuration
val propertiesFilePath = "sudoq-app/gradle.properties"
val gradlePropertiesFile = File(propertiesFilePath)

val properties = Properties()
    .also { it.load(FileInputStream(gradlePropertiesFile)) }

// File Processing & Extraction
val currentVersionCode = properties.getProperty("appVersionCode")?.toInt()!!

val nextVersionCode = currentVersionCode + 1

println("Bumping: $currentVersionCode -> $nextVersionCode")

// Update properties
var content = gradlePropertiesFile.readText()
content = content.replace("appVersionCode=$currentVersionCode", "appVersionCode=$nextVersionCode")
gradlePropertiesFile.writeText(content)

// bump changelogs
listOf("de-DE", "en-US", "fr-FR")
    .map { "fastlane/metadata/android/$it/changelogs" }
    .forEach { path ->
        ProcessBuilder("git", "mv", "$path/${currentVersionCode}.txt", "$path/${nextVersionCode}.txt")
            .start().waitFor()
    }

println("\nSuccessfully bumped version to ($nextVersionCode)")
