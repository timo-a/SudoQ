import java.io.File
import java.io.FileInputStream
import java.util.Properties

/**
 * create a release summary for google play console
 */

// Path Configuration
val propertiesFilePath = "sudoq-app/gradle.properties"
val gradlePropertiesFile = File(propertiesFilePath)
val properties = Properties()
    .also { it.load(FileInputStream(gradlePropertiesFile)) }

// File Processing & Extraction
val currentVersionCode = properties.getProperty("appVersionCode")!!

// add relevant files to git - kinda redundant but better safe than sorry
listOf("de-DE", "en-US", "fr-FR")
    .map { "fastlane/metadata/android/$it/changelogs/$currentVersionCode.txt" }
    .forEach { ProcessBuilder("git", "add", it).start().waitFor() }

fun readChangelog(locale: String): String {
    val changelogFile = "fastlane/metadata/android/$locale/changelogs/$currentVersionCode.txt"
    return File(changelogFile).readText()
}

println("""
    <en-US>
    ${readChangelog("en-US")}
    </en-US>
    <de-DE>
    ${readChangelog("de-DE")}
    </de-DE>
    <fr-FR>
    ${readChangelog("fr-FR")}
    </fr-FR>
""".trimIndent())