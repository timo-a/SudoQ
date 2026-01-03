plugins {
    `java-library`
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation(project(":sudoqmodel"))
    implementation(kotlin("stdlib"))
}
