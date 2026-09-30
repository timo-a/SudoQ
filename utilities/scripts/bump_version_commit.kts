import java.io.File
import java.io.FileInputStream
import java.util.Properties

/**
 * Commit version bump with standard message.
 */

// Path Configuration
val propertiesFilePath = "sudoq-app/gradle.properties"
val gradlePropertiesFile = File(propertiesFilePath)

val properties = Properties()
    .also { it.load(FileInputStream(gradlePropertiesFile)) }

// File Processing & Extraction
val currentVersionCode = properties.getProperty("appVersionCode")!!
val currentVersionName = properties.getProperty("appVersionName")!!

// add relevant files to git - kinda redundant but better safe than sorry
listOf("de-DE", "en-US", "fr-FR")
    .map { "fastlane/metadata/android/$it/changelogs/$currentVersionCode.txt" }
    .forEach { ProcessBuilder("git", "add", it).start().waitFor() }

ProcessBuilder("git", "add", propertiesFilePath).start().waitFor()
ProcessBuilder("git", "commit", "-m", """Version bump: code: $currentVersionCode, name: $currentVersionName""").start().waitFor()
