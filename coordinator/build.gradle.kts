plugins {
    kotlin("jvm") version "2.0.0"
    `maven-publish`
}

val artifactName = "coordinator"
val artifactVersion = "1.0-SNAPSHOT"

group = "com.github.ngnhub"
version = artifactVersion

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = "$group"
            artifactId = artifactName
            version = artifactVersion
        }
    }
    repositories {
        mavenLocal()
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

// versions
val consistentHashVersion = "1.0-SNAPSHOT"
val coroutinesVersion = "1.9.0"
val mockVersion = "5.4.0"
val slf4jVersion = "2.0.16"
val loggerVersion = "7.0.0"

dependencies {
    api("com.github.ngnhub:consistent_hash:$consistentHashVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

    // logging
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("io.github.oshai:kotlin-logging-jvm:$loggerVersion")

    testImplementation(kotlin("test"))
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        events("passed", "failed")
    }
}

java {
    withSourcesJar()
}

kotlin {
    jvmToolchain(21)
}
