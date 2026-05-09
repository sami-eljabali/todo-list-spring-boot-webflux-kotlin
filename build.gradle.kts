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
    implementation(libs.starterWebflux)
    implementation(libs.starterValidation)
    implementation(libs.starterSecurity)
    implementation(libs.starterActuator)
    implementation(libs.starterDataR2dbc)
    implementation(libs.r2dbcPostgresql)
    implementation(libs.springDoc)
    implementation(libs.bundles.kotlinSupport)

    testImplementation(libs.bundles.testcore) {
        exclude(module = "mockito-core")
    }
    testImplementation(libs.bundles.testcontainers)
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.bundles.mockk)
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
    filter { include("**/*Test.class") }
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
