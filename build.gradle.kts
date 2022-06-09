plugins {
    id("org.springframework.boot") version "2.7.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.7.0"
    kotlin("jvm") version "1.7.0"
    kotlin("plugin.spring") version "1.7.0"
}

group = "dev.d1s"
version = "0.8.0-beta.0"
java.targetCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io/")
    maven(url = "https://repo.spring.io/release")
}

val teabagsVersion: String by project
val kmLogVersion: String by project
val starterAdviceVersion: String by project
val starterSimpleSecurityVersion: String by project
val liquibaseVersion: String by project
val longPollingStarterVersion: String by project
val coroutinesVersion: String by project
//val striktVersion: String by project
//val springMockkVersion: String by project
val jsoupVersion: String by project
val kotlinxHtmlVersion: String by project

dependencies {
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib"))
    implementation("dev.d1s.teabags:teabag-spring-web:$teabagsVersion")
    implementation("dev.d1s.teabags:teabag-dto:$teabagsVersion")
    implementation("dev.d1s.teabags:teabag-stdlib:$teabagsVersion")
    implementation("org.lighthousegames:logging-jvm:$kmLogVersion")
    implementation("dev.d1s:spring-boot-starter-advice:$starterAdviceVersion")
    implementation("dev.d1s:spring-boot-starter-simple-security:$starterSimpleSecurityVersion")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-undertow")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("org.mariadb.jdbc:mariadb-java-client")
    implementation("org.liquibase:liquibase-core:$liquibaseVersion")
    implementation("dev.d1s.long-polling:spring-boot-starter-lp-server-web:$longPollingStarterVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jsoup:jsoup:$jsoupVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:$kotlinxHtmlVersion")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("dev.d1s.teabags:teabag-testing:$teabagsVersion")
    testImplementation("dev.d1s.teabags:teabag-testing-spring-web:$teabagsVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
//    testImplementation("com.ninja-squad:springmockk:$springMockkVersion")
//    testImplementation("io.strikt:strikt-jvm:$striktVersion")
}

configurations["implementation"].exclude(
    "org.springframework.boot",
    "spring-boot-starter-tomcat"
)

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()

    testLogging {
        events.addAll(
            setOf(
                org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
            )
        )
    }
}

sourceSets.getByName("test") {
    java.srcDir("./test")
}
