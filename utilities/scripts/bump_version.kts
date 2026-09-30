import java.io.File
import java.io.FileInputStream
import java.util.Properties

/**
 * bumps Android app version and generate Fastlane changelogs.
 */

// Argument Validation
require(args.size == 1) { "Usage: kotlinc -script bump_version.kts -- {major|minor|patch}" }
val bumpType = args.first()

// Path Configuration
val propertiesFilePath = "sudoq-app/gradle.properties"
val gradlePropertiesFile = File(propertiesFilePath)

val properties = Properties()
    .also { it.load(FileInputStream(gradlePropertiesFile)) }

// File Processing & Extraction
val currentVersionCode = properties.getProperty("appVersionCode")?.toInt()!!
val currentVersionName = properties.getProperty("appVersionName")!!
val (major, minor, patch) = currentVersionName.split('.').map(String::toInt)

// Version Bumping Logic
val nextVersionCode = currentVersionCode + 1
val nextVersionName = when (bumpType) {
    "major", "mj", "ma", "M" -> "${major + 1}.0.0"
    "minor", "mn", "mi", "m" -> "$major.${minor + 1}.0"
    "patch", "p"             -> "$major.$minor.${patch + 1}"
    else -> throw IllegalArgumentException("Invalid bump type: $bumpType")
}

println("Bumping: $currentVersionName ($currentVersionCode) -> $nextVersionName ($nextVersionCode)")

// Update properties - we have to edit, to preserve the order of the properties
var content = gradlePropertiesFile.readText()

content = content
    .replace("appVersionCode=$currentVersionCode", "appVersionCode=$nextVersionCode")
    .replace("appVersionName=$currentVersionName", "appVersionName=$nextVersionName")

gradlePropertiesFile.writeText(content)


// create changelog files for Fastlane and add to git
listOf("de-DE", "en-US", "fr-FR")
    .map { "fastlane/metadata/android/$it/changelogs/$nextVersionCode.txt" }
    .forEach { changelogFile ->
        File(changelogFile).createNewFile()
        ProcessBuilder("git", "add", changelogFile).start().waitFor()
    }

println("\nSuccessfully bumped version to $nextVersionName ($nextVersionCode)")
