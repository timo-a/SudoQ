plugins {
    `java-library`
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    //support for junit4 tests
    implementation("junit:junit:4.12") //todo switch to junit5 entirely
    testImplementation("org.junit.vintage:junit-vintage-engine:5.4.0")

    testImplementation(project(":sudoq-persistence-xml"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.4.0")
    testImplementation("org.amshove.kluent:kluent:1.67")
    testImplementation("io.mockk:mockk:1.9")
    testImplementation("io.mockk:mockk-agent-jvm:1.13.8")

    testImplementation("org.apache.directory.studio:org.apache.commons.io:2.4")
    testImplementation("org.apache.commons:commons-lang3:3.12.0")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

val testImplementation by configurations.getting

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

configurations {
    getByName("kotlintestsImplementation") {
        extendsFrom(testImplementation.get())
    }
    getByName("othertestsImplementation") {
        extendsFrom(testImplementation.get())
    }
    getByName("solvertestsImplementation") {
        extendsFrom(testImplementation.get())
    }
}

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
