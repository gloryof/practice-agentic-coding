buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.flywaydb:flyway-database-postgresql:13.12.1")
        classpath("org.postgresql:postgresql:42.7.14")
    }
}

plugins {
    kotlin("jvm") version "2.4.10"
    kotlin("plugin.spring") version "2.4.10"
    id("org.springframework.boot") version "4.1.2"
    id("io.spring.dependency-management") version "1.1.7"
    jacoco
    id("org.flywaydb.flyway") version "13.12.1"
    id("com.google.devtools.ksp") version "2.3.13"
    id("dev.detekt") version "2.0.0-alpha.6"
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
}

group = "jp.glory.practice.agentic"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

// Komapper 7が要求するcoroutine APIをSpring Bootの管理版で下げない。
extra["kotlin-coroutines.version"] = "1.11.0"
extra["flyway.version"] = "13.12.1"

val e2eTestSourceSet = sourceSets.create("e2eTest")
val architectureTestSourceSet =
    sourceSets.create("architectureTest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += output + compileClasspath
    }

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(27)
    }
    sourceCompatibility = JavaVersion.VERSION_24
    targetCompatibility = JavaVersion.VERSION_24
}

repositories {
    mavenCentral()
}

val komapperVersion = "7.0.0"
val testcontainersVersion = "2.0.5"
val flywayUrl =
    providers
        .gradleProperty("flywayUrl")
        .orElse(providers.environmentVariable("FLYWAY_URL"))
        .orElse("jdbc:postgresql://localhost:5432/agentic")
val flywayUser =
    providers
        .gradleProperty("flywayUser")
        .orElse(providers.environmentVariable("FLYWAY_USER"))
        .orElse("agentic")
val flywayPassword =
    providers
        .gradleProperty("flywayPassword")
        .orElse(providers.environmentVariable("FLYWAY_PASSWORD"))
        .orElse("agentic")

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.2.0")
    implementation("org.flywaydb:flyway-core")
    implementation("org.postgresql:postgresql")

    implementation("org.komapper:komapper-spring-boot-starter-jdbc:$komapperVersion")
    implementation("org.komapper:komapper-dialect-postgresql-jdbc:$komapperVersion")
    ksp("org.komapper:komapper-processor:$komapperVersion")

    implementation("io.konform:konform-jvm:0.11.1")
    implementation("com.michael-bull.kotlin-result:kotlin-result-jvm:2.3.1")
    implementation("org.springframework.security:spring-security-crypto")

    testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers:$testcontainersVersion")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter:$testcontainersVersion")
    testImplementation("org.testcontainers:testcontainers-postgresql:$testcontainersVersion")
    testImplementation("io.mockk:mockk:1.14.12")
    testImplementation("com.github.f4b6a3:uuid-creator:6.1.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    add("architectureTestImplementation", "com.tngtech.archunit:archunit-junit5:1.5.1")
    add("architectureTestImplementation", "org.jetbrains.kotlin:kotlin-test-junit5")
    add("architectureTestRuntimeOnly", "org.junit.platform:junit-platform-launcher")

    add("e2eTestImplementation", "org.springframework.boot:spring-boot-starter-actuator-test")
    add("e2eTestImplementation", "org.jetbrains.kotlin:kotlin-test-junit5")
    add("e2eTestImplementation", "io.rest-assured:rest-assured:6.3.0")
    add("e2eTestRuntimeOnly", "org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_24)
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(24)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

configurations.named("e2eTestImplementation") {
    extendsFrom(configurations.testImplementation.get())
}

configurations.named("e2eTestRuntimeOnly") {
    extendsFrom(configurations.testRuntimeOnly.get())
}

configurations.named("architectureTestImplementation") {
    extendsFrom(configurations.implementation.get())
}

configurations.named("architectureTestRuntimeOnly") {
    extendsFrom(configurations.runtimeOnly.get())
}

tasks.register<Test>("e2eTest") {
    group = "verification"
    description = "Runs end-to-end tests against a running API."
    testClassesDirs = e2eTestSourceSet.output.classesDirs
    classpath = e2eTestSourceSet.runtimeClasspath
    shouldRunAfter(tasks.test)
    useJUnitPlatform()
}

val architectureTest =
    tasks.register<Test>("architectureTest") {
        group = "verification"
        description = "Runs architecture dependency rules without external services."
        testClassesDirs = architectureTestSourceSet.output.classesDirs
        classpath = architectureTestSourceSet.runtimeClasspath
        shouldRunAfter(tasks.test)
        useJUnitPlatform()
    }

val jacocoExcludes =
    listOf(
        "**/infra/adapter/persistence/_*",
        "**/infra/adapter/persistence/table/_*",
    )

fun org.gradle.testing.jacoco.tasks.JacocoCoverageVerification.coverageFor(
    includes: List<String>,
    excludes: List<String>,
    minimum: String,
) {
    dependsOn(tasks.test)
    classDirectories.setFrom(
        sourceSets.main.get().output.classesDirs.map { directory ->
            fileTree(directory) {
                include(includes)
                exclude(jacocoExcludes + excludes)
            }
        },
    )
    violationRules {
        rule {
            element = "BUNDLE"
            limit {
                counter = "INSTRUCTION"
                value = "COVEREDRATIO"
                this.minimum = minimum.toBigDecimal()
            }
        }
    }
}

tasks.jacocoTestCoverageVerification {
    coverageFor(listOf("**/*.class"), listOf("**/domain/**", "**/usecase/**"), "0.8")
}

val jacocoDomainCoverageVerification =
    tasks.register<org.gradle.testing.jacoco.tasks.JacocoCoverageVerification>("jacocoDomainCoverageVerification") {
        executionData(layout.buildDirectory.file("jacoco/test.exec"))
        coverageFor(listOf("**/domain/**"), emptyList(), "1.0")
    }

val jacocoUsecaseCoverageVerification =
    tasks.register<org.gradle.testing.jacoco.tasks.JacocoCoverageVerification>("jacocoUsecaseCoverageVerification") {
        executionData(layout.buildDirectory.file("jacoco/test.exec"))
        coverageFor(listOf("**/usecase/**"), emptyList(), "1.0")
    }

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    val jacocoExcludes =
        listOf(
            "**/infra/adapter/persistence/_*",
            "**/infra/adapter/persistence/table/_*",
        )
    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) {
                    exclude(jacocoExcludes)
                }
            },
        ),
    )
    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)
    }
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom("config/detekt/detekt.yml")
    source.setFrom("src/main/kotlin")
}

ktlint {
    filter {
        include("src/**/*.kt")
        include("*.kts")
    }
}

tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.KtLintCheckTask>().configureEach {
    exclude("**/build/generated/**")
}

tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.KtLintFormatTask>().configureEach {
    exclude("**/build/generated/**")
}

tasks.named<org.jlleitschuh.gradle.ktlint.tasks.KtLintCheckTask>("runKtlintCheckOverMainSourceSet") {
    setSource(fileTree("src/main/kotlin") { include("**/*.kt") })
}

tasks.named<org.jlleitschuh.gradle.ktlint.tasks.KtLintFormatTask>("runKtlintFormatOverMainSourceSet") {
    setSource(fileTree("src/main/kotlin") { include("**/*.kt") })
}

tasks.named<org.jlleitschuh.gradle.ktlint.tasks.KtLintCheckTask>("runKtlintCheckOverTestSourceSet") {
    setSource(fileTree("src/test/kotlin") { include("**/*.kt") })
}

tasks.named<org.jlleitschuh.gradle.ktlint.tasks.KtLintFormatTask>("runKtlintFormatOverTestSourceSet") {
    setSource(fileTree("src/test/kotlin") { include("**/*.kt") })
}

tasks.named("check") {
    dependsOn(
        "detekt",
        "ktlintCheck",
        "jacocoTestCoverageVerification",
        jacocoDomainCoverageVerification,
        jacocoUsecaseCoverageVerification,
        "jacocoTestReport",
        architectureTest,
    )
}

flyway {
    url = flywayUrl.get()
    user = flywayUser.get()
    password = flywayPassword.get()
    locations = arrayOf("filesystem:src/main/resources/db/migration")
}
