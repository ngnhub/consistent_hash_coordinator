plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
    `maven-publish`
}

group = "com.github.ngnhub"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

// version
val coordinatorVersion = "1.0-SNAPSHOT"
val slf4jVersion = "2.0.16"
val loggerVersion = "7.0.0"
val redisConnectorVersion = "5.2.0"
val openAPIVersion = "2.6.0"

repositories {
    mavenLocal()
    mavenCentral()
}


dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("redis.clients:jedis:$redisConnectorVersion") // todo version to var
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // own
    implementation("com.github.ngnhub:coordinator:$coordinatorVersion")

    // logging
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("io.github.oshai:kotlin-logging-jvm:$loggerVersion")

    // doc
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$openAPIVersion")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("com.github.codemonstur:embedded-redis:1.4.2")
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<Test> {
    useJUnitPlatform()

    testLogging {
        events("passed", "failed")
    }
}
