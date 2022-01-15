plugins {
    id("org.jetbrains.dokka") version "1.6.0"
    id("org.springframework.boot") version "2.6.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.6.10"
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
}

group = "uno.d1s"
version = "0.0.1-alpha.0"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io/")
    maven(url = "https://repo.spring.io/release")
}

extra["dokkaVersion"] = "1.6.0"
extra["springMockkVersion"] = "3.1.0"
extra["discordWebhooksVersion"] = "0.7.4"
extra["commonsIOVersion"] = "2.11.0"
extra["caffeineVersion"] = "3.0.5"
extra["springdocVersion"] = "1.6.3"
extra["liquibaseVersion"] = "4.6.2"
extra["starterCachingVersion"] = "1.0.0-stable.0"
extra["starterAdviceVersion"] = "1.0.1-stable.0"

dependencies {
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:${property("dokkaVersion")}")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springdoc:springdoc-openapi-ui:${property("springdocVersion")}")
    implementation("org.springdoc:springdoc-openapi-kotlin:${property("springdocVersion")}")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.github.d1snin:spring-boot-starter-caching:${property("starterCachingVersion")}")
    implementation("com.github.d1snin:spring-boot-starter-advice:${property("starterAdviceVersion")}")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql")
    implementation("org.liquibase:liquibase-core:${property("liquibaseVersion")}")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.github.ben-manes.caffeine:caffeine:${property("caffeineVersion")}")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.ninja-squad:springmockk:${property("springMockkVersion")}")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()

    testLogging {
        events.addAll(
            listOf(
                org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
            )
        )
    }
}