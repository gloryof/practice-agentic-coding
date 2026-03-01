buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.flywaydb:flyway-database-postgresql:11.15.0")
        classpath("org.postgresql:postgresql:42.7.9")
    }
}

plugins {
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.spring") version "2.2.20"
    id("org.springframework.boot") version "4.0.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.flywaydb.flyway") version "11.15.0"
    id("com.google.devtools.ksp") version "2.2.20-2.0.3"
    id("dev.detekt") version "2.0.0-alpha.1"
}

group = "jp.glory.practice.agentic"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(24)
    }
}

repositories {
    mavenCentral()
}

val komapperVersion = "6.0.0"
val flywayUrl = providers.gradleProperty("flywayUrl")
    .orElse(providers.environmentVariable("FLYWAY_URL"))
    .orElse("jdbc:postgresql://localhost:5432/agentic")
val flywayUser = providers.gradleProperty("flywayUser")
    .orElse(providers.environmentVariable("FLYWAY_USER"))
    .orElse("agentic")
val flywayPassword = providers.gradleProperty("flywayPassword")
    .orElse(providers.environmentVariable("FLYWAY_PASSWORD"))
    .orElse("agentic")

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.1")
    implementation("org.flywaydb:flyway-core")
    implementation("org.postgresql:postgresql")

    implementation("org.komapper:komapper-spring-boot-starter-jdbc:$komapperVersion")
    implementation("org.komapper:komapper-dialect-postgresql-jdbc:$komapperVersion")
    ksp("org.komapper:komapper-processor:$komapperVersion")

    implementation("io.konform:konform-jvm:0.11.0")
    implementation("com.michael-bull.kotlin-result:kotlin-result-jvm:2.1.0")
    implementation("org.springframework.security:spring-security-crypto")

    testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter:1.20.4")
    testImplementation("org.testcontainers:postgresql:1.20.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom("config/detekt/detekt.yml")
    source.setFrom("src/main/kotlin")
}

tasks.named("check") {
    dependsOn("detekt")
}

flyway {
    url = flywayUrl.get()
    user = flywayUser.get()
    password = flywayPassword.get()
    locations = arrayOf("filesystem:src/main/resources/db/migration")
}
