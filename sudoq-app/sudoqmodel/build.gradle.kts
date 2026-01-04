plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    //support for junit4 tests
    testImplementation(libs.junit)
    testRuntimeOnly(libs.junit.vintage.engine)

    testImplementation(project(":sudoq-persistence-xml"))
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.kluent)
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.agent.jvm)

    testImplementation(libs.apache.commons.io)
    testImplementation(libs.apache.commons.lang3)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

// Define the source sets for the different types of tests.
sourceSets {
    val main by getting
    val test by getting

    create("kotlintests") {
        kotlin.srcDir("src/test/kotlin")
        resources.srcDir("src/othertest/resources")
        compileClasspath += main.output + test.output
        runtimeClasspath += main.output + test.output
    }
    create("othertests") {
        java.srcDir("src/otherTests/java")
        kotlin.srcDir("src/otherTests/kotlin")
        resources.srcDir("src/otherTests/resources")
        compileClasspath += main.output + test.output
        runtimeClasspath += main.output + test.output
    }
    create("solvertests") {
        java.srcDir("src/solverTests/java")
        kotlin.srcDir("src/solverTests/kotlin")
        resources.srcDir("src/solverTests/resources")
        compileClasspath += main.output + test.output
        runtimeClasspath += main.output + test.output
    }
}

// Configure the custom configurations to inherit dependencies from the standard test configurations.
val testImplementation by configurations.getting
val testRuntimeOnly by configurations.getting

configurations.getByName("kotlintestsImplementation") { extendsFrom(testImplementation) }
configurations.getByName("kotlintestsRuntimeOnly") { extendsFrom(testRuntimeOnly) }

configurations.getByName("othertestsImplementation") { extendsFrom(testImplementation) }
configurations.getByName("othertestsRuntimeOnly") { extendsFrom(testRuntimeOnly) }

configurations.getByName("solvertestsImplementation") { extendsFrom(testImplementation) }
configurations.getByName("solvertestsRuntimeOnly") { extendsFrom(testRuntimeOnly) }


val kotlinTest by tasks.registering(Test::class) {
    testClassesDirs = sourceSets.getByName("kotlintests").output.classesDirs
    classpath = sourceSets.getByName("kotlintests").runtimeClasspath
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

val secondTest by tasks.registering(Test::class) {
    testClassesDirs = sourceSets.getByName("othertests").output.classesDirs
    classpath = sourceSets.getByName("othertests").runtimeClasspath
}

val solverTest by tasks.registering(Test::class) {
    testClassesDirs = sourceSets.getByName("solvertests").output.classesDirs
    classpath = sourceSets.getByName("solvertests").runtimeClasspath
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.check {
    dependsOn(secondTest)
    dependsOn(solverTest)
}
