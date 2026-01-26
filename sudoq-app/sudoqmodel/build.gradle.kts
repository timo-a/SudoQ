plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
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

val testImplementation by configurations.getting
val testRuntimeOnly by configurations.getting

sourceSets {
    val main by getting
    val test by getting

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

configurations {
    getByName("othertestsImplementation") {
        extendsFrom(testImplementation.get())
    }
    getByName("solvertestsImplementation") {
        extendsFrom(testImplementation.get())
    }

    getByName("othertestsRuntimeOnly") {
        extendsFrom(testRuntimeOnly.get())
    }
    getByName("solvertestsRuntimeOnly") {
        extendsFrom(testRuntimeOnly.get())
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
