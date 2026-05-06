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

sourceSets {
    val commonTest by creating {
        kotlin {
            srcDir("$projectDir/src/commonTest/kotlin")
            compileClasspath += sourceSets["main"].output
            runtimeClasspath += sourceSets["main"].output
        }
        resources.srcDir("$projectDir/src/commonTest/resources")
    }

    test {
        kotlin {
            compileClasspath += sourceSets["commonTest"].output
            runtimeClasspath += sourceSets["commonTest"].output
        }
    }

    val controllerTest by creating {
        kotlin {
            compileClasspath += sourceSets["main"].output
            runtimeClasspath += sourceSets["main"].output
            compileClasspath += sourceSets["commonTest"].output
            runtimeClasspath += sourceSets["commonTest"].output
            srcDirs("$projectDir/src/controllerTest/kotlin")
        }
        resources.srcDir("$projectDir/src/controllerTest/resources")
    }

    val repositoryTest by creating {
        kotlin {
            compileClasspath += sourceSets["main"].output
            runtimeClasspath += sourceSets["main"].output
            compileClasspath += sourceSets["commonTest"].output
            runtimeClasspath += sourceSets["commonTest"].output
            srcDirs("$projectDir/src/repositoryTest/kotlin")
        }
        resources.srcDir("$projectDir/src/repositoryTest/resources")
    }

    val integrationTest by creating {
        kotlin {
            compileClasspath += sourceSets["main"].output
            runtimeClasspath += sourceSets["main"].output
            compileClasspath += sourceSets["commonTest"].output
            runtimeClasspath += sourceSets["commonTest"].output
            srcDirs("$projectDir/src/integrationTest/kotlin")
        }
        resources.srcDir("$projectDir/src/integrationTest/resources")
    }
}

configurations {
    val commonTestImplementation by getting {
        extendsFrom(configurations["testImplementation"])
    }
    val controllerTestImplementation by getting {
        extendsFrom(configurations["testImplementation"], configurations["commonTestImplementation"])
    }
    val repositoryTestImplementation by getting {
        extendsFrom(configurations["testImplementation"], configurations["commonTestImplementation"])
    }
    val integrationTestImplementation by getting {
        extendsFrom(configurations["testImplementation"], configurations["commonTestImplementation"])
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    filter { include("**/*Test.class") }
    minHeapSize = "128m"
    maxHeapSize = "2048m"
}

val controllerTest by tasks.registering(Test::class) {
    description = "Runs REST API tests."
    group = "verification"
    testClassesDirs = sourceSets["controllerTest"].output.classesDirs
    classpath = sourceSets["controllerTest"].runtimeClasspath
    useJUnitPlatform()
    filter { include("**/*ControllerTest.class") }
}

val repositoryTest by tasks.registering(Test::class) {
    description = "Runs repository tests."
    group = "verification"
    testClassesDirs = sourceSets["repositoryTest"].output.classesDirs
    classpath = sourceSets["repositoryTest"].runtimeClasspath
    useJUnitPlatform()
    filter { include("**/*RepositoryTest.class") }
}

val integrationTest by tasks.registering(Test::class) {
    description = "Runs integration tests."
    group = "verification"
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    useJUnitPlatform()
    filter { include("**/*IntegrationTest.class") }
}

tasks.named<ProcessResources>("processControllerTestResources") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.named<ProcessResources>("processRepositoryTestResources") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.named<ProcessResources>("processIntegrationTestResources") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.check.configure {
    dependsOn(controllerTest, repositoryTest, integrationTest)
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
