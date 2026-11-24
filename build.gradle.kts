import com.adarshr.gradle.testlogger.theme.ThemeType.MOCHA
import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode
import org.jmailen.gradle.kotlinter.tasks.FormatTask
import org.jmailen.gradle.kotlinter.tasks.LintTask

plugins {
    alias(libs.plugins.springboot)
    alias(libs.plugins.dependencyManagement)
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinPluginSpring)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.testLogger)
}

val javaVersion = libs.versions.java.get().toInt()

group = "org.eljabali.sami.todo"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.spring)
    implementation(libs.bundles.kotlin)

    // Starts the services of docker-compose.yml while the application runs in development.
    // It is not part of the production jar.
    developmentOnly(libs.dockerCompose)

    testImplementation(libs.bundles.springtest) {
        exclude(module = "mockito-core")
    }
    testImplementation(libs.bundles.kotlintest)
    testImplementation(libs.bundles.testcontainers)
    testRuntimeOnly(libs.junitPlatformLauncher)
}

kotlin {
    jvmToolchain(javaVersion)
    compilerOptions {
        jvmDefault = JvmDefaultMode.NO_COMPATIBILITY
        progressiveMode.set(true)
        freeCompilerArgs.addAll(
            "-Xjsr305=strict",
            "-Xannotation-default-target=param-property",
        )
        optIn.addAll(
            "kotlin.RequiresOptIn",
            "kotlinx.coroutines.ExperimentalCoroutinesApi",
        )
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    minHeapSize = "128m"
    maxHeapSize = "2048m"
}

tasks.withType<LintTask> {
    exclude { it.file.path.contains("build/generated") }
}

tasks.withType<FormatTask> {
    exclude { it.file.path.contains("build/generated") }
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    // disable plain jar alongside the executable jar
}

tasks.named<Jar>("jar") {
    enabled = false
}

kotlinter {
    ignoreLintFailures = false
    reporters = arrayOf("checkstyle", "plain")
}

testlogger {
    theme = MOCHA
    showExceptions = true
    showStackTraces = true
    showFullStackTraces = false
    showCauses = true
    slowThreshold = 2000
    showSummary = true
    showPassed = true
    showSkipped = true
    showFailed = true
    showStandardStreams = false
    logLevel = LogLevel.LIFECYCLE
}

springBoot {
    buildInfo()
}
