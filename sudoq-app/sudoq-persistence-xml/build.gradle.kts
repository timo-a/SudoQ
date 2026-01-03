plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
}

dependencies {
    implementation(project(":sudoqmodel"))
    implementation(kotlin("stdlib"))
}
